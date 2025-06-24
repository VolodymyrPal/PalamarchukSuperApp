package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.CryptoService
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.KeyStoreException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class KeystoreCryptoServiceImpl @Inject constructor() : CryptoService {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "my_datastore_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun getSecretKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: generateKey()
    }

    private fun generateKey(): SecretKey {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
    }

    /**
     * Cipher encrypts the data and returns the encrypted string
     * @param cipher - create cipher object for encryption + init it with mode and key.
     * @param iv - small random pieces for better encryption
     * @param ciphertext - encrypted data in bytes
     * @param encryptedString - encrypted string to return
     */
    override fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        val encryptedString = Base64.encodeToString(iv + ciphertext, Base64.NO_WRAP)
        return encryptedString
    }

    override fun decrypt(encryptedText: String): String? {
        val decryptedString = try {
            val data = Base64.decode(encryptedText, Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = data.copyOfRange(0, 12)
            val ciphertext = data.copyOfRange(12, data.size)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(ciphertext), Charsets.UTF_8)
        } catch (e: GeneralSecurityException) {
            null
        } catch (e: KeyStoreException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }

        return decryptedString
    }
}