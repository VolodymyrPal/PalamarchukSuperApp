package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.FeatureTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun IOSDatePicker(
    modifier: Modifier = Modifier,
    selectedDate: Date = Date(),
    onDateChanged: (Date) -> Unit,
    minYear: Int = 2020,
    maxYear: Int = 2026,
    locale: Locale = Locale.getDefault(),
    catchSwing: Boolean = false,
) {
    //Actual date with Calendar
    val calendar = Calendar.getInstance().apply { time = selectedDate }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day wheel
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDayMonth = calendar.get(Calendar.DAY_OF_MONTH)

        IOSWheelPicker(
            items = (1..daysInMonth).map { it.toString() },
            selectedIndex = currentDayMonth - 1,
            onSelectionChanged = { index ->
                val newCalendar = Calendar.getInstance().apply {
                    time = selectedDate
                    set(Calendar.DAY_OF_MONTH, index + 1)
                }
                onDateChanged(newCalendar.time)
            },
            modifier = Modifier.weight(0.3f),
            catchSwing = catchSwing
        )

        // Month wheel
        val monthFormat = SimpleDateFormat("MMM", locale)
        val monthsToShow = (0..11).map { monthIndex ->
            val tempCalendar = Calendar.getInstance()
            tempCalendar.set(Calendar.MONTH, monthIndex)
            monthFormat.format(tempCalendar.time).replaceFirstChar {
                it.uppercase(locale)
            }
        }

        IOSWheelPicker(
            items = monthsToShow,
            selectedIndex = calendar.get(Calendar.MONTH),
            onSelectionChanged = { index ->
                val currentCalendar = Calendar.getInstance().apply { time = selectedDate }
                val newCalendar = Calendar.getInstance().apply {
                    time = selectedDate
                    set(Calendar.MONTH, index)
                    // Убедимся, что день не превышает максимальное значение для нового месяца
                    val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (get(Calendar.DAY_OF_MONTH) > maxDayInMonth) {
                        set(Calendar.DAY_OF_MONTH, maxDayInMonth)
                    }
                }
                onDateChanged(newCalendar.time)
            },
            modifier = Modifier.weight(0.4f),
            catchSwing = catchSwing
        )

        // Year wheel
        val years = (minYear..maxYear).toList()
        val currentYear = calendar.get(Calendar.YEAR)
        val yearIndex = years.indexOf(currentYear)

        IOSWheelPicker(
            items = years.map { it.toString() },
            selectedIndex = if (yearIndex >= 0) yearIndex else 0,
            onSelectionChanged = { index ->
                val newYear = years[index]
                val newCalendar = Calendar.getInstance().apply {
                    time = selectedDate
                    set(Calendar.YEAR, newYear)
                    // Убедимся, что день не превышает максимальное значение для нового года
                    val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (get(Calendar.DAY_OF_MONTH) > maxDayInMonth) {
                        set(Calendar.DAY_OF_MONTH, maxDayInMonth)
                    }
                }
                onDateChanged(newCalendar.time)
            },
            modifier = Modifier.weight(0.5f),
            catchSwing = catchSwing
        )
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

@Composable
private fun IOSWheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 26.dp,
    visibleItemCount: Int = 3,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val itemHeightPx = with(density) { itemHeight.toPx() }


    // Корректная инициализация позиции с учетом добавочных элементов
    LaunchedEffect(selectedIndex, items.size) {
        val targetIndex = selectedIndex + 1 // +1 из-за верхнего Spacer
        if (listState.firstVisibleItemIndex != targetIndex - 1) {
            listState.scrollToItem(
                index = maxOf(0, targetIndex - 1),
                scrollOffset = 0
            )
        }
    }

    // Отслеживание изменений скролла
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2

            // Находим элемент ближайший к центру (исключая Spacer элементы)
            val centerItem = layoutInfo.visibleItemsInfo
                .filter { it.index > 0 && it.index <= items.size } // Исключаем Spacer элементы
                .minByOrNull { item ->
                    abs(viewportCenter - (item.offset + item.size / 2))
                }

            centerItem?.let { item ->
                val actualIndex = item.index - 1 // -1 из-за верхнего Spacer
                val clampedIndex = actualIndex.coerceIn(0, items.size - 1)

                if (clampedIndex != selectedIndex) {
                    onSelectionChanged(clampedIndex)
                }

                // Плавно прокручиваем к точной позиции
                scope.launch {
                    listState.animateScrollToItem(
                        index = maxOf(
                            0,
                            clampedIndex
                        ), // Прокручиваем к самому элементу, не учитывая Spacer
                        scrollOffset = 0
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemCount)
    ) {

        // Фоновая подсветка для выбранного элемента
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.Center)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    RoundedCornerShape(16.dp)
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
//                .drawWithContent {
//                    drawContent()
//
//                    // Градиентная маска сверху и снизу
//                    val fadeHeight = itemHeightPx * 0.6f
//
//                    drawRect(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                Color.White,
//                                Color.Transparent
//                            ),
//                            startY = 0f,
//                            endY = fadeHeight
//                        ),
//                        size = size.copy(height = fadeHeight)
//                    )
//
//                    drawRect(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                Color.Transparent,
//                                Color.White
//                            ),
//                            startY = size.height - fadeHeight,
//                            endY = size.height
//                        ),
//                        topLeft = Offset(0f, size.height - fadeHeight),
//                        size = size.copy(height = fadeHeight)
//                    )
//                }
        ) {
            // Верхний Spacer для центрирования
            item {
                Spacer(modifier = Modifier.height(itemHeight))
            }

            items(items.size) { index ->
                val item = items[index]
                val isSelected = index == selectedIndex

                // Вычисляем позицию элемента относительно центра
                val layoutInfo = listState.layoutInfo
                val viewportCenter =
                    layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2

                val itemInfo = layoutInfo.visibleItemsInfo
                    .find { it.index == index + 1 } // +1 из-за верхнего Spacer

                val distance = itemInfo?.let { info ->
                    abs(viewportCenter - (info.offset + info.size / 2)).toFloat()
                } ?: Float.MAX_VALUE

                // Более точное вычисление прозрачности
                val alpha = when {
                    distance == Float.MAX_VALUE -> 0.0f
                    distance < itemHeightPx * 0.3f -> 1.0f // Центральный элемент
                    distance < itemHeightPx * 1.2f -> 0.6f // Соседние элементы
                    else -> 0.0f
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .alpha(alpha),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = if (isSelected && distance < itemHeightPx * 0.3f) 18.sp else 16.sp,
                        fontWeight = if (isSelected && distance < itemHeightPx * 0.3f) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected && distance < itemHeightPx * 0.3f) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Нижний Spacer для центрирования
            item {
                Spacer(modifier = Modifier.height(itemHeight))
            }
        }
    }
}

// Пример использования
@Composable
@Preview
fun DatePickerExample() {
    var selectedDate by remember { mutableStateOf(Date()) }
    FeatureTheme(
        darkTheme = true
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Выбранная дата: ${selectedDate}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                IOSDatePicker(
                    selectedDate = selectedDate,
                    onDateChanged = { selectedDate = it },
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }
        }
    }
}