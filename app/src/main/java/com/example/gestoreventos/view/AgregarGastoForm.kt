package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.ui.theme.WarningGold
import com.example.gestoreventos.ui.theme.WarningGoldBg
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.ui.theme.SuccessGreen
import com.example.gestoreventos.viewmodel.GastoViewModel

private val productosFrecuentes = listOf(
    "Mayonesa", "Caldo de pollo", "Grano amarillo", "Grano blanco", "Mantequilla", "Cerveza"
)

@Composable
fun AgregarGastoForm(
    usuarioActual: Usuario,
    gastoViewModel: GastoViewModel = GastoViewModel(),
    onGuardarExitoso: () -> Unit = {}
) {
    val context = LocalContext.current
    var producto by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }

    val nombreUsuario = "${usuarioActual.nombre} ${usuarioActual.apellidoPaterno}".trim()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Registrar Gasto",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "PRODUCTOS FRECUENTES",
            style = MaterialTheme.typography.labelSmall.copy(
                color = BrandGold.copy(alpha = 0.7f)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productosFrecuentes) { nombreProducto ->
                val seleccionado = producto == nombreProducto
                Card(
                    modifier = Modifier
                        .clickable { producto = nombreProducto }
                        .border(
                            width = 1.dp,
                            color = if (seleccionado) BrandGold else CardBorder,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) WarningGoldBg else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = nombreProducto,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (seleccionado) WarningGold else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = producto,
            onValueChange = { producto = it },
            label = { Text("Producto") },
            placeholder = { Text("Ej. Queso, gas, publicidad Instagram...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandGold,
                focusedLabelColor = BrandGold,
                cursorColor = BrandGold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = montoTexto,
            onValueChange = {
                val filtrado = it.filter { c -> c.isDigit() || c == '.' }
                if (filtrado.count { c -> c == '.' } <= 1) {
                    montoTexto = filtrado
                }
            },
            label = { Text("Monto") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandGold,
                focusedLabelColor = BrandGold,
                cursorColor = BrandGold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = { },
            readOnly = true,
            label = { Text("Fecha de la compra") },
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha",
                    tint = BrandGold,
                    modifier = Modifier.clickable {
                        showDatePicker(context) { seleccionada -> fecha = seleccionada }
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandGold,
                focusedLabelColor = BrandGold,
                cursorColor = BrandGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker(context) { seleccionada -> fecha = seleccionada }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quién registra el gasto se toma del usuario con la sesión abierta, no se escribe a mano
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = CardBorder, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "REGISTRADO POR",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BrandGold.copy(alpha = 0.7f)
                )
            )
            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val monto = montoTexto.toDoubleOrNull()
                if (producto.isBlank() || monto == null || monto <= 0.0 || fecha.isBlank()) {
                    mensaje = "Completa producto, monto y fecha"
                    esError = true
                    return@Button
                }

                guardando = true
                val mes = calcularMesDesdeFecha(fecha)

                gastoViewModel.agregarGasto(
                    producto = producto.trim(),
                    monto = monto,
                    fecha = fecha,
                    mes = mes,
                    idUsuario = usuarioActual.id,
                    nombreUsuario = nombreUsuario,
                    onSuccess = {
                        guardando = false
                        mensaje = "Gasto registrado correctamente"
                        esError = false
                        producto = ""
                        montoTexto = ""
                        fecha = ""
                        onGuardarExitoso()
                    },
                    onFailure = { e ->
                        guardando = false
                        mensaje = "Error al registrar el gasto: ${e.message}"
                        esError = true
                    }
                )
            },
            enabled = !guardando,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGold,
                contentColor = BrandBlack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(if (guardando) "Guardando..." else "Registrar Gasto")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensaje,
                color = if (esError) ErrorRed else SuccessGreen
            )
        }
    }
}

// Convierte "15/08/2026" a "2026-08" para poder filtrar por mes más adelante
private fun calcularMesDesdeFecha(fecha: String): String {
    return try {
        val partes = fecha.split("/")
        val mes = partes[1].padStart(2, '0')
        val anio = partes[2]
        "$anio-$mes"
    } catch (e: Exception) {
        ""
    }
}