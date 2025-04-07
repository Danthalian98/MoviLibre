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

// NombreInput Composable function
@Composable
fun NombreInput() {
    // State for the first text field
    var nombreText by remember { mutableStateOf("") }
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    // State for the second text field
    var textolleno by remember { mutableStateOf("") }

    // Column to arrange text fields vertically
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        // First TextField for "Nombre"
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = nombreText,
            onValueChange = { nombreText = it },
            label = { Text("Nombre") },
            placeholder = { Text("Ingresa tu Nombre") }, // Hint para el primer campo
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // Mostrar el segundo campo cuando el primer campo pierde el foco y tiene texto
                    if (!focusState.isFocused && nombreText.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }

        )
    }
}

// Preview function for NombreInput
@Preview(showBackground = true)
@Composable
fun NombreInputPreview() {
    NombreInput()
}
