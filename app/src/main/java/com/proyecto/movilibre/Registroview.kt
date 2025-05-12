package com.proyecto.movilibre

import android.content.Context
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
import androidx.navigation.NavHostController
import com.proyecto.movilibre.componentes.*

@Composable
fun Registroview(navController: NavHostController) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var esPasswordValida by remember { mutableStateOf(true) } // Nuevo estado para la validez
    var mensajesErrorPassword by remember { mutableStateOf<List<String>>(emptyList()) } // Nuevo estado para los errores

    val authHelper = AuthHelper()
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.title_Registro),
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
                modifier = Modifier.size(223.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            NombreInput(nombre) { nombre = it }
            CorreoInput(correo) { correo = it }

            // Campo de contraseña con validación en tiempo real
            PasswInput(
                value = password,
                onValueChange = { password = it },
                onValidationChange = { isValid, errors -> // Recibimos la validez y los errores
                    esPasswordValida = isValid
                    mensajesErrorPassword = errors
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                btnRegistro {
                    // La validación de la contraseña ahora se maneja dentro de PasswInput
                    if (!esPasswordValida) return@btnRegistro // Usamos el estado de validez de PasswInput

                    isLoading = true
                    authHelper.registerUser(nombre, correo, password, context) { success ->
                        isLoading = false
                        if (success) {
                            authHelper.saveCredential(correo, password, context)
                            navController.navigate("mainv") {
                                popUpTo("registro") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Registro fallido", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(50.dp),
            contentAlignment = Alignment.Center
        ) {
            BtnVolver(
                onClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistroview() {
    Registroview(navController = rememberNavController())
}
