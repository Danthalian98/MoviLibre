package com.proyecto.movilibre

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.proyecto.movilibre.data.UserPreferences
import com.proyecto.movilibre.util.cargarGeoJson
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun Mainview(navController: NavHostController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val userPrefs = remember { UserPreferences(context) }
    val temaOscuro by userPrefs.temaOscuro.collectAsState(initial = false)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val googleMapRef = remember { mutableStateOf<GoogleMap?>(null) }


    val selectedRuta = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("rutaSeleccionada", "")
        ?.collectAsState(initial = "")

    val locationPermissionGranted = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted.value = isGranted
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    LaunchedEffect(true) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted.value = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        } else {
            launcher.launch(permission)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val mapView = MapView(ctx)
                mapView.onCreate(Bundle())
                mapView.onResume()

                mapView.getMapAsync { map ->
                    googleMapRef.value = map
                    map.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            if (temaOscuro) R.raw.map_style_dark else R.raw.map_style_light
                        )
                    )

                    if (locationPermissionGranted.value) {
                        map.isMyLocationEnabled = true
                    }

                    userLocation?.let {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }

                    // Cargar GeoJSON de ruta seleccionada
                    if (selectedRuta?.value == "C54") {
                        cargarGeoJson(context, map, R.raw.ruta_c54)
                    }
                }

                mapView
            },
            modifier = Modifier.fillMaxSize()
        )

        // UI adicional (botones y tarjetas)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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
                        Text(text = "Ruta: X - M", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Tiempo: Y mins.", color = MaterialTheme.colorScheme.onSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Disp.:", color = MaterialTheme.colorScheme.onSurface)
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Próxima ruta: X - N", color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Dentro de: X mins.", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        LaunchedEffect(userLocation) {
            userLocation?.let {
                googleMapRef.value?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            }
        }

    }
}