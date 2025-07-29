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
import androidx.navigation.navArgument
import androidx.compose.runtime.*
import com.example.gestoreventos.ui.theme.GestorEventosTheme
import com.example.gestoreventos.view.*
import com.example.gestoreventos.viewmodel.EventoViewModel
import com.example.gestoreventos.viewmodel.UsuarioViewModel
import com.example.gestoreventos.viewmodel.SuperAdminViewModel
import com.example.gestoreventos.model.Evento
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // BLOQUE TEMPORAL: Crear superadmin en FirebaseAuth
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val email = "6933@miapp.com"
        val password = "caruma"
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Superadmin creado en FirebaseAuth")
                } else {
                    println("Error al crear superadmin: ${task.exception?.message}")
                }
            }
        // FIN BLOQUE TEMPORAL

        enableEdgeToEdge()
        setContent {
            GestorEventosTheme {
                val navController = rememberNavController()
                val usuarioViewModel: UsuarioViewModel = viewModel()
                val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
                val eventoViewModel: EventoViewModel = viewModel()
                val eventos by eventoViewModel.eventos.collectAsState()

                // Redirección automática tras login según rol
                LaunchedEffect(usuarioActual) {
                    usuarioActual?.let { usuario ->
                        when (usuario.rol) {
                            "super_admin" -> navController.navigate("superadmin_home")
                            "admin" -> navController.navigate("admin_home")
                            "empleado" -> navController.navigate("empleado_home")
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Pantalla de Login
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                usuarioViewModel = usuarioViewModel
                            )
                        }

                        // Rutas protegidas - solo para super admin
                        composable("superadmin_home") {
                            if (usuarioActual?.rol == "super_admin") {
                                HomeScreenSuperAdmin(
                                    usuarioActual = usuarioActual!!,
                                    onMobiliarioClick = { navController.navigate("mobiliario_list") },
                                    onEmpleadosClick = { navController.navigate("empleados_list") },
                                    onEventosClick = { navController.navigate("eventos_list") },
                                    onServiciosClick = { navController.navigate("servicios_list") },
                                    onLogoutClick = {
                                        usuarioViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            } else {
                                // Redirigir al login si no es super admin
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // Rutas protegidas - para admin (sin acceso a empleados)
                        composable("admin_home") {
                            if (usuarioActual?.rol == "admin") {
                                HomeScreenAdmin(
                                    usuarioActual = usuarioActual!!,
                                    onMobiliarioClick = { navController.navigate("mobiliario_list") },
                                    onEmpleadosClick = { navController.navigate("empleados_list_admin") },
                                    onEventosClick = { navController.navigate("eventos_list") },
                                    onServiciosClick = { navController.navigate("servicios_list") },
                                    onLogoutClick = {
                                        usuarioViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            } else {
                                // Redirigir al login si no es admin
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        // Rutas protegidas - accesibles para super admin y admin
                        composable("mobiliario_list") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                MobiliarioListScreen(
                                    onAgregarMobiliarioClick = { navController.navigate("agregar_mobiliario") },
                                    onAgregarCategoriaClick = { navController.navigate("agregar_categoria_mobiliario") }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("agregar_mobiliario") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                AgregarMobiliarioForm()
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("agregar_categoria_mobiliario") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                AgregarCategoriaMobiliarioForm()
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        // Rutas protegidas - solo para super admin (empleados)
                        composable("empleados_list") {
                            if (usuarioActual?.rol == "super_admin") {
                                val superAdminViewModel: SuperAdminViewModel = viewModel()

                                // Establecer el usuario actual en el ViewModel
                                LaunchedEffect(usuarioActual) {
                                    usuarioActual?.let { usuario ->
                                        superAdminViewModel.establecerUsuarioActual(usuario)
                                    }
                                }

                                EmpleadosListScreen(
                                    onAgregarEmpleadoClick = { navController.navigate("registro_usuario") },
                                    viewModel = superAdminViewModel
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // Rutas protegidas - para admin (empleados sin agregar)
                        composable("empleados_list_admin") {
                            if (usuarioActual?.rol == "admin") {
                                val superAdminViewModel: SuperAdminViewModel = viewModel()

                                // Establecer el usuario actual en el ViewModel
                                LaunchedEffect(usuarioActual) {
                                    usuarioActual?.let { usuario ->
                                        superAdminViewModel.establecerUsuarioActual(usuario)
                                    }
                                }

                                EmpleadosListScreenAdmin(viewModel = superAdminViewModel)
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("registro_usuario") {
                            if (usuarioActual?.rol == "super_admin") {
                                RegistroUsuarioScreen()
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("eventos_list") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                EventosListScreen(
                                    onAgregarEventoClick = { navController.navigate("agregar_evento") },
                                    onEditarEventoClick = { eventoId ->
                                        navController.navigate("editar_evento/$eventoId")
                                    },
                                    onCalendarioClick = { navController.navigate("calendario") },
                                    currentUser = usuarioActual
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("agregar_evento") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                AgregarEventoForm()
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable(
                            route = "editar_evento/{eventoId}",
                            arguments = listOf(
                                navArgument("eventoId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                val eventoId = backStackEntry.arguments?.getString("eventoId")
                                val eventoViewModel: EventoViewModel = viewModel()
                                var eventoAEditar by remember { mutableStateOf<Evento?>(null) }

                                LaunchedEffect(eventoId) {
                                    if (eventoId != null) {
                                        eventoViewModel.obtenerEventoPorId(eventoId) { evento ->
                                            eventoAEditar = evento
                                        }
                                    }
                                }

                                AgregarEventoForm(eventoAEditar = eventoAEditar)
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("servicios_list") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                ServiciosListScreen(
                                    onAgregarServicioClick = { navController.navigate("agregar_servicio") }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("agregar_servicio") {
                            if (usuarioActual?.rol == "super_admin" || usuarioActual?.rol == "admin") {
                                AgregarServicioForm()
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // Ruta del calendario - accesible para todos los roles
                        composable("calendario") {
                            CalendarioScreen()
                        }
                        // Rutas protegidas - para empleados
                        composable("empleado_home") {
                            if (usuarioActual?.rol == "empleado") {
                                HomeScreenEmpleado(
                                    usuarioActual = usuarioActual!!,
                                    onMisEventosClick = { navController.navigate("eventos_list_empleado") },
                                    onLogoutClick = {
                                        usuarioViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable("eventos_list_empleado") {
                            if (usuarioActual?.rol == "empleado") {
                                val eventosEmpleado = eventos.filter { it.listaIdsEmpleados.contains(usuarioActual!!.id) }
                                EventosEmpleadoListScreen(
                                    eventosEmpleado = eventosEmpleado,
                                    usuarioActual = usuarioActual!!,
                                    onBack = { navController.popBackStack() },
                                    onCalendarioClick = { navController.navigate("calendario") }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
