package com.proyecto.movilibre

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.proyecto.movilibre.componentes.BotonesTarjeta
import com.proyecto.movilibre.componentes.TarjetaProximaUnidad
import com.proyecto.movilibre.componentes.TarjetaRuta
import com.proyecto.movilibre.componentes.TarjetaTiempoCaminando
import com.proyecto.movilibre.data.UserPreferences
import com.proyecto.movilibre.util.cargarGeoJson
import com.proyecto.movilibre.util.obtenerYCalcularRutaCaminando
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Mainview(navController: NavHostController) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val userPrefs = remember { UserPreferences(context) }
    val temaOscuro by userPrefs.temaOscuro.collectAsState(initial = false)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val googleMapRef = remember { mutableStateOf<GoogleMap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val selectedRuta by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("rutaSeleccionada", "")
        ?.collectAsState(initial = "") ?: remember { mutableStateOf("") }

    val locationPermissionGranted = remember { mutableStateOf(false) }
    var walkingRouteTime by remember { mutableStateOf<Long?>(null) }
    var selectedBusStopDestination by remember { mutableStateOf<LatLng?>(null) }

    var currentGeoJsonLayer by remember { mutableStateOf<GeoJsonLayer?>(null) }
    // currentWalkingPolyline ya no necesita ser 'var' si se usa DisposableEffect para crearlo y gestionarlo
    // Se gestionará internamente en el DisposableEffect

    var justSetBusStopDestination by remember { mutableStateOf(false) }


    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted.value = isGranted
        if (isGranted) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted.value = true
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            launcher.launch(permission)
        }
    }

    DisposableEffect(fusedLocationClient) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    var initialZoomDone by remember { mutableStateOf(false) }

    LaunchedEffect(googleMapRef.value, userLocation) {
        val map = googleMapRef.value
        val location = userLocation
        if (map != null && location != null && !initialZoomDone) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
            initialZoomDone = true
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
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }
                }
                mapView
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(googleMapRef.value, selectedRuta) {
            val map = googleMapRef.value
            if (map != null) {
                currentGeoJsonLayer?.removeLayerFromMap()
                currentGeoJsonLayer = null

                map.clear()

                selectedBusStopDestination = null
                walkingRouteTime = null
                justSetBusStopDestination = false

                val geoJsonResId = when (selectedRuta) {
                    "C54" -> R.raw.ruta_c54
                    "622" -> R.raw.ruta_622
                    "27" -> R.raw.ruta_c64
                    else -> null
                }

                geoJsonResId?.let { resId ->
                    val newLayer = cargarGeoJson(
                        context = context,
                        map = map,
                        geoJsonRawResId = resId,
                        userLocation = userLocation,
                        coroutineScope = coroutineScope,
                        onWalkingRouteInfo = { /* ... */ },
                        onBusStopSelected = { latLng ->
                            selectedBusStopDestination = latLng
                            justSetBusStopDestination = true
                        }
                    )
                    currentGeoJsonLayer = newLayer
                }

                userLocation?.let {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                }
            }
        }

        // *** NUEVO DisposableEffect para la polilínea de la ruta a pie ***
        DisposableEffect(googleMapRef.value, userLocation, selectedBusStopDestination) {
            val map = googleMapRef.value
            var walkingPolyline: Polyline? = null // Polilínea local al efecto

            // Solo procede si tenemos mapa, ubicación de usuario y un destino
            if (map != null && userLocation != null && selectedBusStopDestination != null) {
                // Iniciar la obtención de la ruta en una corrutina
                val job = coroutineScope.launch {
                    val (ruta, durationSeconds) = obtenerYCalcularRutaCaminando(
                        context = context,
                        origin = userLocation!!,
                        destination = selectedBusStopDestination!!
                    )

                    withContext(Dispatchers.Main) { // Asegúrate de actualizar la UI en el hilo principal
                        if (ruta != null) {
                            val polylineOptions = PolylineOptions()
                                .addAll(ruta)
                                .color(0xFF0000FF.toInt())
                                .width(10f)
                            walkingPolyline = map.addPolyline(polylineOptions) // Aquí se añade la Polyline
                            walkingRouteTime = durationSeconds

                            // Lógica de cámara
                            if (justSetBusStopDestination) {
                                val builder = LatLngBounds.builder()
                                builder.include(userLocation!!)
                                builder.include(selectedBusStopDestination!!)
                                val bounds = builder.build()
                                val padding = 100

                                justSetBusStopDestination = false
                            } else {
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 16f))
                            }
                        } else {
                            walkingRouteTime = 999999L // Indicar que no hay ruta
                            walkingPolyline?.remove() // Asegurar que se borra si no se encuentra ruta
                            walkingPolyline = null
                        }
                    }
                }
                onDispose {
                    // Este bloque se ejecuta cuando las dependencias cambian o el efecto se "dispone"
                    job.cancel() // Cancela el trabajo si aún está en progreso
                    walkingPolyline?.remove() // Borra la polilínea actual antes de que se cree una nueva
                }
            } else {
                // Si no hay destino, asegúrate de que no haya ruta a pie visible
                onDispose {
                    walkingPolyline?.remove()
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

            TarjetaRuta(ruta = selectedRuta, tiempo = 15L)

            TarjetaTiempoCaminando(
                visible = selectedBusStopDestination != null && (walkingRouteTime ?: 0L) >= 0L,
                tiempo = walkingRouteTime
            )

            TarjetaProximaUnidad(ruta = selectedRuta, tiempoUnidad = 10L)
        }
    }
}