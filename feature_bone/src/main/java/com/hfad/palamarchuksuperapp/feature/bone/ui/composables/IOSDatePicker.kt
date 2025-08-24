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
import java.util.Locale
import kotlin.math.abs

@Composable
fun IOSDatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    onDateChanged: (Calendar) -> Unit,
    catchSwing: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day
        IOSWheelPicker(
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
        IOSWheelPicker(
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
        IOSWheelPicker(
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