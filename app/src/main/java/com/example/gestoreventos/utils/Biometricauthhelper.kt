package com.example.gestoreventos.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricAuthHelper {

    // Revisa si el dispositivo tiene sensor de huella/rostro configurado y disponible.
    // Si el celular no tiene sensor, o la persona nunca configuró una huella en Android,
    // esto regresa false y la app simplemente no ofrece la opción.
    fun biometriaDisponible(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun mostrarPrompt(
        activity: FragmentActivity,
        titulo: String = "Inicia sesión con tu huella",
        onExito: () -> Unit,
        onError: (String) -> Unit,
        onCancelado: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onExito()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                ) {
                    onCancelado()
                } else {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                // La huella no coincidió en este intento — el sistema sigue esperando
                // otro intento, no cerramos el prompt aquí.
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titulo)
            .setSubtitle("Caruma Snacks Bar")
            .setNegativeButtonText("Usar contraseña")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        prompt.authenticate(info)
    }
}