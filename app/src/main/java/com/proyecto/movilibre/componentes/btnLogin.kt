package com.proyecto.movilibre.componentes


// Necessary imports
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.movilibre.R

// Composable function for the button
@Composable
fun btnLogin() {
    Button(
        onClick = { /*TODO: Add click action*/ },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Gris)),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.btnIniciarS),
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
