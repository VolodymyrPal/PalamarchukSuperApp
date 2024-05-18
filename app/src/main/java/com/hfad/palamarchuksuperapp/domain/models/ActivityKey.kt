package com.hfad.palamarchuksuperapp.domain.models

sealed class ActivityKey {
    object ActivityXML : ActivityKey()
    object ActivityCompose : ActivityKey()
}