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
        label = "Shimmer animation",
    )

    // Применяем эффект мерцания, рисуя градиент
    return graphicsLayer {
        alpha = 0.95f  // Небольшая прозрачность для смягчения эффекта
        clip = true
    }
        .blur(
            radius = 10.dp,
            edgeTreatment = BlurredEdgeTreatment.Unbounded
        )  // Легкое размытие
        .drawBehind {
            val width = size.width  // Ширина элемента
            val height = size.height // Высота элемента
            val diagonal =
                sqrt(width * width + height * height) // Диагональ элемента для повышения качества анимации
            val startX =
                -diagonal + (diagonal * 2 * shimmerFloatAnimation.value) // Старт градиента заранее
            val startY = -diagonal + (diagonal * 2 * shimmerFloatAnimation.value)

            drawRect(
                brush = Brush.linearGradient(
                    // Определяем цвета градиента с разной прозрачностью
                    colors = listOf(
                        Color.Transparent,
                        Color.LightGray.copy(alpha = 0.10f),
                        Color.LightGray.copy(alpha = 0.7f),
                        Color.LightGray.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    start = Offset(
                        x = startX, // Начало градиента по Х
                        y = startY  // Начало градиента по Y
                    ),
                    end = Offset(
                        x = startX + diagonal * 0.3f,  // Конец градиента по Х
                        y = startY + diagonal * 0.3f  // Конец градиента по Y
                    ),
                )
            )
        }
}