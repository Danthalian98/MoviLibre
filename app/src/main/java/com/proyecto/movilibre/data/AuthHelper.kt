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
                    val isVerified = auth.currentUser?.isEmailVerified ?: false
                    onResult(true, isVerified)
                } else {
                    onResult(false, false)
                }
            }
    }



    fun registerUser(
        nombre: String,
        email: String,
        password: String,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // ✅ Enviar verificación de correo
                    auth.currentUser?.sendEmailVerification()

                    val userId = auth.currentUser?.uid
                    val user = hashMapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "notificaciones" to true
                    )

                    if (userId != null) {
                        db.collection("usuarios").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                onResult(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error guardando usuario: ", e)
                                onResult(false)
                            }
                    } else {
                        Log.e("Auth", "userId es null después del registro")
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