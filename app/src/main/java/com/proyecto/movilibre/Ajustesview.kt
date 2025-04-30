package com.proyecto.movilibre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.proyecto.movilibre.componentes.BtnVolver
import com.proyecto.movilibre.componentes.btnDesplegable2

@Composable
fun Ajustesview(navController: NavHostController) {
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
            text = stringResource(id = R.string.title_Ajustes),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.AzulTopBar))
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        btnDesplegable2(
            estado = false,
            navController = navController
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
            contentAlignment = Alignment.BottomCenter
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
fun Ajustesview() {
    Ajustesview(navController = rememberNavController())
}
