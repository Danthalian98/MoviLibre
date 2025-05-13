package com.proyecto.movilibre

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.*
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import com.proyecto.movilibre.data.UserPreferences

@Composable
fun Mainview(navController: NavHostController) {
    val colorScheme = MaterialTheme.colorScheme
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.4326, -99.1332), 12f) // CDMX
    }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val temaOscuro by userPrefs.temaOscuro.collectAsState(initial = false)
    val mapProperties by remember(temaOscuro) {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context,
                    if (temaOscuro) R.raw.map_style_dark else R.raw.map_style_light
                )
            )
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties
        ) {
            Marker(
                state = MarkerState(position = LatLng(19.4326, -99.1332)),
                title = "Aquí estás",
                snippet = "Ubicación de ejemplo"
            )
        }

        //  Contenido encima del mapa
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.navigate("rutasv") },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.btnRutas),
                        color = colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = { navController.navigate("ajustesv") },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.btnAjustes),
                        color = colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }
            }

            // Tarjeta de ruta actual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
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
                        Text(text = "Ruta: X - M", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Text(text = "Tiempo: Y mins.", color = colorScheme.onSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Disp.:", color = colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(colorResource(id = R.color.Naranja))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(colorResource(id = R.color.Verde))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de próxima ruta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Próxima ruta: X - N", color = colorScheme.onSurface)
                    Text(text = "Dentro de: X mins.", color = colorScheme.onSurface)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainview() {
    Mainview(navController = rememberNavController())
}
