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
    onValidationChange: (Boolean, List<String>) -> Unit // Nueva función para comunicar el estado de validación
) {
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val longitudMinima = 8

    fun validarContrasena(password: String): List<String> {
        val errores = mutableListOf<String>()
        if (password.length < longitudMinima) {
            errores.add(stringResource(id = R.string.ErrorContrasenaLongitudMinima, longitudMinima))
        }
        if (!password.any { it.isUpperCase() }) {
            errores.add(stringResource(id = R.string.ErrorContrasenaMayuscula))
        }
        if (!password.any { it.isLowerCase() }) {
            errores.add(stringResource(id = R.string.ErrorContrasenaMinuscula))
        }
        if (!password.any { it.isDigit() }) {
            errores.add(stringResource(id = R.string.ErrorContrasenaNumero))
        }
        // Opcional: verificar símbolos
        // if (!password.any { !it.isLetterOrDigit() }) {
        //     errores.add(stringResource(id = R.string.ErrorContrasenaSimbolo))
        // }
        return errores
    }

    Column(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = value,
            onValueChange = { nuevoValor ->
                onValueChange(nuevoValor)
                val errores = validarContrasena(nuevoValor)
                onValidationChange(errores.isEmpty(), errores)
            },
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
            isError = errorMessages.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && value.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }
        )

        // Mostrar errores debajo del campo
        if (errorMessages.isNotEmpty()) {
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
    PasswInput(value = value, onValueChange = { value = it }, onValidationChange = { _, _ -> })
}