package com.example.gestoreventos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.InputStream
import android.graphics.BitmapFactory
import com.example.gestoreventos.R
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BlackSurface
import com.example.gestoreventos.ui.theme.BlackBorder
import com.example.gestoreventos.ui.theme.ErrorRed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gestoreventos.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var id by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val isLoading by usuarioViewModel.isLoading.collectAsState()

    // Cargar logo desde raw
    val logoBitmap = remember {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.logo_caruma)
        BitmapFactory.decodeStream(inputStream)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBlack),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    state = scrollState,
                    enabled = true
                )
                .padding(top = 48.dp, bottom = 80.dp)
        ) {
            // Logo real de la marca
            if (logoBitmap != null) {
                Image(
                    bitmap = logoBitmap.asImageBitmap(),
                    contentDescription = "Logo Caruma",
                    modifier = Modifier
                        .size(width = 280.dp, height = 185.dp)
                        .padding(bottom = 40.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .widthIn(max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandGold,
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                OutlinedTextField(
                    value = id,
                    onValueChange = {
                        id = it
                        error = null
                    },
                    label = { Text("ID de usuario") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BlackSurface,
                        unfocusedContainerColor = BlackSurface,
                        focusedBorderColor = BrandGold,
                        unfocusedBorderColor = BlackBorder,
                        focusedLabelColor = BrandGold,
                        unfocusedLabelColor = BlackBorder,
                        cursorColor = BrandGold,
                        focusedTextColor = BrandGold,
                        unfocusedTextColor = BrandGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        error = null
                    },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BlackSurface,
                        unfocusedContainerColor = BlackSurface,
                        focusedBorderColor = BrandGold,
                        unfocusedBorderColor = BlackBorder,
                        focusedLabelColor = BrandGold,
                        unfocusedLabelColor = BlackBorder,
                        cursorColor = BrandGold,
                        focusedTextColor = BrandGold,
                        unfocusedTextColor = BrandGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
                if (error != null) {
                    Text(
                        text = error ?: "",
                        color = ErrorRed,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (id.isBlank() || contrasena.isBlank()) {
                            error = "Completa todos los campos"
                            return@Button
                        }

                        usuarioViewModel.login(
                            id = id,
                            contrasena = contrasena,
                            onSuccess = { usuario ->
                                when (usuario.rol) {
                                    "super_admin" -> navController.navigate("superadmin_home")
                                    "admin" -> navController.navigate("admin_home")
                                    "empleado" -> navController.navigate("empleado_home")
                                }
                            },
                            onFailure = { errorMessage ->
                                error = errorMessage
                            }
                        )
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isLoading) "Cargando..." else "Entrar",
                        color = BrandBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}