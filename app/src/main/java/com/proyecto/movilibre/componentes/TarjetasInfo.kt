package com.proyecto.movilibre.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.proyecto.movilibre.R

@Composable
fun BotonesTarjeta(navController: NavHostController){
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { navController.navigate("rutasv") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btnRutas),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }

        Button(
            onClick = { navController.navigate("ajustesv") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btnAjustes),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TarjetaRuta(ruta: String?, tiempo: Long?) {
    var campos = false
    if (ruta != "") campos = true else campos = false
    val rutaStr = if (campos == true) "Ruta: $ruta" else "Seleccione una ruta"
    val tiempoStr = if (campos == true && tiempo != null) "Tiempo: ${tiempo} min" else "Tiempo: N/A"
    val color1 = if (campos == true) colorResource(id = R.color.Naranja) else MaterialTheme.colorScheme.tertiary
    val color2 = if (campos == true) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bus),
                contentDescription = "Bus Icon",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = rutaStr, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = tiempoStr, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Disp.:", color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(color1)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(color2)
                    )
                }
            }
        }
    }
}


fun formatearTiempoCaminando(segundos: Long?): String {
    if (segundos == null) return "Sin datos"

    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    val segundosRestantes = segundos % 60

    return when {
        horas > 0 -> "${horas}h ${minutos}m"
        minutos > 0 -> "${minutos}m ${segundosRestantes}s"
        else -> "${segundosRestantes}s"
    }
}

@Composable
fun TarjetaTiempoCaminando(visible: Boolean, tiempo: Long?) {
    if (!visible) return

    val tiempoFormateado = formatearTiempoCaminando(tiempo)
    val walkingRouteTime = "Tiempo a pie: $tiempoFormateado"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = walkingRouteTime,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
fun TarjetaProximaUnidad(ruta: String?, tiempoUnidad: Long) {
    var campos = false
    if (ruta != "") campos = true else campos = false
    val nextrutaStr = if (campos == true) "Próxima Unidad: $ruta - U02" else "Próxima Unidad: N/A"
    val nextUnidad = if (campos == true && tiempoUnidad != null) "Dentro de: $tiempoUnidad min" else "Dentro de: N/A"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nextrutaStr, color = MaterialTheme.colorScheme.onSurface)
            Text(text = nextUnidad, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
