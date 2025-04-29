
package com.proyecto.movilibre

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


// Composable function for the dropdown button
@Composable
fun btnDesplegable2(
    estado: Boolean,
    navController: NavHostController
) {
    var expandedNotificaciones by remember { mutableStateOf(false) }
    var expandedUsuario by remember { mutableStateOf(false) }

    Column {
        // Sección: Notificaciones
        Button(
            onClick = { expandedNotificaciones = !expandedNotificaciones },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Notificaciones", color = Color.White, fontSize = 22.sp)
                Icon(
                    painter = painterResource(
                        id = if (expandedNotificaciones)
                            R.drawable.ic_arrow_drop_down
                        else
                            R.drawable.ic_arrow_drop_up
                    ),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        if (expandedNotificaciones) {
            val notificaciones = listOf("Sonido", "Vibración", "Correo")
            val switches = remember { mutableStateListOf(true, false, true) }

            notificaciones.forEachIndexed { index, texto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = texto, fontSize = 18.sp)
                    Switch(
                        checked = switches[index],
                        onCheckedChange = { switches[index] = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección: Usuario
        Button(
            onClick = { expandedUsuario = !expandedUsuario },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Usuario", color = Color.White, fontSize = 22.sp)
                Icon(
                    painter = painterResource(
                        id = if (expandedUsuario)
                            R.drawable.ic_arrow_drop_down
                        else
                            R.drawable.ic_arrow_drop_up
                    ),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        if (expandedUsuario) {
            if (!estado) {
                // Solo un botón centrado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9E9E9E) // Dark gray color
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Iniciar sesión", fontSize = 18.sp)
                    }
                }
            } else {
                // 3 opciones como en notificaciones
                val opciones = listOf("Perfil", "Cambiar contraseña", "Cerrar sesión")
                opciones.forEach { texto ->
                    Text(
                        text = texto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .padding(vertical = 12.dp),
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


// Preview function
@Preview
@Composable
fun PreviewbtnDesplegable2() {
    val fakeNavController = rememberNavController()
    btnDesplegable2(
        estado = false,
        navController = fakeNavController
    )
}
