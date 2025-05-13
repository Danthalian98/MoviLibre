package com.proyecto.movilibre.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.proyecto.movilibre.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent

@Composable
fun CorreoInput(value: String, onValueChange: (String) -> Unit) {
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    var esCorreoValido by remember { mutableStateOf(true) }
    val regexCorreo = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = value,
            onValueChange = { nuevoValor ->
                val textoSinSaltos = nuevoValor.replace("\n", "")
                onValueChange(textoSinSaltos) // Actualiza el valor sin saltos de línea
                esCorreoValido = textoSinSaltos.matches(regexCorreo) // Validación del correo
            },
            label = { Text(stringResource(id = R.string.CorreoHint1)) },
            placeholder = { Text(stringResource(id = R.string.CorreoHint2)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && value.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter) {
                        true
                    } else {
                        false
                    }
                },
            isError = !esCorreoValido,
            supportingText = {
                if (!esCorreoValido && value.isNotEmpty()) {
                    Text(stringResource(id = R.string.ErrorCorreoInvalido), color = Color.Red)
                }
            }
        )
    }
}

// Preview function
@Preview(showBackground = true)
@Composable
fun PreviewCorreoInput() {
    var value by remember { mutableStateOf("correo@correo") }
    CorreoInput(value = value, onValueChange = { value = it })
}