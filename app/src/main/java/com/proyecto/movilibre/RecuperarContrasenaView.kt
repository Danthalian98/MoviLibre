package com.proyecto.movilibre

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RecuperarContrasena(
    show: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var correoReset by remember { mutableStateOf("") }
    var envioExitoso by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Recuperar contraseña") },
            text = {
                Column {
                    if (envioExitoso) {
                        Text(text = mensaje)
                    } else {
                        OutlinedTextField(
                            value = correoReset,
                            onValueChange = { correoReset = it },
                            label = { Text("Correo electrónico") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (envioExitoso) {
                    TextButton(onClick = {
                        envioExitoso = false
                        mensaje = ""
                        correoReset = ""
                        onDismiss()
                    }) {
                        Text("Aceptar")
                    }
                } else {
                    TextButton(onClick = {
                        if (correoReset.isNotBlank()) {
                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(correoReset)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        mensaje = "Se ha enviado un correo para restablecer tu contraseña."
                                        envioExitoso = true
                                    } else {
                                        mensaje = "Error al enviar el correo. Verifica el correo ingresado."
                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            mensaje = "Por favor ingresa tu correo electrónico."
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Enviar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    envioExitoso = false
                    mensaje = ""
                    correoReset = ""
                    onDismiss()
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}