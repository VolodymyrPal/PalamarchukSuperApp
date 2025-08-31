package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.flow.filter

@Composable
fun AppWheelDatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    onDateChanged: (Calendar) -> Unit,
    catchSwing: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Day
        AppWheelPicker(
            items = state.dayItems.value,
            selectedIndex = state.selectedDayIndex.value,
            onSelectionChanged = { index ->
                state.updateDay(index)
                onDateChanged(state.selectedDate.value)
            },
            modifier = Modifier.weight(0.3f),
            catchSwing = catchSwing,

            )

        // Month
        AppWheelPicker(
            items = state.monthItems.value,
            selectedIndex = state.selectedMonthIndex.value,
            onSelectionChanged = { index ->
                state.updateMonth(index)
                onDateChanged(state.selectedDate.value)
            },
            modifier = Modifier.weight(0.4f),
            catchSwing = catchSwing,
        )

        // Year
        AppWheelPicker(
            items = state.yearItems.value,
            selectedIndex = state.selectedYearIndex.value,
            onSelectionChanged = { index ->
                state.updateYear(index)
                onDateChanged(state.selectedDate.value)
            },
            modifier = Modifier.weight(0.5f),
            catchSwing = catchSwing,
        )
    }
}

@Composable
private fun AppWheelPicker(
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

    LaunchedEffect(selectedIndex) {
        val target = (selectedIndex).coerceIn(0, items.lastIndex + 1)
        listState.animateScrollToItem(index = maxOf(0, target), scrollOffset = 0)
    }

    LaunchedEffect(items.size) {
        val target = (selectedIndex).coerceIn(0, items.lastIndex + 1)
        listState.scrollToItem(index = maxOf(0, target), scrollOffset = 0)
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

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collect {
                if (centerIndex != null) {
                    onSelectionChanged(centerIndex!!)
                }
            }
    }

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                return if (catchSwing) {
                    val vertical = available.y
                    Offset(x = 0f, y = vertical / 1.05f)
                } else {
                    return super.onPostScroll(consumed, available, source)
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .height(itemHeight * visibleItemCount)
            .nestedScroll(scrollConnection)
            .fadingEdge(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }

        items(items.size) { index ->

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxSize(),
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

        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }
    }
}


@Composable
private fun AppDayWheelPicker(
    modifier: Modifier = Modifier,
    daysRange: IntRange = 1..31,
    startRestriction: Int = 0,
    endRestriction: Int = 32,
    selectedDay: Int,
    onSelectionChanged: (Int) -> Unit,
    itemHeight: Dp = 40.dp,
    visibleItemCount: Int = 3,
    catchSwing: Boolean = false,
) {
    val listState = rememberLazyListState()

    val listToDisplay by remember(daysRange, startRestriction, endRestriction) {
        derivedStateOf {
            val range = daysRange.filter { it in startRestriction..endRestriction }
            range
        }
    }

    LaunchedEffect(selectedDay) {

        if (listToDisplay.isEmpty()) return@LaunchedEffect

        val targetIndex = listToDisplay.indexOf(selectedDay).takeIf { it >= 0 }
            ?: listToDisplay.indexOfFirst { it >= selectedDay }.takeIf { it >= 0 }
            ?: listToDisplay.lastIndex

        listState.animateScrollToItem(
            index = targetIndex, // +1 из-за верхнего Spacer
            scrollOffset = 0,
        )
    }
    LaunchedEffect(listToDisplay) {
        if (listToDisplay.isEmpty()) return@LaunchedEffect

        val targetIndex = listToDisplay.indexOf(selectedDay).takeIf { it >= 0 }
            ?: listToDisplay.indexOfFirst { it >= selectedDay }.takeIf { it >= 0 }
            ?: listToDisplay.lastIndex

        listState.scrollToItem(
            index = targetIndex, // +1 из-за верхнего Spacer
            scrollOffset = 0,
        )
    }

    LaunchedEffect(
        listState,
        listToDisplay,
    )
    {
        val centerIndex = derivedStateOf {
            listState.layoutInfo.run {
                if (visibleItemsInfo.isEmpty()) return@run null

                val viewportCenter = viewportStartOffset + viewportSize.height / 2

                val itemsInfo = visibleItemsInfo
                    .minByOrNull { abs(viewportCenter - (it.offset + it.size / 2)) }
                    ?.let { it.index - 1 } // -1 из-за верхнего Spacer
                    ?.coerceIn(
                        0,
                        listToDisplay.lastIndex,
                    )
                itemsInfo
            }
        }

        val currentCenterDay = centerIndex.value?.let { index ->
            if (index in listToDisplay.indices) listToDisplay[index] else 0
        }


        snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collect {
                if (centerIndex.value != null) {
                    onSelectionChanged(centerIndex.value!!)
                }
            }
    }

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                return if (catchSwing) {
                    val vertical = available.y
                    Offset(x = 0f, y = vertical / 1.05f)
                } else {
                    return super.onPostScroll(consumed, available, source)
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .height(itemHeight * visibleItemCount)
            .nestedScroll(scrollConnection)
            .fadingEdge(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }

        items(
            listToDisplay.size,
            key = { it },
        ) { index ->

            val day = listToDisplay[index]
            val isSelected = day == selectedDay

            if (day in listToDisplay) {

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxSize()
                        .clickable {
                            onSelectionChanged(day)
                        },
                ) {
                    AppText(
                        value = listToDisplay[index].toString(),
                        appTextConfig = appTextConfig(
                            fontSize = if (isSelected) 18.sp else 16.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }
    }
}

@Composable
@Preview
fun DatePickerExample() {
    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    FeatureTheme(
        darkTheme = true,
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Выбранная дата: ${selectedDate.value.time}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
                val datePickerState = rememberDatePickerState(
                    initialDate = selectedDate.value,
                )

                AppWheelDatePicker(
                    state = datePickerState,
                    onDateChanged = { selectedDate.value = it },
                    modifier = Modifier.fillMaxWidth(0.5f),
                )
            }
        }
    }
}

@Composable
@Preview
fun DatePickerExampleOne() {

    val datePickerState = rememberDatePickerState(
        initialDate = Calendar.getInstance(),
        minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -2) },
        maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, +1) },
    )

    var selectedDateText by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    LaunchedEffect(datePickerState.selectedDate) {
        selectedDateText = dateFormat.format(datePickerState.selectedDate.value.time)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Выбранная дата: $selectedDateText",
            style = MaterialTheme.typography.headlineSmall,
        )

        AppWheelDatePicker(
            state = datePickerState,
            onDateChanged = { newDate ->

            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = {
                    datePickerState.updateDate(Calendar.getInstance())
                },
            ) {
                Text("Сегодня")
            }

            Button(
                onClick = {
                    val weekAgo = Calendar.getInstance().apply {
                        add(Calendar.WEEK_OF_YEAR, -1)
                    }
                    datePickerState.updateDate(weekAgo)
                },
            ) {
                Text("Неделю назад")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Информация о состоянии:",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text("Год: ${datePickerState.currentYear.value}")
                Text("Месяц: ${datePickerState.currentMonth.value + 1}")
                Text("День: ${datePickerState.currentDay.value}")
                Text("Доступные дни: ${datePickerState.dayRange.value}")
                Text("Доступные месяцы: ${datePickerState.monthRange.value.first + 1}-${datePickerState.monthRange.value.last + 1}")
                Text("Доступные годы: ${datePickerState.yearRange.value}")
            }
        }
    }
}

@Preview
@Composable
fun MultiDatePickerExample() {
    val startDateState = rememberDatePickerState(
        initialDate = Calendar.getInstance(),
        minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -1) },
        maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, +1) },
    )

    val endDateState = rememberDatePickerState(
        initialDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) },
        minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -1) },
        maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, +1) },
    )

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Выбор периода",
            style = MaterialTheme.typography.headlineMedium,
        )

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Дата начала: ${dateFormat.format(startDateState.selectedDate.value.time)}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppWheelDatePicker(
                    state = startDateState,
                    onDateChanged = { newStartDate ->
                        if (newStartDate.after(endDateState.selectedDate.value)) {
                            val newEndDate = Calendar.getInstance().apply {
                                time = newStartDate.time
                                add(Calendar.DAY_OF_YEAR, 1)
                            }
                            endDateState.updateDate(newEndDate)
                        }
                    },
                )
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Дата окончания: ${dateFormat.format(endDateState.selectedDate.value.time)}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppWheelDatePicker(
                    state = endDateState,
                    onDateChanged = { newEndDate ->
                        if (newEndDate.before(startDateState.selectedDate.value)) {
                            val newStartDate = Calendar.getInstance().apply {
                                time = newEndDate.time
                                add(Calendar.DAY_OF_YEAR, -1)
                            }
                            startDateState.updateDate(newStartDate)
                        }
                    },
                )
            }
        }

        val daysDifference by remember {
            derivedStateOf {
                val diffInMillis =
                    endDateState.selectedDate.value.timeInMillis - startDateState.selectedDate.value.timeInMillis
                (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
            }
        }

        Text(
            text = "Период: $daysDifference дн.",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
