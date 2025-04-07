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

// Composable function for CorreoInput
@Composable
fun CorreoInput() {
    // State for the first text field
    var correoText by remember { mutableStateOf("") }
    // State to control the visibility of the second text field
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    // State for the second text field
    var textoLlenoText by remember { mutableStateOf("") }

    // Column to arrange the text fields vertically
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        // First text field (Correo)
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = correoText,
            onValueChange = { correoText = it },
            label = { Text("Correo") },
            placeholder = { Text("Ingresa tu correo") }, // Hint para el primer campo
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // Mostrar el segundo campo cuando el primer campo pierde el foco y tiene texto
                    if (!focusState.isFocused && correoText.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }

        )
    }
}

// Preview function
@Preview(showBackground = true)
@Composable
fun PreviewCorreoInput() {
    CorreoInput()
}