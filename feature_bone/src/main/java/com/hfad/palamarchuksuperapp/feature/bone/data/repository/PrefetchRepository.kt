package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import kotlinx.coroutines.flow.MutableStateFlow

abstract class PrefetchRepository {
    val fetchDone : MutableStateFlow<Boolean> = MutableStateFlow(false)
    fun init() {
        fetchDone.value = true
    }
}