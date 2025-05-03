package com.proyecto.movilibre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.proyecto.movilibre.componentes.BtnVolver
import com.proyecto.movilibre.componentes.btnDesplegable

@Composable
fun Rutasview(navController: androidx.navigation.NavHostController) {
    val rutas = listOf(
        "Ruta 1" to listOf("U-01", "U-02", "U-03"),
        "Ruta 2" to listOf("U-04", "U-05"),
        "Ruta 3" to listOf("U-06", "U-07", "U-08", "U-09")
    )

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = stringResource(id = R.string.title_Rutas),
                color = colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.primary)
                    .padding(20.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Rutas desplegables
            rutas.forEach { (nombre, unidades) ->
                btnDesplegable(tituloRuta = nombre, unidades = unidades)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Botón fijo abajo
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
fun Rutasview() {
    Rutasview(navController = rememberNavController())
}
