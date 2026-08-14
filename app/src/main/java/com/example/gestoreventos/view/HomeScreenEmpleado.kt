package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.ui.theme.ErrorRedBg

@Composable
fun HomeScreenEmpleado(
    usuarioActual: Usuario,
    onMisEventosClick: () -> Unit,
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido, ${usuarioActual.nombre}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        ElegantButton(
            text = "Mis eventos",
            onClick = onMisEventosClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de logout
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = CardBorder,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRedBg,
                contentColor = ErrorRed
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