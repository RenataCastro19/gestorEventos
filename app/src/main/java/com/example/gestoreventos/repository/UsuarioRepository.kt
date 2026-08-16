package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // La contraseña real ya vive de forma segura en Firebase Authentication.
        // Aquí la limpiamos antes de guardar para que NUNCA quede en texto plano en Firestore.
        val usuarioSinContrasena = usuario.copy(contrasena = "")
        db.collection("usuarios")
            .document(usuario.id)
            .set(usuarioSinContrasena)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Guarda un espejo mínimo {rol} en roles/{uid}, donde uid es el UID real de
    // Firebase Auth (distinto del id de 4 dígitos que usas como documento en "usuarios").
    // Las reglas de seguridad de Firestore necesitan esto para poder verificar el rol
    // del usuario que hace la petición.
    fun registrarRolParaReglas(
        uid: String,
        rol: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("roles")
            .document(uid)
            .set(mapOf("rol" to rol))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun verificarIdDisponible(id: String, onResult: (Boolean) -> Unit) {
        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { document ->
                onResult(!document.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun obtenerUsuarios(onResult: (List<Usuario>) -> Unit) {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Usuario::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun obtenerUsuarioPorId(id: String, onResult: (Usuario?) -> Unit) {
        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(Usuario::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // NOTA: se quitó la función login(id, contrasena, ...) que comparaba contraseñas en
    // texto plano contra Firestore. Estaba sin usar — UsuarioViewModel.login() ya usa
    // Firebase Auth (signInWithEmailAndPassword), que es lo correcto. Dejarla ahí era
    // riesgo sin ningún beneficio.

    fun verificarSuperAdminExiste(onResult: (Boolean) -> Unit) {
        db.collection("usuarios")
            .whereEqualTo("rol", "super_admin")
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(!snapshot.isEmpty)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun verificarAdminExiste(onResult: (Boolean) -> Unit) {
        db.collection("usuarios")
            .whereEqualTo("rol", "admin")
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(!snapshot.isEmpty)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun actualizarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val usuarioSinContrasena = usuario.copy(contrasena = "")
        db.collection("usuarios")
            .document(usuario.id)
            .set(usuarioSinContrasena)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}