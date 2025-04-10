package com.hfad.palamarchuksuperapp.core.ui.composables.basic

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import com.hfad.palamarchuksuperapp.core.R
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Suppress("LongParameterList")
@Composable
fun <T> AppOutlinedTextDialogField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @StringRes label: Int = R.string.place_holder_text,
    notSetLabel: String? = null,
    items: List<T>,
    selectedItem: T? = null,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = selectedItemToString(item),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
    appOutlinedTextField: @Composable (expanded: Boolean) -> Unit = { expanded ->
        AppOutlinedTextField(
            labelRes = label,
            value = if (selectedItem == null) "" else selectedItemToString(selectedItem),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { },
            outlinedTextConfig = appEditOutlinedTextConfig(
                enabled = enabled,
                trailingIcon = {
                    val icon =
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                    Icon(icon, "")
                },
                readOnly = true
            ),
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        appOutlinedTextField(expanded)

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { expanded = !expanded },
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        DropdownDialog(
            onDismissRequest = { expanded = false },
            selectedItem = selectedItem,
            notSetLabel = notSetLabel,
            items = items,
            onItemSelected = onItemSelected,
            drawItem = drawItem,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun <T> AppOutlinedTextPopUpField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @StringRes label: Int = R.string.place_holder_text,
    notSetLabel: String? = null,
    items: List<T>,
    selectedItem: T? = null,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = selectedItemToString(item),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
    appOutlinedTextField: @Composable ExposedDropdownMenuBoxScope.(
        expanded: Boolean,
        label: Int,
        selectedItem: T?,
        selectedItemToString: (T) -> String,
        enabled: Boolean,
        onDismissRequest: () -> Unit,
    ) -> Unit = {
            expanded, label, selectedItem, selectedItemToString, enabled, onDismissRequest,
        ->
        AppOutlinedTextField(
            labelRes = label,
            value = if (selectedItem == null) "" else selectedItemToString(selectedItem),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable, true),
            onValueChange = { },
            outlinedTextConfig = appEditOutlinedTextConfig(
                enabled = enabled,
                trailingIcon = {
                    val icon =
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                    Icon(icon, "")
                },
                readOnly = true,
                interactionSource = remember { MutableInteractionSource() }
            ),
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {

        appOutlinedTextField(
            expanded,
            label,
            selectedItem,
            selectedItemToString,
            enabled,
            { expanded = !expanded })

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            if (notSetLabel != null) {
                AppText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    value = notSetLabel,
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                )
            }
            if (items.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
            items.fastForEachIndexed { index, item ->
                val selectedItem = item == selectedItem
                drawItem(
                    item,
                    selectedItem,
                    true
                ) {
                    onItemSelected(index, item)
                    expanded = false
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
    LaunchedEffect(expanded) {
        if (!expanded) {
            delay(60)
            focusManager.clearFocus(force = true)
        }
    }
}


@Suppress("LongParameterList")
@Composable
fun <T> DropdownDialog(
    onDismissRequest: () -> Unit,
    selectedItem: T?,
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
            modifier = Modifier.padding(vertical = 24.dp),
        ) {
            val listState = rememberLazyListState()
            val selectedItemIndex = remember(selectedItem) { items.indexOf(selectedItem) }

            if (selectedItemIndex > -1) {
                LaunchedEffect(selectedItem) {
                    selectedItemIndex
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = listState
            ) {
                if (notSetLabel != null) {
                    item() {
                        AppText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            value = notSetLabel,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                if (items.isEmpty()) {
                    item() {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                itemsIndexed(items) { index, item ->
                    val selectedItem = index == selectedItemIndex
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
            appTextConfig = appTextConfig(
                textStyle = if (selected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
                fontWeight = if (selected) FontWeight.Bold else null,
            ),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LargeDropdownMenuItemPreview() {

    var selectedIndex by remember { mutableStateOf("") }
    var selected2Index by remember { mutableStateOf("") }


    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTheme (useDarkTheme = true) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                Column {
                    AppOutlinedTextDialogField(
                        modifier = Modifier.padding(4.dp),
                        selectedItem = selectedIndex,
                        items = testList,
                        onItemSelected = { _, item -> selectedIndex = item },
                        // notSetLabel = "Wow, what is it"
                    )
                    AppOutlinedTextPopUpField(
                        modifier = Modifier.padding(4.dp),
                        selectedItem = selected2Index,
                        items = testList,
                        onItemSelected = { _, item ->
                            selected2Index = item
                        },
                        notSetLabel = "Wow, what is it"
                    )
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
                        selectedItem = selectedIndex,
                        items = testList,
                        onItemSelected = { _, item -> selectedIndex = item },
                        // notSetLabel = "Wow, what is it"
                    )
                    AppOutlinedTextPopUpField(
                        modifier = Modifier.padding(4.dp),
                        items = testList,
                        selectedItem = selected2Index,
                        onItemSelected = { _, item ->
                            selected2Index = item
                        },
                        notSetLabel = "Wow, what is it",
                    )
                }
            }
        }
    }
}

val testList = listOf(
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
    "AGgldfsg",
    "adsf sald",
    "FDSKSDFLKD",
)