package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun IOSDatePicker(
    modifier: Modifier = Modifier,
    selectedDate: Date,
    onDateChanged: (Date) -> Unit,
    minDate: Calendar = Calendar.getInstance().apply { add(Calendar.YEAR, -4) },
    maxDate: Calendar = Calendar.getInstance().apply { add(Calendar.YEAR, +1) },
    locale: Locale = Locale.getDefault(),
    catchSwing: Boolean = false,
) {
    val calendar = remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                time = selectedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        )
    }

    val currentYear = calendar.value.get(Calendar.YEAR)
    val currentMonth = calendar.value.get(Calendar.MONTH)

    val daysInMonth = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }.getActualMaximum(Calendar.DAY_OF_MONTH)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // День
        val minDay = if (
            currentYear == minDate.get(Calendar.YEAR) &&
            currentMonth == minDate.get(Calendar.MONTH)
        ) {
            minDate.get(Calendar.DAY_OF_MONTH)
        } else 1
        Log.d("Min date:", "${minDay}")

        val maxDay = if (
            currentYear == maxDate.get(Calendar.YEAR) &&
            currentMonth == maxDate.get(Calendar.MONTH)
        ) {
            maxDate.get(Calendar.DAY_OF_MONTH)
        } else {
            daysInMonth
        }
        Log.d("Max day:", "${maxDay}")

        val currentDayMonth = calendar.value.get(Calendar.DAY_OF_MONTH)
        val effectiveDay = currentDayMonth.coerceIn(minDay, maxDay)

        val selectedIndex = (effectiveDay - minDay)
        val days = (minDay..maxDay).map { it.toString() }

        IOSWheelPicker(
            items = days,
            selectedIndex = selectedIndex,
            onSelectionChanged = { index ->
                Log.d("Index:", "${minDay}")
                val pickedDay = (minDay + index).coerceIn(minDay, maxDay)
                Log.d("Picked day:", pickedDay.toString())
                val newCalendar = calendar.value.apply {
                    set(Calendar.DAY_OF_MONTH, pickedDay)
                }

                onDateChanged(newCalendar.time)
            },
            modifier = Modifier.weight(0.3f),
            catchSwing = catchSwing
        )


        // Месяц
        val monthFormat = SimpleDateFormat("MMM", locale)

        val minMonth = if (currentYear == minDate.get(Calendar.YEAR)) {
            minDate.get(Calendar.MONTH)
        } else 0

        val maxMonth = if (currentYear == maxDate.get(Calendar.YEAR)) {
            maxDate.get(Calendar.MONTH)
        } else 11

        val monthsToShow = (minMonth..maxMonth).map { monthIndex ->
            val tempCalendar = Calendar.getInstance()
            tempCalendar.set(Calendar.MONTH, monthIndex)
            monthFormat.format(tempCalendar.time)
                .replaceFirstChar { it.uppercase(locale) }
                .trimEnd('.')
        }

        val selectedMonthIndex = (calendar.value.get(Calendar.MONTH) - minMonth).coerceAtLeast(0)

        IOSWheelPicker(
            items = monthsToShow,
            selectedIndex = selectedMonthIndex,
            onSelectionChanged = { index ->
                val rememberedDay = calendar.value.get(Calendar.DAY_OF_MONTH)
                val newCalendar = calendar.value.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.MONTH, index + minMonth)
                    set(Calendar.MONTH, index + minMonth)

                    val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
                    set(Calendar.DAY_OF_MONTH, rememberedDay.coerceAtMost(maxDayInMonth))
                }

                if (newCalendar.before(minDate)) {
                    newCalendar.time = minDate.time
                } else if (newCalendar.after(maxDate)) {
                    newCalendar.time = maxDate.time
                }

                onDateChanged(newCalendar.time)
            },
            modifier = Modifier.weight(0.4f),
            catchSwing = catchSwing,
        )

        // Год
        val minYear = minDate.get(Calendar.YEAR)
        val maxYear = maxDate.get(Calendar.YEAR)

        val years = (minYear..maxYear).toList()
        val selectedYearIndex = (calendar.value.get(Calendar.YEAR) - minYear).coerceAtLeast(0)

        val yearsToShow = years.map { it.toString() }

        IOSWheelPicker(
            items = yearsToShow,
            selectedIndex = selectedYearIndex,
            onSelectionChanged = { index ->
                val newCalendar = calendar.value.apply {
                    set(Calendar.YEAR, years[index])
                    val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (get(Calendar.DAY_OF_MONTH) > maxDayInMonth) {
                        set(Calendar.DAY_OF_MONTH, maxDayInMonth)
                    }
                }

                if (newCalendar.before(minDate)) {
                    newCalendar.time = minDate.time
                } else if (newCalendar.after(maxDate)) {
                    newCalendar.time = maxDate.time
                }

                onDateChanged(newCalendar.time)

            },
            modifier = Modifier.weight(0.5f),
            catchSwing = catchSwing,
        )
    }
    Log.d("Remembered selected date: ", "${selectedDate}")

}

@Composable
private fun IOSWheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 40.dp,
    visibleItemCount: Int = 3,
    catchSwing: Boolean = false,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedIndex, items.size) {
        val targetIndex = selectedIndex + 1
        if (listState.firstVisibleItemIndex != targetIndex - 1) {
            listState.scrollToItem(
                index = maxOf(0, targetIndex - 1),
                scrollOffset = 0
            )
        }
    }

    val centerIndex by remember {
        derivedStateOf {
            listState.layoutInfo.run {
                val viewportCenter = viewportStartOffset + viewportSize.height / 2
                visibleItemsInfo
                    .minWithOrNull(compareBy { abs(viewportCenter - (it.offset + it.size / 2)) })
                    ?.let { it.index - 1 }
            }
        }
    }

    LaunchedEffect(listState, selectedIndex) {
        snapshotFlow { listState.isScrollInProgress }
            .filter {
                !it
            }
            .collect {
                if (centerIndex != selectedIndex) {
                    onSelectionChanged(centerIndex ?: 0)
                }
                listState.scrollToItem(centerIndex ?: 0, scrollOffset = 0)
            }
    }

    val scrollModifier = if (catchSwing) {
        val vScroll = rememberScrollableState { delta ->
            scope.launch { listState.scrollBy(-delta) }
            delta
        }

        modifier.scrollable(
            state = vScroll,
            orientation = Orientation.Vertical,
            flingBehavior = ScrollableDefaults.flingBehavior(),
            reverseDirection = false
        )
    } else {
        modifier
    }

    LazyColumn(
        state = listState,
        modifier = scrollModifier
            .height(itemHeight * visibleItemCount)
            .fadingEdge(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Spacer to show first item in center
        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }

        items(items.size) { index ->

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxSize()
            ) {
                AppText(
                    value = items[index],
                    appTextConfig = appTextConfig(
                        fontSize = if (centerIndex == index) 18.sp else 16.sp,
                        fontWeight = if (centerIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        // Spacer to show last item in center
        item {
            Spacer(modifier = Modifier.height(itemHeight))
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
                    modifier = Modifier.fillMaxWidth(0.5f),
//                    minDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 5) }
                )
            }
        }
    }
}