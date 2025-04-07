package com.proyecto.movilibre


// Necessary imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Composable function for the button
@Composable
fun btnCrearC(onClick: () -> Unit) {
    Text(
        text = "Crear cuenta",
        textAlign = TextAlign.Center,
        color = Color(0xFF007AFF),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(onClick = onClick) // <- aquí llamamos a la función pasada
    )
}

// Preview function
@Preview(showBackground = true)
@Composable
fun PreviewBtnCrearC() {
    MaterialTheme {
        btnCrearC {
            println("Preview: Crear cuenta presionado")
        }
    }
}
