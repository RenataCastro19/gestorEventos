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
        db.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
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

    // Nueva función para login
    fun login(
        id: String,
        contrasena: String,
        onSuccess: (Usuario) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    if (usuario != null && usuario.contrasena == contrasena) {
                        onSuccess(usuario)
                    } else {
                        onFailure("Contraseña incorrecta")
                    }
                } else {
                    onFailure("Usuario no encontrado")
                }
            }
            .addOnFailureListener {
                onFailure("Error al conectar con la base de datos")
            }
    }

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
        db.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}