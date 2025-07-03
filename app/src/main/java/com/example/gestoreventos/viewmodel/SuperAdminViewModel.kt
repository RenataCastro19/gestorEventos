package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.repository.EventoRepository
import com.example.gestoreventos.repository.UsuarioRepository
import com.example.gestoreventos.repository.MobiliarioRepository
import com.example.gestoreventos.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SuperAdminViewModel : ViewModel() {

    private val eventoRepository = EventoRepository()
    private val usuarioRepository = UsuarioRepository()
    private val mobiliarioRepository = MobiliarioRepository()
    private val servicioRepository = ServicioRepository()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    private val _empleados = MutableStateFlow<List<Usuario>>(emptyList())
    val empleados: StateFlow<List<Usuario>> = _empleados.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _mobiliario = MutableStateFlow<List<Mobiliario>>(emptyList())
    val mobiliario: StateFlow<List<Mobiliario>> = _mobiliario.asStateFlow()

    private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
    val servicios: StateFlow<List<Servicio>> = _servicios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        _isLoading.value = true
        cargarEventos()
        cargarEmpleados()
        cargarMobiliario()
        cargarServicios()
        _isLoading.value = false
    }

    private fun cargarEventos() {
        eventoRepository.obtenerEventos { listaEventos ->
            _eventos.value = listaEventos.sortedBy { it.fecha }
        }
    }

    private fun cargarEmpleados() {
        usuarioRepository.obtenerUsuarios { listaUsuarios ->
            val usuarioActual = _usuarioActual.value
            _empleados.value = listaUsuarios.filter {
                (it.rol == "empleado" || it.rol == "admin" || it.rol == "super_admin") &&
                        it.id != usuarioActual?.id // Excluir al usuario actual
            }
        }
    }

    private fun cargarMobiliario() {
        mobiliarioRepository.obtenerMobiliarios { listaMobiliario ->
            _mobiliario.value = listaMobiliario
        }
    }

    private fun cargarServicios() {
        servicioRepository.obtenerServicios { listaServicios ->
            _servicios.value = listaServicios
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun actualizarDatos() {
        cargarDatos()
    }

    fun establecerUsuarioActual(usuario: Usuario) {
        _usuarioActual.value = usuario
        cargarEmpleados() // Recargar empleados para filtrar el usuario actual
    }

    fun inhabilitarUsuario(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val usuarioInhabilitado = usuario.copy(estado = "inhabilitado")
        usuarioRepository.actualizarUsuario(
            usuario = usuarioInhabilitado,
            onSuccess = {
                cargarEmpleados() // Recargar la lista después de inhabilitar
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    fun habilitarUsuario(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val usuarioHabilitado = usuario.copy(estado = "activo")
        usuarioRepository.actualizarUsuario(
            usuario = usuarioHabilitado,
            onSuccess = {
                cargarEmpleados() // Recargar la lista después de habilitar
                onSuccess()
            },
            onFailure = onFailure
        )
    }
}