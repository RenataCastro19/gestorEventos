package com.example.gestoreventos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.InputStream
import android.graphics.BitmapFactory
import com.example.gestoreventos.R
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
            .background(Color(0xFFF8F8F8)),
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
                .padding(top = 20.dp, bottom = 200.dp) // Más padding inferior para forzar scroll
        ) {
            // Logo grande y centrado
            if (logoBitmap != null) {
                Image(
                    bitmap = logoBitmap.asImageBitmap(),
                    contentDescription = "Logo Caruma",
                    modifier = Modifier
                        .size(width = 500.dp, height = 330.dp)
                        .padding(bottom = 20.dp)
                )
            }
            // Card del formulario
            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Espacio superior para centrar el contenido
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        modifier = Modifier.padding(bottom = 24.dp)
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFFD4AF37),
                            cursorColor = Color(0xFFD4AF37)
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFFD4AF37),
                            cursorColor = Color(0xFFD4AF37)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                    if (error != null) {
                        Text(
                            text = error ?: "",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    Button(
                        onClick = {
                            focusManager.clearFocus() // Ocultar teclado
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
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isLoading) "Cargando..." else "Entrar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Espacio inferior para centrar el contenido
                    Spacer(modifier = Modifier.height(20.dp))

                    // Espacio adicional para forzar scroll
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}