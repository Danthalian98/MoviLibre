package com.proyecto.movilibre

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log // ¡Importa esto!
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
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
import com.proyecto.movilibre.util.obtenerYCalcularRutaAuto
import com.proyecto.movilibre.util.obtenerYCalcularRutaCaminando
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.proyecto.movilibre.componentes.formatearTiempo


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
    var carRouteTime by remember { mutableStateOf<Long?>(null) }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val busMarkers = remember { mutableMapOf<String, Marker>() }
    var isBusAvailable by remember { mutableStateOf<Boolean?>(null) }
    var busToStopRouteTime by remember { mutableStateOf<Long?>(null) }

    val selectedRuta by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("rutaSeleccionada", "")
        ?.collectAsState(initial = "") ?: remember { mutableStateOf("") }

    val locationPermissionGranted = remember { mutableStateOf(false) }
    var walkingRouteTime by remember { mutableStateOf<Long?>(null) }
    var selectedBusStopDestination by remember { mutableStateOf<LatLng?>(null) }
    var closestBusLocation by remember { mutableStateOf<LatLng?>(null) }

    var currentGeoJsonLayer by remember { mutableStateOf<GeoJsonLayer?>(null) }

    var justSetBusStopDestination by remember { mutableStateOf(false) }
    var isClosestBusAvailable by remember { mutableStateOf<Boolean?>(null) }


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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
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
                busMarkers.values.forEach { it.remove() }
                busMarkers.clear()

                selectedBusStopDestination = null
                walkingRouteTime = null
                justSetBusStopDestination = false
                busToStopRouteTime = null // Reiniciamos el tiempo del bus a la parada
                isClosestBusAvailable = null
                isBusAvailable = null // Reiniciamos la disponibilidad del autobús al cambiar de ruta
                closestBusLocation = null // Reiniciamos la ubicación del bus más cercano

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

            // --- INICIO DE SECCIÓN CRÍTICA DE FIRESTORE Y BUS ---
            val listenerRegistration = firestore.collection("buses")
                .whereEqualTo("ruta", selectedRuta)
                .addSnapshotListener { snapshots, error ->
                    if (error != null || snapshots == null) {
                        Log.e("Mainview", "Error al obtener datos de buses: ${error?.message}")
                        isBusAvailable = null
                        closestBusLocation = null
                        return@addSnapshotListener
                    }

                    val map = googleMapRef.value ?: return@addSnapshotListener
                    var foundAvailableBus = false
                    var tempClosestBusLocation: LatLng? = null
                    var minDistance = Float.MAX_VALUE

                    val currentIds = snapshots.documents.mapNotNull { it.id }.toSet()
                    val previousIds = busMarkers.keys.toSet()

                    for (id in previousIds.subtract(currentIds)) {
                        busMarkers[id]?.remove()
                        busMarkers.remove(id)
                        Log.d("Mainview", "Marcador de bus $id eliminado.")
                    }

                    // Para guardar los buses disponibles antes de calcular el más cercano
                    val currentAvailableBuses = mutableListOf<Pair<String, LatLng>>()

                    for (doc in snapshots.documents) {
                        val id = doc.id
                        val lat = doc.getDouble("latitud")
                        val lng = doc.getDouble("longitud")
                        val enServicio = doc.getBoolean("en_servicio")
                        val disponible = doc.getBoolean("disponible")

                        // Log para cada documento de bus
                        Log.d("Mainview", "Bus $id - Lat: $lat, Lng: $lng, EnServicio: $enServicio, Disponible: $disponible")

                        if (lat == null || lng == null || enServicio == null || disponible == null) {
                            Log.w("Mainview", "Datos incompletos para el bus $id. Saltando.")
                            continue
                        }

                        val position = LatLng(lat, lng)

                        if (enServicio && disponible) {
                            foundAvailableBus = true
                            currentAvailableBuses.add(id to position) // Agrega a la lista de buses disponibles
                            Log.d("Mainview", "Bus $id está en servicio y disponible.")
                        } else {
                            Log.d("Mainview", "Bus $id no está en servicio o no disponible.")
                        }

                        if (busMarkers.containsKey(id)) {
                            busMarkers[id]?.position = position
                            Log.d("Mainview", "Marcador de bus $id actualizado a $position.")
                        } else {
                            val marker = map.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title("Bus $id")
                                    .snippet("Ruta: $selectedRuta, Disponible: $disponible")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                            )
                            if (marker != null) {
                                busMarkers[id] = marker
                                Log.d("Mainview", "Marcador de bus $id agregado en $position.")
                            }
                        }
                    }

                    // Recalcula closestBusLocation cada vez que cambian los datos de Firebase O cuando se selecciona una parada
                    selectedBusStopDestination?.let { destination ->
                        minDistance = Float.MAX_VALUE // Reinicia la distancia mínima
                        tempClosestBusLocation = null // Reinicia la ubicación del bus más cercano

                        for ((id, position) in currentAvailableBuses) {
                            val results = FloatArray(1)
                            android.location.Location.distanceBetween(
                                position.latitude, position.longitude,
                                destination.latitude, destination.longitude,
                                results
                            )
                            val distance = results[0]
                            Log.d("Mainview", "Distancia del bus $id a la parada (${destination.latitude},${destination.longitude}): $distance metros.")
                            if (distance < minDistance) {
                                minDistance = distance
                                tempClosestBusLocation = position
                                Log.d("Mainview", "Bus $id es el más cercano hasta ahora: $tempClosestBusLocation")
                            }
                        }
                        if (tempClosestBusLocation == null && foundAvailableBus) {
                            Log.w("Mainview", "Aunque hay buses disponibles, no se pudo determinar el más cercano a la parada seleccionada. (Esto podría pasar si la parada seleccionada es nula temporalmente durante el cálculo)")
                        }
                    } ?: run {
                        Log.d("Mainview", "No hay parada de autobús seleccionada para calcular distancia a buses. selectedBusStopDestination es null.")
                        // Si no hay parada seleccionada, closestBusLocation debe ser null
                        tempClosestBusLocation = null
                    }
                    // --- FIN DE LÓGICA DE CÁLCULO DE CLOSESTBUSLOCATION ---


                    isBusAvailable = foundAvailableBus
                    closestBusLocation = tempClosestBusLocation
                    Log.d("Mainview", "Estado final después de Firestore update: isBusAvailable=$isBusAvailable, closestBusLocation=$closestBusLocation")
                }


        }

        // --- INICIO DE SECCIÓN CRÍTICA DE CÁLCULO DE RUTA EN AUTO ---
        DisposableEffect(googleMapRef.value, userLocation, selectedBusStopDestination, closestBusLocation) {
            val map = googleMapRef.value
            var walkingPolyline: Polyline? = null
            var busToStopPolyline: Polyline? = null

            if (map != null && userLocation != null && selectedBusStopDestination != null) {
                Log.d("Mainview", "DisposableEffect activado. userLocation=$userLocation, selectedBusStopDestination=$selectedBusStopDestination")

                val job = coroutineScope.launch {
                    // Ruta a pie del usuario a la parada
                    val (walkingRuta, walkingDuration) = obtenerYCalcularRutaCaminando(
                        context = context,
                        origin = userLocation!!,
                        destination = selectedBusStopDestination!!
                    )
                    withContext(Dispatchers.Main) {
                        walkingRouteTime = walkingDuration
                        if (walkingRuta != null) {
                            walkingPolyline = map.addPolyline(
                                PolylineOptions()
                                    .addAll(walkingRuta)
                                    .color(0xFF0000FF.toInt())
                                    .width(10f)
                            )
                            Log.d("Mainview", "Polilínea de ruta a pie dibujada.")
                        } else {
                            Log.w("Mainview", "No se pudo dibujar polilínea de ruta a pie.")
                        }
                    }

                    // *** LÓGICA DE RUTA DEL AUTOBÚS MÁS CERCANO A LA PARADA SELECCIONADA ***
                    var tempBusToStopDuration: Long? = null
                    if (closestBusLocation != null) {
                        Log.d("Mainview", "Calculando ruta del bus a la parada: Origen=${closestBusLocation!!}, Destino=${selectedBusStopDestination!!}")
                        try {
                            val (busRuta, busDuration) = obtenerYCalcularRutaAuto(
                                context = context,
                                origin = closestBusLocation!!,
                                destination = selectedBusStopDestination!!
                            )
                            withContext(Dispatchers.Main) {
                                tempBusToStopDuration = busDuration
                                Log.d("Mainview", "Resultado de obtenerYCalcularRutaAuto: busDuration=$busDuration")
                                if (busRuta != null) {
                                    busToStopPolyline = map.addPolyline(
                                        PolylineOptions()
                                            .addAll(busRuta)
                                            .color(0xFF0000FF.toInt())
                                            .width(8f)
                                            .pattern(listOf(Dot(), Gap(10f)))
                                    )
                                    Log.d("Mainview", "Polilínea de ruta del bus a la parada dibujada.")
                                } else {
                                    Log.w("Mainview", "No se pudo dibujar polilínea de ruta del bus a la parada (busRuta es null).")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Mainview", "Error al calcular ruta del bus a la parada: ${e.message}", e)
                            tempBusToStopDuration = null
                        }
                    } else {
                        Log.d("Mainview", "No hay closestBusLocation, no se calcula ruta del bus a la parada.")
                    }

                    withContext(Dispatchers.Main) {
                        busToStopRouteTime = tempBusToStopDuration
                        Log.d("Mainview", "FINAL: busToStopRouteTime asignado: $busToStopRouteTime")

                        if (justSetBusStopDestination) {
                            val builder = LatLngBounds.builder()
                            userLocation?.let { builder.include(it) }
                            selectedBusStopDestination?.let { builder.include(it) }
                            closestBusLocation?.let { builder.include(it) }

                            val bounds = builder.build()
                            val padding = 100
                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                            justSetBusStopDestination = false
                            Log.d("Mainview", "Cámara ajustada para incluir la ruta.")
                        }
                    }
                }

                onDispose {
                    job.cancel()
                    walkingPolyline?.remove()
                    busToStopPolyline?.remove()
                    Log.d("Mainview", "Polilíneas de ruta removidas al salir del DisposableEffect.")
                }
            } else {
                Log.d("Mainview", "DisposableEffect: No se cumplen las condiciones (map, userLocation, selectedBusStopDestination) para calcular rutas.")
                onDispose {
                    walkingPolyline?.remove()
                    busToStopPolyline?.remove()
                }
            }
        }
        // --- FIN DE SECCIÓN CRÍTICA DE CÁLCULO DE RUTA EN AUTO ---

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BotonesTarjeta(navController = navController)

            // Pasamos el tiempo de llegada del autobús a TarjetaRuta
            TarjetaRuta(ruta = selectedRuta, tiempo = busToStopRouteTime, isBusAvailable = isBusAvailable)

            TarjetaTiempoCaminando(
                visible = selectedBusStopDestination != null && (walkingRouteTime ?: 0L) >= 0L,
                tiempo = walkingRouteTime
            )

            TarjetaProximaUnidad(ruta = selectedRuta, tiempoUnidad = 10L)
        }
    }
}