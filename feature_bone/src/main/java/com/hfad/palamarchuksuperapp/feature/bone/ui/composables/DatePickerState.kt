package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Stable
class DatePickerState(
    initialDate: Calendar = Calendar.getInstance(),
    val minDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -4)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    val maxDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, +1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    val locale: Locale = Locale.getDefault(),
) {

    private var _minDate by mutableStateOf(normalizeCalendar(minDate))
    private var _maxDate by mutableStateOf(normalizeCalendar(maxDate))


    private val _selectedDate = mutableStateOf(
        normalizeCalendar(initialDate).apply {
            if (before(_minDate)) time = _minDate.time
            if (after(_maxDate)) time = _maxDate.time
        }
    )
    val selectedDate = _selectedDate

//    private val calendar = mutableStateOf(_selectedDate.value)

    val currentYear = derivedStateOf { _selectedDate.value.get(Calendar.YEAR) }
    val currentMonth = derivedStateOf { _selectedDate.value.get(Calendar.MONTH) }
    val currentDay = derivedStateOf { _selectedDate.value.get(Calendar.DAY_OF_MONTH) }

    val yearRange = derivedStateOf {
        _minDate.get(Calendar.YEAR).._maxDate.get(Calendar.YEAR)
    }
    val monthRange = derivedStateOf {
        val minMonth = if (currentYear.value == _minDate.get(Calendar.YEAR)) {
            _minDate.get(Calendar.MONTH)
        } else 0

        val maxMonth = if (currentYear.value == _maxDate.get(Calendar.YEAR)) {
            _maxDate.get(Calendar.MONTH)
        } else 11

        minMonth..maxMonth
    }
    val dayRange = derivedStateOf {
        val minDay = if (
            currentYear.value == _minDate.get(Calendar.YEAR) &&
            currentMonth.value == _minDate.get(Calendar.MONTH)
        ) {
            _minDate.get(Calendar.DAY_OF_MONTH)
        } else 1

        val maxDay = if (
            currentYear.value == _maxDate.get(Calendar.YEAR) &&
            currentMonth.value == _maxDate.get(Calendar.MONTH)
        ) {
            _maxDate.get(Calendar.DAY_OF_MONTH)
        } else {
            getDaysInMonth(currentYear.value, currentMonth.value)
        }

        minDay..maxDay
    }

    // --- UI-friendly items ---
    val dayItems = derivedStateOf { dayRange.value.map { it.toString() } }
    val monthItems = derivedStateOf {
        val monthFormat = SimpleDateFormat("MMM", locale)
        monthRange.value.map { monthIndex ->
            Calendar.getInstance().apply { set(Calendar.MONTH, monthIndex) }.let {
                monthFormat.format(it.time)
                    .replaceFirstChar { c -> c.uppercase(locale) }
                    .trimEnd('.')
            }
        }
    }
    val yearItems = derivedStateOf { yearRange.value.map { it.toString() } }

    val selectedDayIndex = derivedStateOf {
        (currentDay.value - dayRange.value.first).coerceAtLeast(0)
    }
    val selectedMonthIndex = derivedStateOf {
        (currentMonth.value - monthRange.value.first).coerceAtLeast(0)
    }
    val selectedYearIndex = derivedStateOf {
        (currentYear.value - yearRange.value.first).coerceAtLeast(0)
    }

    fun updateDay(dayIndex: Int) {
        val newDay = (dayRange.value.first + dayIndex).coerceIn(dayRange.value)
        updateDate { set(Calendar.DAY_OF_MONTH, newDay) }
    }

    fun updateMonth(monthIndex: Int) {
        val rememberedDay = currentDay
        Log.d("DatePickerState", "rememberedDay: ${rememberedDay.value}")
        val newMonth = monthRange.value.first + monthIndex

        updateDate {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, newMonth)

            val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
            Log.d("DatePickerState", "maxDayInMonth: $maxDayInMonth, ${rememberedDay.value}")
            set(Calendar.DAY_OF_MONTH, rememberedDay.value.coerceAtMost(maxDayInMonth))
        }
    }

    fun updateYear(yearIndex: Int) {
        val newYear = yearRange.value.first + yearIndex

        updateDate {
            set(Calendar.YEAR, newYear)
            val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
            if (get(Calendar.DAY_OF_MONTH) > maxDayInMonth) {
                set(Calendar.DAY_OF_MONTH, maxDayInMonth)
            }
        }
    }


    fun updateDate(newCalendar: Calendar) {
        val normalizedCalendar = normalizeCalendar(newCalendar)
        Log.d("DatePickerState", "normalizedCalendar: ${normalizedCalendar.get(Calendar.DAY_OF_MONTH)}")
        when {
            normalizedCalendar.before(_minDate) -> normalizedCalendar.time = _minDate.time
            normalizedCalendar.after(_maxDate) -> normalizedCalendar.time = _maxDate.time
        }
        Log.d("NormalizedCalendar", "${normalizedCalendar.get(Calendar.DAY_OF_MONTH)}")
        _selectedDate.value = normalizedCalendar
//        calendar.value = normalizedCalendar
    }

    fun updateMinDate(newMinDate: Calendar) {
        val normalized = normalizeCalendar(newMinDate)
        _minDate = normalized

        // Проверяем, что выбранная дата все еще в допустимом диапазоне
        if (_selectedDate.value.before(normalized)) {
            _selectedDate.value = normalized.clone() as Calendar
        }

        // Проверяем, что maxDate все еще больше minDate
        if (_maxDate.before(normalized)) {
            _maxDate = normalized.clone() as Calendar
        }
    }

    fun updateMaxDate(newMaxDate: Calendar) {
        val normalized = normalizeCalendar(newMaxDate)
        _maxDate = normalized

        // Проверяем, что выбранная дата все еще в допустимом диапазоне
        if (_selectedDate.value.after(normalized)) {
            _selectedDate.value = normalized.clone() as Calendar
        }

        // Проверяем, что minDate все еще меньше maxDate
        if (_minDate.after(normalized)) {
            _minDate = normalized.clone() as Calendar
        }
    }

    private inline fun updateDate(action: Calendar.() -> Unit) {
        val newCalendar = _selectedDate.value.clone() as Calendar
        newCalendar.action()
        Log.d("NewCalendar 123: ", "${newCalendar.get(Calendar.DAY_OF_MONTH)}")
        updateDate(newCalendar)
    }

    private fun normalizeCalendar(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    companion object {
        fun Saver(
            minDate: Calendar,
            maxDate: Calendar,
            locale: Locale,
        ): Saver<DatePickerState, Long> = Saver(
            save = { it.selectedDate.value.timeInMillis },
            restore = {
                DatePickerState(
                    initialDate = Calendar.getInstance().apply { timeInMillis = it },
                    minDate = minDate,
                    maxDate = maxDate,
                    locale = locale
                )
            }
        )
    }
}


@Composable
fun rememberDatePickerState(
    initialDate: Calendar = Calendar.getInstance(),
    minDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -4)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    maxDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, +1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    locale: Locale = Locale.getDefault(),
): DatePickerState {
    return remember {
        DatePickerState(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            locale = locale
        )
    }
}

@Composable
fun rememberDatePickerStateSavable(
    initialDate: Calendar = Calendar.getInstance(),
    minDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -4)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    maxDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, +1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    locale: Locale = Locale.getDefault(),
): DatePickerState {
    return rememberSaveable(
        saver = DatePickerState.Saver(
            minDate = minDate,
            maxDate = maxDate,
            locale = locale
        )
    ) {
        DatePickerState(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            locale = locale
        )
    }
}