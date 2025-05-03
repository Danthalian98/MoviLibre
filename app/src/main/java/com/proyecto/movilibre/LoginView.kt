package com.proyecto.movilibre

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.proyecto.movilibre.componentes.BtnVolver
import com.proyecto.movilibre.componentes.CorreoInput
import com.proyecto.movilibre.componentes.PasswInput
import com.proyecto.movilibre.componentes.btnCrearC
import com.proyecto.movilibre.componentes.btnLogin
import com.proyecto.movilibre.AuthHelper
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun LoginView(navController: androidx.navigation.NavHostController) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val authHelper = AuthHelper()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authHelper.getSavedCredential(context)?.let { (savedEmail, savedPass) ->
            correo = savedEmail
            password = savedPass
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            PasswInput(password) { password = it }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(color = colorScheme.primary)
            } else {
                btnLogin {
                    isLoading = true
                    authHelper.loginUser(correo, password, context) { success ->
                        isLoading = false
                        if (success) {
                            navController.navigate("mainv") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Login fallido", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            btnCrearC {
                navController.navigate("registro")
            }
        }

        // Botón fijo abajo
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
fun PreviewLoginView() {
    LoginView(navController = rememberNavController())
}
