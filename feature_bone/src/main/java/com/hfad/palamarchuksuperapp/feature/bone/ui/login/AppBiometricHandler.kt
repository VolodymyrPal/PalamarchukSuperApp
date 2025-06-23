package com.hfad.palamarchuksuperapp.feature.bone.ui.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AppBiometricHandler(
    private val context: Context,
) {
    companion object {
        private const val TAG = "BiometricAuth"
        private const val ALLOWED_AUTHENTICATORS = DEVICE_CREDENTIAL or BIOMETRIC_STRONG or BIOMETRIC_WEAK
    }

    private val executor = ContextCompat.getMainExecutor(context)

    /**
     * Check to know which authenticators are available
     */
    fun canAuthenticate(): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(ALLOWED_AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricAvailability.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricAvailability.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricAvailability.SecurityUpdateRequired
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricAvailability.Unsupported
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricAvailability.Unknown
            else -> BiometricAvailability.Unknown
        }
    }

    /**
     * Create biometric prompt
     */
    fun createBiometricPrompt(
        fragmentActivity: FragmentActivity = context as FragmentActivity,
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit = { _, message ->
            showDefaultErrorToast(message)
        },
        onFailed: () -> Unit = { showDefaultFailedToast() }
    ): BiometricPrompt {
        return BiometricPrompt(
            fragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Аутентификация успешна")
                    Toast.makeText(context, "Аутентификация успешна!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, "Ошибка аутентификации: errorCode=$errorCode, message=$errString")
                    onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "Аутентификация не удалась")
                    onFailed()
                }
            }
        )
    }

    /**
     * Create base prompt info
     */
    fun createPromptInfo(
        title: String = "Biometric Authentication",
        subtitle: String = "Log in using your biometric credential",
        description: String? = null
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .apply { description?.let { setDescription(it) } }
            .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)
            .build()
    }

    /**
     *  Call authentication
     */
    fun authenticate(
        biometricPrompt: BiometricPrompt,
        promptInfo: BiometricPrompt.PromptInfo
    ) {
        if (canAuthenticate() == BiometricAvailability.Available) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            Log.w(TAG, "Биометрическая аутентификация недоступна: ${canAuthenticate()}")
            Toast.makeText(
                context,
                "Биометрическая аутентификация недоступна",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDefaultErrorToast(message: String) {
        Toast.makeText(context, "Ошибка аутентификации: $message", Toast.LENGTH_SHORT).show()
    }

    private fun showDefaultFailedToast() {
        Toast.makeText(
            context,
            "Аутентификация не удалась. Попробуйте снова.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * Different states of biometric availability
 */
sealed class BiometricAvailability {
    object Available : BiometricAvailability()
    object NoHardware : BiometricAvailability()
    object HardwareUnavailable : BiometricAvailability()
    object NoneEnrolled : BiometricAvailability()
    object SecurityUpdateRequired : BiometricAvailability()
    object Unsupported : BiometricAvailability()
    object Unknown : BiometricAvailability()
}