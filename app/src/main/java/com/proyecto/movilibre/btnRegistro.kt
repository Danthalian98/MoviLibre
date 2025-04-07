package com.proyecto.movilibre


// Necessary imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Composable function for the button
@Composable
fun btnRegistro() {
    Button(
        onClick = { /*TODO: Add click action*/ },
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F4F4F)),
        modifier = Modifier
            .padding(16.dp)
            .height(48.dp)
    ) {
        Text(
            text = "Registrar",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Preview function
@Preview
@Composable
fun PreviewBtnRegistro() {
    btnRegistro()
}
