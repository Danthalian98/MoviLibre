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
import com.proyecto.movilibre.componentes.NombreInput
import com.proyecto.movilibre.componentes.PasswInput
import com.proyecto.movilibre.componentes.btnRegistro

@Composable
fun Registroview(navController: androidx.navigation.NavHostController) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val authHelper = AuthHelper()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(colorResource(id = R.color.BlancoBKG)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = stringResource(id = R.string.title_Registro),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.AzulTopBar))
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Icono
        Image(
            painter = painterResource(id = R.drawable.ic_accessibility),
            contentDescription = "Accessibility Icon",
            modifier = Modifier.size(223.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Campos de entrada
        NombreInput(nombre) { nombre = it }
        CorreoInput(correo) { correo = it }
        PasswInput(password) { password = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de Registro
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            btnRegistro {
                isLoading = true
                authHelper.registerUser(nombre, correo, password, context) { success ->
                    isLoading = false
                    if (success) {
                        navController.navigate("login") {
                            popUpTo("registro") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Registro fallido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de regreso
        BtnVolver(
            onClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistroview() {
    Registroview(navController = rememberNavController())
}
