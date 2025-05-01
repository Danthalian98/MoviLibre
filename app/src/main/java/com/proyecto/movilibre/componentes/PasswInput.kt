package com.proyecto.movilibre.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.proyecto.movilibre.R

@Composable
fun PasswInput(value: String, onValueChange: (String) -> Unit) {
    var mostrarSegundoCampo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(id = R.string.ContraHint1)) },
            placeholder = { Text(stringResource(id = R.string.ContraHint2)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // Mostrar el segundo campo cuando el primer campo pierde el foco y tiene texto
                    if (!focusState.isFocused && value.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }

        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswInputPreview() {
    var value by remember { mutableStateOf("123456") }
    PasswInput(value = value, onValueChange = { value = it })
}