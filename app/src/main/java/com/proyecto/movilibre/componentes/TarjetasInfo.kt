// TarjetaRuta.kt
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
fun TarjetaRuta(ruta: String?, tiempo: Long?, isBusAvailable: Boolean?) {
    val campos = !ruta.isNullOrBlank()
    val rutaStr = if (campos) "Ruta: $ruta" else "Seleccione una ruta"
    // Usamos la función formatearTiempo para el tiempo del bus
    // *** POSIBLE PROBLEMA: Asegúrate de que 'tiempo' no sea null aquí. ***
    val tiempoStr = if (campos && tiempo != null) "Llega en: ${formatearTiempo(tiempo)}" else "Tiempo: N/A"

    // Determine the colors based on `isBusAvailable`
    val availableColor = colorResource(id = R.color.Naranja) // Orange for available
    val unavailableColor = MaterialTheme.colorScheme.tertiary // Your default tertiary color

    val colorStatus1 = if (isBusAvailable == true) availableColor else unavailableColor
    val colorStatus2 = if (isBusAvailable == false) availableColor else unavailableColor


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
                            .background(colorStatus1) // Use colorStatus1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorStatus2) // Use colorStatus2
                    )
                }
            }
        }
    }
}

// Renombramos la función para que sea más genérica, ya que ahora se usa para el tiempo del bus también.
fun formatearTiempo(segundos: Long?): String {
    if (segundos == null) return "N/A" // Cambiado a N/A para ser consistente con el resto de la app

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

    val tiempoFormateado = formatearTiempo(tiempo) // Usamos la función genérica
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