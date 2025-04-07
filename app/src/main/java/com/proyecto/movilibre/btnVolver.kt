package com.proyecto.movilibre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

// Composable function for the image button
@Composable
fun BtnVolver(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 60.dp)
            .clip(RoundedCornerShape(50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back), // Replace with your back arrow icon
            contentDescription = "Volver",
            modifier = Modifier.fillMaxSize(), // Make the Icon fill the Box
            tint = Color(0xFF38E428) // Optional: Set a tint color for the icon
        )
    }
}

// Preview function
@Preview
@Composable
fun PreviewBtnVolver() {
    BtnVolver(onClick = {
        println("Botón Volver clickeado")
    })
}