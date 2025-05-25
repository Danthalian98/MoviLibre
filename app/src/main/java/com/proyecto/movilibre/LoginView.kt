package com.proyecto.movilibre

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.movilibre.componentes.BtnVolver
import com.proyecto.movilibre.componentes.CorreoInput
import com.proyecto.movilibre.componentes.PasswInput
import com.proyecto.movilibre.componentes.btnCrearC
import com.proyecto.movilibre.componentes.btnLogin
import com.proyecto.movilibre.data.AuthHelper


@Composable
fun LoginView(navController: androidx.navigation.NavHostController) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val authHelper = AuthHelper()
    var isLoading by remember { mutableStateOf(false) }
    var loginFallido by remember { mutableStateOf(false) }
    var esPasswordValida by remember { mutableStateOf(true) }
    var mensajesErrorPassword by remember { mutableStateOf<List<String>>(emptyList()) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        authHelper.getSavedCredential(context)?.let { (savedEmail, savedPass) ->
            correo = savedEmail
            password = savedPass
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally
    )   {
        Text(
            text = stringResource(id = R.string.title_Login),
            color = colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_accessibility),
            contentDescription = "Accessibility Icon",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        CorreoInput(correo) { correo = it }
        PasswInput(
            value = password,
            onValueChange = { password = it },
            onValidationChange = { isValid, errors ->
                esPasswordValida = isValid
                mensajesErrorPassword = errors
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { showForgotPasswordDialog = true },
            modifier = Modifier
                .align(Alignment.End) // Alinea a la derecha
                .padding(end = 16.dp)
        ) {
            Text("¿Olvidaste tu contraseña?", color = colorScheme.secondary)
        }

        btnLogin(
            enabled = !isLoading && correo.isNotEmpty() && password.isNotEmpty(),
            onClick = {
                isLoading = true
                authHelper.loginUser(correo, password, context) { success, isVerified ->
                    isLoading = false
                    if (success) {
                        loginFallido = false
                        if (isVerified) {
                            Toast.makeText(context, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show()
                            navController.navigate("mainv") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Tu correo no ha sido verificado. Por favor, revisa tu bandeja de entrada y haz clic en el enlace de verificación.", Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                        }
                    } else {
                        loginFallido = true
                        Toast.makeText(context, "Login fallido. Verifica tus credenciales.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(6.dp))

        btnCrearC {
            navController.navigate("registro")
        }
        Spacer(modifier = Modifier.height(12.dp))

        BtnVolver(
            onClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        )
    }

    RecuperarContrasena(
        show = showForgotPasswordDialog,
        onDismiss = { showForgotPasswordDialog = false }
    )

}

@Preview(showBackground = true)
@Composable
fun PreviewLoginView() {
    LoginView(navController = rememberNavController())
}