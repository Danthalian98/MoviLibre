package com.proyecto.movilibre

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginView(navController: androidx.navigation.NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Iniciar Sesión",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF007AFF))
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Icono
        Image(
            painter = painterResource(id = R.drawable.ic_accessibility),
            contentDescription = "Accessibility Icon",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Campos de entrada
        CorreoInput()
        PasswInput()

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de login
        btnLogin()

        // Botón de crear cuenta
        btnCrearC {
            navController.navigate("registro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de regreso
        BtnVolver(

            onClick = {
                // Aquí defines la acción que ocurre al hacer clic en el botón Volver
                // Por ejemplo, podrías navegar a la pantalla anterior:
                // navController.popBackStack()
                println("Botón Volver presionado")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginView() {
    LoginView(navController = rememberNavController())
}
