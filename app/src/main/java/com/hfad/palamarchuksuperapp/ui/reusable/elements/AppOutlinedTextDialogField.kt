package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.R

@Suppress("LongParameterList")
@Composable
fun <T> AppOutlinedTextDialogField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @StringRes label: Int = R.string.app_name,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        AppOutlinedTextField(
            labelRes = label,
            value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { },
            outlinedTextConfig = rememberOutlinedTextConfig(
                enabled = enabled,
                trailingIcon = {
                    val icon =
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                    Icon(icon, "")
                },
                readOnly = true
            ),
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { expanded = true },
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        DropdownDialog(
            onDismissRequest = { expanded = false },
            selectedIndex = selectedIndex,
            notSetLabel = notSetLabel,
            items = items,
            onItemSelected = onItemSelected,
            drawItem = drawItem
        )
    }
}

@Suppress("LongParameterList")
@Composable
fun <T> DropdownDialog(
    onDismissRequest: () -> Unit,
    selectedIndex: Int,
    notSetLabel: String? = null,
    items: List<T>,
    onItemSelected: (index: Int, item: T) -> Unit,
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
        ) {
            val listState = rememberLazyListState()
            if (selectedIndex > -1) {
                LaunchedEffect(selectedIndex) {
                    listState.scrollToItem(index = selectedIndex)
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                if (notSetLabel != null) {
                    item() {
                        AppText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            value = notSetLabel,
                            appTextConfig = rememberTextConfig(
                                textStyle = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                itemsIndexed(items) { index, item ->
                    val selectedItem = index == selectedIndex
                    drawItem(
                        item,
                        selectedItem,
                        true
                    ) {
                        onItemSelected(index, item)
                        onDismissRequest()
                    }
                    if (index < items.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 12.dp)) {
        AppText(
            value = text,
            appTextConfig = rememberTextConfig(
                textStyle = MaterialTheme.typography.titleSmall
            ),
        )
    }
}


@Composable
@Preview
fun LargeDropdownMenuItemPreview() {

    var selectedIndex by remember { mutableStateOf(-1) }

    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTheme(useDarkTheme = true) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                Column {
                    AppOutlinedTextDialogField(
                        modifier = Modifier.padding(4.dp),
                        selectedIndex = selectedIndex,
                        items = listOf("123", "321", "232", "111"),
                        onItemSelected = { index, _ -> selectedIndex = index },
                        // notSetLabel = "Wow, what is it"
                    )
                    repeat(3) {
                        LargeDropdownMenuItem(
                            text = "$it",
                            selected = if (it % 2 == 0) false else true,
                            enabled = if (it % 2 == 0) false else true
                        ) { }
                    }
                }
            }
        }
        AppTheme(useDarkTheme = false) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                Column {
                    AppOutlinedTextDialogField(
                        modifier = Modifier.padding(4.dp),
                        items = listOf(
                            "123",
                            "321",
                            "232",
                            "11451",
                            "123",
                            "321",
                            "232",
                            "11451",
                            "123",
                            "321",
                            "232",
                            "11451",
                            "123",
                            "321",
                            "232",
                            "11451",
                            "123",
                            "321",
                            "232",
                            "11451",
                            "123",
                            "321",
                            "232",
                            "11451",
                            "123",
                            "321",
                            "232",
                            "11451"
                        ),
                        selectedIndex = selectedIndex,
                        onItemSelected = { index, _ ->
                            selectedIndex = index
                        },
                        notSetLabel = "Wow, what is it"
                    )
                    repeat(3) {
                        LargeDropdownMenuItem(
                            text = "$it",
                            selected = if (it % 2 == 0) false else true,
                            enabled = if (it % 2 == 0) false else true
                        ) { }
                    }
                }
            }
        }
    }
}