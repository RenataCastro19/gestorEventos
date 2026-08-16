package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.auth.FirebaseAuth

class UsuarioViewModel : ViewModel() {
    private val repository = UsuarioRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

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

    // Registro de usuario usando FirebaseAuth
    fun registrarUsuarioConAuth(
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
        val email = "$id@miapp.com"

        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                val usuario = Usuario(id, nombre, apellidoPaterno, apellidoMaterno, telefono, contrasena, rol)
                repository.agregarUsuario(
                    usuario = usuario,
                    onSuccess = {
                        // Espejo del rol en roles/{uid} para que las Reglas de Seguridad de
                        // Firestore puedan verificarlo (ver UsuarioRepository.registrarRolParaReglas)
                        if (uid != null) {
                            repository.registrarRolParaReglas(
                                uid = uid,
                                rol = rol,
                                onSuccess = {},
                                onFailure = {}
                            )
                        }
                        onSuccess()
                    },
                    onFailure = onFailure
                )
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Login usando FirebaseAuth con ID y contraseña
    fun login(
        id: String,
        contrasena: String,
        onSuccess: (Usuario) -> Unit,
        onFailure: (String) -> Unit
    ) {
        _isLoading.value = true
        val email = "$id@miapp.com"
        auth.signInWithEmailAndPassword(email, contrasena)
            .addOnSuccessListener {
                repository.obtenerUsuarioPorId(id) { usuario ->
                    _isLoading.value = false
                    if (usuario != null) {
                        _usuarioActual.value = usuario
                        onSuccess(usuario)
                    } else {
                        onFailure("Usuario no encontrado en Firestore")
                    }
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                onFailure("ID o contraseña incorrectos")
            }
    }

    fun logout() {
        _usuarioActual.value = null
    }

    fun tienePermiso(permisoRequerido: String): Boolean {
        val usuario = _usuarioActual.value ?: return false
        return when (permisoRequerido) {
            "super_admin" -> usuario.rol == "super_admin"
            "admin" -> usuario.rol == "super_admin" || usuario.rol == "admin"
            "empleado" -> true
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
}