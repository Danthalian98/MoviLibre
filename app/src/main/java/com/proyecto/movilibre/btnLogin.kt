package com.proyecto.movilibre


// Necessary imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Composable function for the button
@Composable
fun btnLogin() {
    Button(
        onClick = { /*TODO: Add click action*/ },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9E9E9E) // Dark gray color
        ),
        shape = RoundedCornerShape(50) // Rounded corners
    ) {
        Text(
            text = "Iniciar Sesión",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Preview function
@Preview
@Composable
fun PreviewBtnLogin() {
    btnLogin()
}
