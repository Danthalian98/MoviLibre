package com.proyecto.movilibre.componentes


// Necessary imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.movilibre.R

// Composable function for the button
@Composable
fun btnCrearC(onClick: () -> Unit) {
    Text(
        text = stringResource(id = R.string.CrearC),
        textAlign = TextAlign.Center,
        color = colorResource(id = R.color.AzulTopBar),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(onClick = onClick)
    )
}

// Preview function
@Preview(showBackground = true)
@Composable
fun PreviewBtnCrearC() {
    MaterialTheme {
        btnCrearC {        }
    }
}
