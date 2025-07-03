package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class UsuarioViewModel : ViewModel() {
    private val repository = UsuarioRepository()

    // Estado del usuario actual
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    // Estado de loading para el login
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun agregarUsuario(
        id: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        telefono: String,
        contrasena: String,
        rol: String = "empleado",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val usuario = Usuario(id, nombre, apellidoPaterno, apellidoMaterno, telefono, contrasena, rol)
        repository.agregarUsuario(usuario, onSuccess, onFailure)
    }

    fun verificarIdDisponible(id: String, onResult: (Boolean) -> Unit) {
        repository.verificarIdDisponible(id, onResult)
    }

    fun obtenerUsuarios(onResult: (List<Usuario>) -> Unit) {
        repository.obtenerUsuarios(onResult)
    }

    fun obtenerUsuarioPorId(id: String, onResult: (Usuario?) -> Unit) {
        repository.obtenerUsuarioPorId(id, onResult)
    }

    // Nueva función para login
    fun login(
        id: String,
        contrasena: String,
        onSuccess: (Usuario) -> Unit,
        onFailure: (String) -> Unit
    ) {
        _isLoading.value = true
        repository.login(
            id = id,
            contrasena = contrasena,
            onSuccess = { usuario ->
                _usuarioActual.value = usuario
                _isLoading.value = false
                onSuccess(usuario)
            },
            onFailure = { error ->
                _isLoading.value = false
                onFailure(error)
            }
        )
    }

    fun logout() {
        _usuarioActual.value = null
    }

    // Función para verificar permisos
    fun tienePermiso(permisoRequerido: String): Boolean {
        val usuario = _usuarioActual.value ?: return false
        return when (permisoRequerido) {
            "super_admin" -> usuario.rol == "super_admin"
            "admin" -> usuario.rol == "super_admin" || usuario.rol == "admin"
            "empleado" -> true // Todos tienen permisos de empleado
            else -> false
        }
    }

    fun agregarUsuarioAutoId(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        telefono: String,
        contrasena: String,
        rol: String = "empleado",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val id = (1000..9999).random().toString()
        agregarUsuario(
            id = id,
            nombre = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            telefono = telefono,
            contrasena = contrasena,
            rol = rol,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun crearSuperAdminPorDefecto() {
        // Verificar si ya existe un super admin
        repository.verificarSuperAdminExiste { existe ->
            if (!existe) {
                // Crear super admin por defecto
                agregarUsuario(
                    id = "0001",
                    nombre = "Super",
                    apellidoPaterno = "Admin",
                    apellidoMaterno = "",
                    telefono = "0000000000",
                    contrasena = "admin123",
                    rol = "super_admin",
                    onSuccess = {
                        println("Super admin creado exitosamente")
                    },
                    onFailure = { exception ->
                        println("Error al crear super admin: ${exception.message}")
                    }
                )
            }
        }

        // Verificar si ya existe un admin
        repository.verificarAdminExiste { existe ->
            if (!existe) {
                // Crear admin por defecto
                agregarUsuario(
                    id = "0002",
                    nombre = "Admin",
                    apellidoPaterno = "User",
                    apellidoMaterno = "",
                    telefono = "0000000000",
                    contrasena = "admin123",
                    rol = "admin",
                    onSuccess = {
                        println("Admin creado exitosamente")
                    },
                    onFailure = { exception ->
                        println("Error al crear admin: ${exception.message}")
                    }
                )
            }
        }
    }
}