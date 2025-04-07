package com.proyecto.movilibre

// Necessary imports
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// PasswInput Composable function
@Composable
fun PasswInput() {
    // State for password input
    var password by remember { mutableStateOf("") }
    var mostrarSegundoCampo by remember { mutableStateOf(false) }

    // State for filled text
    var textoLlenoText by remember { mutableStateOf("") }

    // Column to arrange elements vertically
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        // Password TextField
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            placeholder = { Text("Ingresa tu Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // Mostrar el segundo campo cuando el primer campo pierde el foco y tiene texto
                    if (!focusState.isFocused && password.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }

        )
    }
}

// Preview function
@Preview(showBackground = true)
@Composable
fun PasswInputPreview() {
    PasswInput()
}
