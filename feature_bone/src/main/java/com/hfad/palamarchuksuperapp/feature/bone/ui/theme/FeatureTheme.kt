package com.hfad.palamarchuksuperapp.feature.bone.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType



@Composable
fun financeStatusColor(
    status: TransactionType,
): Color {
    val isDark = isSystemInDarkTheme()
    return when (status) {
        TransactionType.CREDIT -> if (!isDark) Color(0xFF43A047) else Color(0xFF66BB6A)
        TransactionType.DEBIT -> if (!isDark) Color(0xFF90CAF9) else Color(0xFF90CAF9)
    }
}