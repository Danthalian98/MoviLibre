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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.proyecto.movilibre.R
import androidx.compose.ui.graphics.Color

@Composable
fun NombreInput(value: String, onValueChange: (String) -> Unit) {
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    var esNombreValido by remember { mutableStateOf(true) }
    val maxCaracteres = 50 // Define la longitud máxima deseada

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = value,
            onValueChange = { nuevoValor ->
                if (nuevoValor.length <= maxCaracteres && nuevoValor.all { it.isLetter() || it.isWhitespace() }) {
                    onValueChange(nuevoValor)
                    esNombreValido = nuevoValor.isNotEmpty()
                }
            },
            label = { Text(stringResource(id = R.string.NombreHint1)) },
            placeholder = { Text(stringResource(id = R.string.NombreHint2)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        mostrarSegundoCampo = value.isNotEmpty()
                        esNombreValido = value.isNotEmpty() && value.all { it.isLetter() || it.isWhitespace() }
                    }
                },
            isError = !esNombreValido && value.isNotEmpty(),
            supportingText = {
                if (!esNombreValido && value.isNotEmpty()) {
                    val mensajeError = if (value.isEmpty()) {
                        stringResource(id = R.string.ErrorNombreVacio)
                    } else {
                        stringResource(id = R.string.ErrorNombreSoloLetras)
                    }
                    Text(mensajeError, color = Color.Red)
                } else if (value.length > maxCaracteres) {
                    Text(stringResource(id = R.string.ErrorNombreLongitudMaxima, maxCaracteres), color = Color.Red)
                }
            }
        )
    }
}

// Preview function for NombreInput
@Preview(showBackground = true)
@Composable
fun NombreInputPreview() {
    var value by remember { mutableStateOf("Zavala") }
    NombreInput(value = value, onValueChange = { value = it })
}