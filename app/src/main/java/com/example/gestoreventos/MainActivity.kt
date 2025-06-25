package com.example.gestoreventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestoreventos.ui.theme.GestorEventosTheme
import com.example.gestoreventos.view.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorEventosTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home_superadmin",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home_superadmin") {
                            HomeScreenSuperAdmin(
                                onMobiliarioClick = { navController.navigate("mobiliario_list") },
                                onEmpleadosClick = { navController.navigate("empleados_list") },
                                onEventosClick = { navController.navigate("eventos_list") },
                                onServiciosClick = { navController.navigate("servicios_list") }
                            )
                        }
                        composable("mobiliario_list") {
                            MobiliarioListScreen(
                                onAgregarMobiliarioClick = { navController.navigate("agregar_mobiliario") },
                                onAgregarCategoriaClick = { navController.navigate("agregar_categoria_mobiliario") }
                            )
                        }
                        composable("agregar_mobiliario") {
                            AgregarMobiliarioForm()
                        }
                        composable("agregar_categoria_mobiliario") {
                            AgregarCategoriaMobiliarioForm()
                        }
                        composable("empleados_list") {
                            EmpleadosListScreen(
                                onAgregarEmpleadoClick = { navController.navigate("registro_usuario") }
                            )
                        }
                        composable("registro_usuario") {
                            RegistroUsuarioScreen()
                        }
                        composable("eventos_list") {
                            EventosListScreen(
                                onAgregarEventoClick = { navController.navigate("agregar_evento") }
                            )
                        }
                        composable("agregar_evento") {
                            AgregarEventoForm()
                        }
                        composable("servicios_list") {
                            ServiciosListScreen(
                                onAgregarServicioClick = { navController.navigate("agregar_servicio") }
                            )
                        }
                        composable("agregar_servicio") {
                            AgregarServicioForm()
                        }
                    }
                }
            }
        }
    }
}
