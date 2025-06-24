package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

interface CryptoService {
    fun encrypt(text: String) : String
    fun decrypt(encryptedText: String) : String?
}