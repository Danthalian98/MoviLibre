package com.proyecto.movilibre

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.google.android.gms.maps.model.MarkerOptions
import com.proyecto.movilibre.componentes.BotonesTarjeta
import com.proyecto.movilibre.util.mostrarRutaCaminando
import com.proyecto.movilibre.componentes.TarjetaProximaUnidad
import com.proyecto.movilibre.componentes.TarjetaRuta
import com.proyecto.movilibre.componentes.TarjetaTiempoCaminando


@Composable
fun Mainview(navController: NavHostController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val userPrefs = remember { UserPreferences(context) }
    val temaOscuro by userPrefs.temaOscuro.collectAsState(initial = false)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val googleMapRef = remember { mutableStateOf<GoogleMap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val selectedRuta = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("rutaSeleccionada", "")
        ?.collectAsState(initial = "")

    val locationPermissionGranted = remember { mutableStateOf(false) }
    var walkingRouteTime by remember { mutableStateOf<Long?>(null) }

    var selectedBusStopDestination by remember { mutableStateOf<LatLng?>(null) }


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

    LaunchedEffect(Unit) {
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
                }
                mapView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Este LaunchedEffect ahora gestiona la lógica de dibujo del mapa
        LaunchedEffect(userLocation, googleMapRef.value, selectedRuta?.value, selectedBusStopDestination) {
            val map = googleMapRef.value
            if (map != null && userLocation != null) {
                // Limpiar el mapa siempre antes de dibujar nuevos elementos
                map.clear()

                // Añadir marcador de la ubicación del usuario
                //map.addMarker(MarkerOptions().position(userLocation!!).title("Tu ubicación"))

                // Cargar la ruta de autobús en función de la selección
                // Pasa el callback para actualizar selectedBusStopDestination
                if (selectedRuta?.value == "C54") {
                    cargarGeoJson(
                        context = context,
                        map = map,
                        geoJsonRawResId = R.raw.ruta_c54,
                        userLocation = userLocation,
                        coroutineScope = coroutineScope,
                        onWalkingRouteInfo = { info ->
                            walkingRouteTime = info
                        },
                        onBusStopSelected = { latLng ->
                            selectedBusStopDestination = latLng
                        }
                    )
                }
                if (selectedRuta?.value == "622") {
                    cargarGeoJson(
                        context = context,
                        map = map,
                        geoJsonRawResId = R.raw.ruta_622,
                        userLocation = userLocation,
                        coroutineScope = coroutineScope,
                        onWalkingRouteInfo = { info ->
                            walkingRouteTime = info
                        },
                        onBusStopSelected = { latLng ->
                            selectedBusStopDestination = latLng
                        }
                    )
                }
                if (selectedRuta?.value == "27") {
                    cargarGeoJson(
                        context = context,
                        map = map,
                        geoJsonRawResId = R.raw.ruta_c64,
                        userLocation = userLocation,
                        coroutineScope = coroutineScope,
                        onWalkingRouteInfo = { info ->
                            walkingRouteTime = info
                        },
                        onBusStopSelected = { latLng ->
                            selectedBusStopDestination = latLng
                        }
                    )
                }

                // Si hay una parada de autobús seleccionada, dibujar la ruta a pie
                selectedBusStopDestination?.let { destination ->
                    mostrarRutaCaminando(
                        context = context,
                        map = map,
                        origin = userLocation!!,
                        destination = destination,
                        coroutineScope = coroutineScope,
                        onRouteInfo = { seconds ->
                            walkingRouteTime = seconds
                        }
                    )
                }

                // Animar la cámara a la ubicación del usuario (o centrar en la ruta si es más relevante)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f))

            } else if (map != null && userLocation == null && locationPermissionGranted.value) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BotonesTarjeta(navController = navController)

            TarjetaRuta(ruta = selectedRuta?.value, tiempo = 15L)

            TarjetaTiempoCaminando(
                visible = selectedBusStopDestination != null && (walkingRouteTime ?: 0L) >= 10L,
                tiempo = walkingRouteTime
            )

            TarjetaProximaUnidad(ruta = selectedRuta?.value, tiempoUnidad = 10L)

        }
    }
}