package com.proyecto.movilibre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.proyecto.movilibre.ui.theme.MoviLibreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviLibreTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

// Puedes eliminar o comentar estas funciones si ya no las necesitas.
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    // Usamos rememberNavController para crear un NavHostController para la vista previa
    val navController = rememberNavController()

    MoviLibreTheme {
        // Le pasamos el navController a LoginView
        LoginView(navController = navController)
    }
}

