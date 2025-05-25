
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

    val temaOscuro by prefs.temaOscuro.collectAsState(initial = false)

    val user = Firebase.auth.currentUser
    val emailVerified = user?.isEmailVerified ?: false

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón PERFIL (solo activo si el correo fue verificado)
        Button(
            onClick = { navController.navigate("perfil") },
            enabled = emailVerified,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
        ) {
            Text("Perfil", fontSize = 20.sp, color = colorScheme.onSurface)
        }

        // SWITCH TEMA OSCURO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(50))
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tema oscuro", fontSize = 20.sp, color = colorScheme.onBackground)
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


@Preview
@Composable
fun PreviewbtnDesplegable2() {
    val fakeNavController = rememberNavController()
    btnDesplegable2(
        estado = false,
        navController = fakeNavController
    )
}
