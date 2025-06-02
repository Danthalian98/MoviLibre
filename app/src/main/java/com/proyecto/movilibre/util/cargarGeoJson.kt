package com.proyecto.movilibre.util

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPoint
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import com.proyecto.movilibre.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

suspend fun obtenerRutaCaminando(
    origin: LatLng,
    destination: LatLng,
    apiKey: String
): Pair<List<LatLng>?, Long?> = withContext(Dispatchers.IO) {
    val url = "https://maps.googleapis.com/maps/api/directions/json" +
            "?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&mode=walking&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: return@withContext Pair(null, null) // Correcto

    val json = JSONObject(body)
    val routes = json.getJSONArray("routes")
    if (routes.length() == 0) return@withContext Pair(null, null)

    val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
    val points = overviewPolyline.getString("points")

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

// Ahora solo obtiene la ruta, no la dibuja ni la borra
suspend fun obtenerYCalcularRutaCaminando( // Renombrada para claridad
    context: Context,
    origin: LatLng,
    destination: LatLng,
): Pair<List<LatLng>?, Long?> {
    val apiKey = context.getString(R.string.gmaps_key)
    return obtenerRutaCaminando(origin, destination, apiKey)
}

suspend fun obtenerRutaAuto(
    origin: LatLng,
    destination: LatLng,
    apiKey: String
): Pair<List<LatLng>?, Long?> = withContext(Dispatchers.IO) {
    val url = "https://maps.googleapis.com/maps/api/directions/json" +
            "?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&mode=driving&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: return@withContext Pair(null, null)

    val json = JSONObject(body)
    val routes = json.getJSONArray("routes")
    if (routes.length() == 0) return@withContext Pair(null, null)

    val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
    val points = overviewPolyline.getString("points")

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

suspend fun obtenerYCalcularRutaAuto(
    context: Context,
    origin: LatLng,
    destination: LatLng
): Pair<List<LatLng>?, Long?> {
    val apiKey = context.getString(R.string.gmaps_key)
    return obtenerRutaAuto(origin, destination, apiKey)
}


fun cargarGeoJson(
    context: Context,
    map: GoogleMap,
    geoJsonRawResId: Int,
    userLocation: LatLng?,
    coroutineScope: CoroutineScope,
    onWalkingRouteInfo: (Long?) -> Unit,
    onBusStopSelected: (LatLng) -> Unit
): GeoJsonLayer {
    val layer = GeoJsonLayer(map, geoJsonRawResId, context)

    // Lista para guardar los estilos de puntos
    val pointStyles = mutableListOf<Pair<GeoJsonPoint, GeoJsonPointStyle>>()

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
                val pointGeometry = feature.geometry as GeoJsonPoint

                val pointStyle = GeoJsonPointStyle().apply {
                    title = stopName
                    snippet = "Parada de camión"
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_busstop)
                }

                feature.pointStyle = pointStyle
                pointStyles.add(pointGeometry to pointStyle)
            }
        }
    }

    // Listener para detectar clics en las paradas
    layer.setOnFeatureClickListener { feature ->
        val geometry = feature.geometry
        if (geometry.geometryType == "Point") {
            val pointGeometry = geometry as GeoJsonPoint
            val destination = pointGeometry.coordinates
            userLocation?.let {
                onBusStopSelected(destination)
            }
        }
    }

    // Agrega la capa al mapa
    layer.addLayerToMap()

    // Listener para detectar cambios de zoom y actualizar íconos
    map.setOnCameraIdleListener {
        val zoom = map.cameraPosition.zoom
        val nuevoIcono = if (zoom >= 14.2f) {
            BitmapDescriptorFactory.fromResource(R.drawable.ic_busstop)
        } else {
            BitmapDescriptorFactory.fromResource(R.drawable.ic_busstop_invisible) // ícono transparente
        }

        for ((_, style) in pointStyles) {
            style.icon = nuevoIcono
        }
    }

    return layer
}
