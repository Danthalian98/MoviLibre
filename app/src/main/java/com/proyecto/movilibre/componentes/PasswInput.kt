package com.proyecto.movilibre.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.movilibre.R
import android.content.Context
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PasswInput(
    value: String,
    onValueChange: (String) -> Unit,
    onValidationChange: (Boolean, List<String>) -> Unit
) {
    var mostrarSegundoCampo by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val longitudMinima = 8
    val context = LocalContext.current

    val erroresContrasena: List<String> by remember(value) {
        derivedStateOf {
            val errores = mutableListOf<String>()
            if (value.length < longitudMinima) {
                errores.add(context.getString(R.string.ErrorContrasenaLongitudMinima, longitudMinima))
            }
            if (!value.any { it.isUpperCase() }) {
                errores.add(context.getString(R.string.ErrorContrasenaMayuscula))
            }
            if (!value.any { it.isLowerCase() }) {
                errores.add(context.getString(R.string.ErrorContrasenaMinuscula))
            }
            if (!value.any { it.isDigit() }) {
                errores.add(context.getString(R.string.ErrorContrasenaNumero))
            }
            errores
        }
    }

    val esContrasenaValida by remember(erroresContrasena) { derivedStateOf { erroresContrasena.isEmpty() } }

    Column(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = value,
            onValueChange = { nuevoValor ->
                onValueChange(nuevoValor)
                onValidationChange(esContrasenaValida, erroresContrasena)
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
            isError = !esContrasenaValida,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && value.isNotEmpty()) {
                        mostrarSegundoCampo = true
                    }
                }
        )

        // Mostrar errores debajo del campo
        if (!esContrasenaValida) {
            erroresContrasena.forEach { error ->
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