package com.proyecto.movilibre.util

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.data.geojson.GeoJsonPoint
import com.google.android.gms.maps.model.LatLng
import com.proyecto.movilibre.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.google.android.gms.maps.model.Polyline // Import Polyline


suspend fun obtenerRutaCaminando(
    origin: LatLng,
    destination: LatLng,
    apiKey: String
): Pair<List<LatLng>?, Long?> = withContext(Dispatchers.IO) { // Retorna la ruta y el tiempo estimado
    val url = "https://maps.googleapis.com/maps/api/directions/json" +
            "?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&mode=walking&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: return@withContext Pair(null, null)

    val json = JSONObject(body)
    val routes = json.getJSONArray("routes")
    if (routes.length() == 0) return@withContext Pair(null, null)

    val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
    val points = overviewPolyline.getString("points")

    // Extraer duración
    var durationSeconds: Long? = null
    try {
        val legs = routes.getJSONObject(0).getJSONArray("legs")
        if (legs.length() > 0) {
            val duration = legs.getJSONObject(0).getJSONObject("duration")
            durationSeconds = duration.getLong("value")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext Pair(PolyUtil.decode(points), durationSeconds)
}

fun mostrarRutaCaminando(
    context: Context,
    map: GoogleMap,
    origin: LatLng,
    destination: LatLng,
    coroutineScope: CoroutineScope,
    onRouteInfo: (Long?) -> Unit
) {
    val apiKey = context.getString(R.string.gmaps_key)
    coroutineScope.launch {
        val (ruta, durationSeconds) = obtenerRutaCaminando(origin, destination, apiKey)
        if (ruta != null) {
            val polylineOptions = PolylineOptions()
                .addAll(ruta)
                .color(0xFF0000FF.toInt())
                .width(10f)
            map.addPolyline(polylineOptions)

            onRouteInfo(durationSeconds)

            // Animar la cámara a la ruta a pie para que sea visible
            val bounds = LatLng(
                Math.min(origin.latitude, destination.latitude),
                Math.min(origin.longitude, destination.longitude)
            ) to LatLng(
                Math.max(origin.latitude, destination.latitude),
                Math.max(origin.longitude, destination.longitude)
            )
            // Consider adjusting camera to fit both origin and destination
            // map.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.builder().include(origin).include(destination).build(), 100))
        } else {
            onRouteInfo(999999)
        }
    }
}

fun cargarGeoJson(
    context: Context,
    map: GoogleMap,
    geoJsonRawResId: Int,
    userLocation: LatLng?,
    coroutineScope: CoroutineScope,
    onWalkingRouteInfo: (Long?) -> Unit,
    onBusStopSelected: (LatLng) -> Unit // Nuevo callback para la parada seleccionada
) {
    val layer = GeoJsonLayer(map, geoJsonRawResId, context)

    for (feature in layer.features) {
        when (feature.geometry.geometryType) {
            "LineString" -> {
                val lineStyle = GeoJsonLineStringStyle().apply {
                    color = 0xFFFF0000.toInt()
                    width = 12f
                }
                feature.lineStringStyle = lineStyle
            }
            "Point" -> {
                val stopName = feature.getProperty("name") ?: "Parada"
                val pointStyle = GeoJsonPointStyle().apply {
                    title = stopName
                    snippet = "Parada de camión"
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_busstop)
                }
                feature.pointStyle = pointStyle
            }
        }
    }

    layer.setOnFeatureClickListener { feature ->
        val geometry = feature.geometry
        if (geometry.geometryType == "Point") {
            val pointGeometry = geometry as GeoJsonPoint
            val destination = pointGeometry.coordinates
            userLocation?.let {
                // En lugar de dibujar la ruta a pie directamente,
                // notificamos a Mainview que se ha seleccionado una parada
                onBusStopSelected(destination)
            }
        }
    }

    layer.addLayerToMap()
}