package com.hfad.palamarchuksuperapp.ui.reusable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * Расширение Modifier для создания эффекта мерцания (shimmer) под адаптивный дизайн
 * @param durationMillis продолжительность одного цикла анимации в миллисекундах
 */
@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    // Создаем бесконечную анимацию
    val transition = rememberInfiniteTransition(label = "Shimmer transition")

    // Настраиваем параметры анимации перемещения
    val shimmerFloatAnimation = transition.animateFloat(
        // Анимация для изменения значения Float
        initialValue = 0f,                                         // Начальное значение
        targetValue = 1f,                                         // Целевое значение до которого начальное движется
        animationSpec = infiniteRepeatable(
            // Настраиваем бесконечную анимацию
            animation = tween(
                // Тип анимации и характеристики
                durationMillis = durationMillis,                   // Продолжительность одного цикла анимации
                easing = LinearEasing,                             // Тип движения анимации
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    // Применяем эффект мерцания, рисуя градиент
    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                // Определяем цвета градиента с разной прозрачностью
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.1f),
                    Color.LightGray.copy(alpha = 0.6f),
                    Color.LightGray.copy(alpha = 0.1f),
                ),
                start = Offset(x = translateAnimation.value, y = translateAnimation.value),
                end = Offset(
                    x = translateAnimation.value + 100f,
                    y = translateAnimation.value + 100f
                ),
            )
        )
    }
}