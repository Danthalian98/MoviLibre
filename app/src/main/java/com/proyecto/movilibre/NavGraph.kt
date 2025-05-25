package com.proyecto.movilibre

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
    }
}