package com.proyecto.movilibre.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.movilibre.Ajustesview
import com.proyecto.movilibre.CambiarContrasenaView
import com.proyecto.movilibre.LoginView
import com.proyecto.movilibre.Mainview
import com.proyecto.movilibre.PerfilView
import com.proyecto.movilibre.Registroview
import com.proyecto.movilibre.Rutasview


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "mainv") {
        composable("mainv") {
            Mainview(navController)
        }
        composable("rutasv") {
            Rutasview(navController)
        }
        composable("ajustesv") {
            Ajustesview(navController)
        }
        composable("login") {
            LoginView(navController)
        }
        composable("registro") {
            Registroview(navController)
        }
        composable("cambiar_contrasena") {
            CambiarContrasenaView(navController)
        }
        composable("perfilv") {
            PerfilView(navController)
        }
    }
}