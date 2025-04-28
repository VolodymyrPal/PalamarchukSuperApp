package com.hfad.palamarchuksuperapp.core.ui.composables

import android.icu.text.NumberFormat
import java.util.Locale

fun Number.formatTrim(
    numOfDigital: Int = 2,
    locale: Locale = Locale.getDefault(),
): String {
    return NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = numOfDigital
        minimumFractionDigits = 0
        isGroupingUsed = true
    }.format(this)
}