package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun ArrowCheckmarkAnimation() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Создаем анимируемое значение от 0f до 1f
        val animatedProgress = remember { Animatable(0f) }

        // Запускаем анимацию при первой композиции
        LaunchedEffect(key1 = true) {
            // Бесконечное повторение анимации
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1500),
                    repeatMode = RepeatMode.Restart
                )
            )
        }

        Canvas(modifier = Modifier.size(200.dp)) {
            // Определяем размеры и позиции для нашей галочки/стрелки
            val width = size.width
            val height = size.height

            // Стартовая точка анимации
            val startX = width * 0.2f
            val startY = height * 0.5f

            // Промежуточная точка (изгиб галочки)
            val midX = width * 0.4f
            val midY = height * 0.7f

            // Конечная точка галочки
            val endX = width * 0.8f
            val endY = height * 0.3f

            // Рассчитываем текущее положение анимации
            val currentProgress = animatedProgress.value

            // Рисуем первую часть галочки (от начальной до промежуточной точки)
            if (currentProgress <= 0.5f) {
                // Нормализуем прогресс для первой половины анимации
                val normalizedProgress = currentProgress * 2

                // Вычисляем текущие точки для первой линии
                val currentMidX = startX + (midX - startX) * normalizedProgress
                val currentMidY = startY + (midY - startY) * normalizedProgress

                // Рисуем первую линию
                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(currentMidX, currentMidY),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            } else {
                // Рисуем полностью первую линию
                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(midX, midY),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )

                // Нормализуем прогресс для второй половины анимации
                val normalizedProgress = (currentProgress - 0.5f) * 2

                // Вычисляем текущие точки для второй линии
                val currentEndX = midX + (endX - midX) * normalizedProgress
                val currentEndY = midY + (endY - midY) * normalizedProgress

                // Рисуем вторую линию
                drawLine(
                    color = Color.Blue,
                    start = Offset(midX, midY),
                    end = Offset(currentEndX, currentEndY),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}