package com.proyecto.movilibre.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.movilibre.R

@Composable
fun PasswInput(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessages: List<String> = emptyList()
) {
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(id = R.string.ContraHint1)) },
            placeholder = { Text(stringResource(id = R.string.ContraHint2)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val iconRes = if (passwordVisible) R.drawable.view else R.drawable.hide
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        Modifier.padding(end = 8.dp)
                    )
                }
            },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && value.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }
        )

        // Mostrar errores debajo del campo
        if (isError) {
            errorMessages.forEach { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 2.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswInputPreview() {
    var value by remember { mutableStateOf("123456") }
    PasswInput(value = value, onValueChange = { value = it })
}
