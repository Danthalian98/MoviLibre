package com.proyecto.movilibre.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import com.proyecto.movilibre.R
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip

@Composable
fun BtnVolver(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(width = 150.dp, height = 60.dp)
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Volver",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
// Preview function
@Preview
@Composable
fun PreviewBtnVolver() {
    BtnVolver(onClick = {    })
}