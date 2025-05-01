package com.proyecto.movilibre

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthHelper {

    fun loginUser(email: String, password: String, context: Context, onResult: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
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
                    onResult(false)
                }
            }
    }

}