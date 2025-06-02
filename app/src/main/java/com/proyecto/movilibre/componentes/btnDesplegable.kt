
package com.proyecto.movilibre.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.proyecto.movilibre.R

@Composable
fun btnDesplegable(
    tituloRuta: String,
    unidades: List<String>,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = tituloRuta, color = colorScheme.onSurface, fontSize = 22.sp)
                Icon(
                    painter = painterResource(id = if (!expanded) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down),
                    contentDescription = null,
                    tint = colorScheme.onSurface
                )
            }
        }

        if (expanded) {
            Column {
                unidades.forEach { unidad ->
                    TextButton(
                        onClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.previousBackStackEntry?.savedStateHandle?.set("rutaSeleccionada", unidad)
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .border(
                                width = 2.dp,
                                color = colorScheme.tertiary, // Color del margen
                                shape = RoundedCornerShape(50)
                            )
                            .clip(RoundedCornerShape(50))
                    ) {
                        Text(text = unidad, fontSize = 20.sp, color = colorScheme.onBackground)
                    }
                }
            }
        }
    }
}