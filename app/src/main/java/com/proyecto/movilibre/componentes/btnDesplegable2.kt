package com.proyecto.movilibre.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proyecto.movilibre.R
import com.proyecto.movilibre.data.UserPreferences
import kotlinx.coroutines.launch


@Composable
fun btnDesplegable2(
    estado: Boolean,
    navController: NavHostController
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val sonido by prefs.sonido.collectAsState(initial = true)
    val vibracion by prefs.vibracion.collectAsState(initial = true)
    val correo by prefs.correo.collectAsState(initial = true)
    val temaOscuro by prefs.temaOscuro.collectAsState(initial = false)

    var expandedNotificaciones by remember { mutableStateOf(false) }
    var expandedUsuario by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    Column {
        // Sección: Notificaciones



        Button(
            onClick = { expandedNotificaciones = !expandedNotificaciones },

            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.Notif),
                    color = colorScheme.onSurface,
                    fontSize = 22.sp
                )
                Icon(
                    painter = painterResource(
                        id = if (expandedNotificaciones)
                            R.drawable.ic_arrow_drop_down
                        else
                            R.drawable.ic_arrow_drop_up
                    ),
                    contentDescription = null,
                    tint = colorScheme.onSurface
                )
            }
        }

        if (expandedNotificaciones) {
            val notificaciones = listOf("Sonido", "Vibración", "Correo")
            val estados = listOf(sonido, vibracion, correo)

            notificaciones.forEachIndexed { index, texto ->
                val checked = estados[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = texto,
                        fontSize = 18.sp,
                        color = colorScheme.onBackground
                    )
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            scope.launch {
                                when (index) {
                                    0 -> prefs.setSonido(it)
                                    1 -> prefs.setVibracion(it)
                                    2 -> prefs.setCorreo(it)
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.primary,
                            uncheckedThumbColor = colorScheme.outline,
                            checkedTrackColor = colorScheme.primary.copy(alpha = 0.5f),
                            uncheckedTrackColor = colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección: Usuario
        Button(
            onClick = { expandedUsuario = !expandedUsuario },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)


        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.strUser),
                    color = colorScheme.onSurface,
                    fontSize = 22.sp
                )
                Icon(
                    painter = painterResource(
                        id = if (expandedUsuario)
                            R.drawable.ic_arrow_drop_down
                        else
                            R.drawable.ic_arrow_drop_up
                    ),
                    contentDescription = null,
                    tint = colorScheme.onSurface
                )
            }
        }

        if (expandedUsuario) {
            if (!estado) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            stringResource(id = R.string.btnIniciarS),
                            fontSize = 22.sp,
                            color = colorScheme.onPrimary
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                    Button(
                        onClick = { /* navegar a perfil */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceVariant)
                    ) {
                        Text("Perfil", fontSize = 18.sp, color = colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { navController.navigate("cambiar_contrasena") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceVariant)
                    ) {
                        Text("Cambiar contraseña", fontSize = 18.sp, color = colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                prefs.setTemaOscuro(false)
                            }
                            Firebase.auth.signOut()

                            navController.navigate("mainv") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error,
                            contentColor = colorScheme.onError
                        )
                    ) {
                        Text("Cerrar sesión", fontSize = 18.sp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Tema oscuro", fontSize = 18.sp, color = colorScheme.onBackground)
                        Switch(
                            checked = temaOscuro,
                            onCheckedChange = {
                                scope.launch { prefs.setTemaOscuro(it) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colorScheme.primary,
                                uncheckedThumbColor = colorScheme.outline,
                                checkedTrackColor = colorScheme.primary.copy(alpha = 0.5f),
                                uncheckedTrackColor = colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewbtnDesplegable2() {
    val fakeNavController = rememberNavController()
    btnDesplegable2(
        estado = false,
        navController = fakeNavController
    )
}