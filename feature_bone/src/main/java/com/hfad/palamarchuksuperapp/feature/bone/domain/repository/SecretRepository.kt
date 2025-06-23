package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

interface SecretRepository {
    fun encrypt(text: String) : String
    fun decrypt(encryptedText: String) : String?
}