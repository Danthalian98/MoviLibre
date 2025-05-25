package com.proyecto.movilibre.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.CreateCredentialException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Importar para usar await()

class AuthHelper {

    fun loginUser(
        email: String,
        password: String,
        context: Context,
        onResult: (success: Boolean, isVerified: Boolean) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // PASO CRUCIAL: Recargar el usuario para obtener el estado más reciente de isEmailVerified
                    user?.reload() // Inicia la recarga de forma asíncrona
                        ?.addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                val isVerified = user.isEmailVerified
                                onResult(true, isVerified) // Devolver el estado actualizado
                            } else {
                                // Error al recargar el usuario, tratar como no verificado o error de login
                                Log.e("AuthHelper", "Error al recargar el usuario: ${reloadTask.exception?.message}")
                                onResult(true, false) // Podrías considerar esto como un login exitoso pero con un problema de verificación. O false si prefieres.
                            }
                        }
                } else {
                    onResult(false, false) // Login fallido
                }
            }
    }

    fun registerUser(
        nombre: String,
        email: String,
        password: String,
        context: Context,
        onResult: (Boolean) -> Unit // Considera cambiar el tipo de onResult si quieres devolver también si el correo fue enviado
    ) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification() // Ya está aquí, lo cual es correcto
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                Log.d("AuthHelper", "Correo de verificación enviado a: ${user.email}")
                                // Podrías añadir un Toast aquí o manejarlo en el Registroview
                            } else {
                                Log.e("AuthHelper", "Error al enviar correo de verificación: ${emailTask.exception?.message}")
                            }
                        }

                    val userId = auth.currentUser?.uid
                    val userFirestoreData = hashMapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "notificaciones" to true,
                        "emailVerificado" to false // Añadir este campo para control adicional si lo necesitas en Firestore
                    )

                    if (userId != null) {
                        db.collection("usuarios").document(userId)
                            .set(userFirestoreData)
                            .addOnSuccessListener {
                                onResult(true) // Registro exitoso, incluyendo el envío del correo
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error guardando usuario en Firestore: ", e)
                                // Aunque el usuario de Auth fue creado, Firestore falló. Podrías eliminar el usuario de Auth aquí.
                                user?.delete() // Opcional: limpiar si Firestore falla
                                onResult(false)
                            }
                    } else {
                        Log.e("Auth", "userId es null después del registro")
                        // Si userId es null, no se pudo crear el usuario de Auth.
                        onResult(false)
                    }
                } else {
                    Log.e("FirebaseAuth", "Error en registro: ", task.exception)
                    Toast.makeText(context, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    onResult(false)
                }
            }
    }

    fun saveCredential(correo: String, password: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val credentialManager = CredentialManager.create(context)

            val createRequest = CreatePasswordRequest(
                id = correo,
                password = password
            )

            try {
                credentialManager.createCredential(
                    request = createRequest,
                    context = context
                )
                Log.d("Credenciales", "Credencial guardada con éxito")
            } catch (e: CreateCredentialException) {
                Log.e("Credenciales", "Error al guardar credencial: ${e.message}")
            }
        }
    }

    suspend fun getSavedCredential(context: Context): Pair<String, String>? {
        val credentialManager = CredentialManager.create(context)
        return try {
            val result = credentialManager.getCredential(
                request = GetCredentialRequest(listOf(GetPasswordOption())),
                context = context
            )
            val credential = result.credential as? PasswordCredential
            credential?.let { it.id to it.password }
        } catch (e: Exception) {
            Log.e("Credenciales", "Error al obtener credenciales: ${e.message}")
            null
        }
    }
}