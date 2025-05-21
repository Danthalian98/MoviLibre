package com.proyecto.movilibre.util

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.proyecto.movilibre.R

fun cargarGeoJson(context: Context, map: GoogleMap, geoJsonRawResId: Int) {
    val layer = GeoJsonLayer(map, geoJsonRawResId, context)

    for (feature in layer.features) {
        when (feature.geometry.geometryType) {
            "LineString" -> {
                val lineStyle = GeoJsonLineStringStyle().apply {
                    color = 0xFF0000FF.toInt() // Azul
                    width = 10f
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

    layer.addLayerToMap()
}