package com.proyecto.movilibre

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.movilibre.componentes.BtnVolver
import com.proyecto.movilibre.componentes.PasswInput // Reutilizamos PasswInput
import com.proyecto.movilibre.R // Asegúrate de que este R sea el correcto de tu proyecto

@Composable
fun CambiarContrasenaView(navController: NavHostController) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isCurrentPasswordValid by remember { mutableStateOf(false) } // Para la validación de PasswInput
    var isNewPasswordValid by remember { mutableStateOf(false) }
    var currentPasswordErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    var newPasswordErrors by remember { mutableStateOf<List<String>>(emptyList()) }


    if (user == null) {
        // Esto no debería pasar si el botón solo se muestra cuando el usuario está logueado
        // Pero es una buena práctica manejar el caso.
        LaunchedEffect(Unit) {
            Toast.makeText(context, "No hay usuario autenticado.", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // O navegar a login
        }
        return // Salir del Composable si no hay usuario
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally
    )   {
        Text(
            text = stringResource(id = R.string.title_CambiarContrasena),
            color = colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Ingresa tu contraseña actual y la nueva contraseña.",
            color = colorScheme.onBackground,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Contraseña actual.",
            color = colorScheme.onBackground,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Campo para la contraseña actual
        PasswInput(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            onValidationChange = { isValid, errors ->
                isCurrentPasswordValid = isValid
                currentPasswordErrors = errors
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nueva contraseña.",
            color = colorScheme.onBackground,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Campo para la nueva contraseña
        PasswInput(
            value = newPassword,
            onValueChange = { newPassword = it },
            onValidationChange = { isValid, errors ->
                isNewPasswordValid = isValid
                newPasswordErrors = errors
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Repetir nueva contraseña.",
            color = colorScheme.onBackground,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Campo para confirmar la nueva contraseña
        PasswInput(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            onValidationChange = { _, _ -> /* No necesitamos validación de reglas aquí, solo que coincida */ }
        )
        // Mensaje de error si las contraseñas no coinciden
        if (newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty() && newPassword != confirmNewPassword) {
            Text(
                text = "Las nuevas contraseñas no coinciden.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para cambiar contraseña
        Button(
            onClick = {
                if (newPassword != confirmNewPassword) {
                    Toast.makeText(context, "Las nuevas contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!isCurrentPasswordValid || !isNewPasswordValid) {
                    Toast.makeText(context, "Por favor, corrige los errores de contraseña.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    isLoading = false
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(context, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // Volver a la pantalla anterior
                                    } else {
                                        Toast.makeText(context, "Error al actualizar contraseña: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Error de autenticación: Contraseña actual incorrecta.", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            enabled = !isLoading && currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty() && newPassword == confirmNewPassword && isCurrentPasswordValid && isNewPasswordValid,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Cambiar Contraseña", color = colorScheme.onPrimary, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Volver
        BtnVolver(onClick = { navController.popBackStack() })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCambiarContrasenaView() {
    CambiarContrasenaView(navController = rememberNavController())
}