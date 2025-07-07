package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.GrayLight
import com.example.gestoreventos.view.ElegantButton

@Composable
fun HomeScreenSuperAdmin(
    usuarioActual: Usuario,
    onMobiliarioClick: () -> Unit = {},
    onEmpleadosClick: () -> Unit = {},
    onEventosClick: () -> Unit = {},
    onServiciosClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Mensaje de bienvenida
            Text(
                text = "Bienvenido, ${usuarioActual.nombre}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Título principal
            Text(
                text = "Gestor de Eventos",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Panel de Administración",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Botones elegantes
            ElegantButton(
                text = "Mobiliario",
                onClick = onMobiliarioClick,
                modifier = Modifier.fillMaxWidth()
            )

            ElegantButton(
                text = "Empleados",
                onClick = onEmpleadosClick,
                modifier = Modifier.fillMaxWidth()
            )

            ElegantButton(
                text = "Eventos",
                onClick = onEventosClick,
                modifier = Modifier.fillMaxWidth()
            )

            ElegantButton(
                text = "Servicios",
                onClick = onServiciosClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de logout
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = Color.Red.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.1f),
                    contentColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}


