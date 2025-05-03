package com.proyecto.movilibre.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val SONIDO_KEY = booleanPreferencesKey("sonido")
        val VIBRACION_KEY = booleanPreferencesKey("vibracion")
        val CORREO_KEY = booleanPreferencesKey("correo")
    }

    val sonido: Flow<Boolean> = context.dataStore.data.map { it[SONIDO_KEY] ?: true }
    val vibracion: Flow<Boolean> = context.dataStore.data.map { it[VIBRACION_KEY] ?: true }
    val correo: Flow<Boolean> = context.dataStore.data.map { it[CORREO_KEY] ?: true }

    suspend fun setSonido(value: Boolean) {
        context.dataStore.edit { it[SONIDO_KEY] = value }
    }

    suspend fun setVibracion(value: Boolean) {
        context.dataStore.edit { it[VIBRACION_KEY] = value }
    }

    suspend fun setCorreo(value: Boolean) {
        context.dataStore.edit { it[CORREO_KEY] = value }
    }


    val temaOscuro = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey("tema_oscuro")] ?: false
    }

    suspend fun setTemaOscuro(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("tema_oscuro")] = valor
        }
    }

}