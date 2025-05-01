package com.proyecto.movilibre

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun LoginView(navController: androidx.navigation.NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(colorResource(id = R.color.BlancoBKG)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.title_Login),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.AzulTopBar))
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_accessibility),
            contentDescription = "Accessibility Icon",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        CorreoInput()
        PasswInput()

        Spacer(modifier = Modifier.height(8.dp))

        btnLogin()

        btnCrearC {
            navController.navigate("registro")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
fun PreviewLoginView() {
    LoginView(navController = rememberNavController())
}
