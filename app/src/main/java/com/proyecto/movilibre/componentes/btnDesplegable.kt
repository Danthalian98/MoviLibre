
package com.proyecto.movilibre.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.proyecto.movilibre.R


// Composable function for the dropdown button
@Composable
fun btnDesplegable(
    tituloRuta: String,
    unidades: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 12.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Gris))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
            ) {
                Text(text = tituloRuta, color = Color.White, fontSize = 22.sp)
                Icon(
                    painter = painterResource(id = if (expanded) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_up),
                    contentDescription = null,
                    tint = colorResource(id = R.color.black)
                )
            }
        }
        if (expanded) {
            Column {
                unidades.forEach { unidad ->
                    Text(
                        text = unidad,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .padding(12.dp),
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


// Preview function
@Preview
@Composable
fun PreviewbtnDesplegable() {
    btnDesplegable(
        tituloRuta = TODO(),
        unidades = TODO()
    )
}
