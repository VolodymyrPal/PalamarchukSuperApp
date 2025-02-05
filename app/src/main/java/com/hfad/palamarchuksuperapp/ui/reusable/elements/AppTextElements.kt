package com.hfad.palamarchuksuperapp.ui.reusable.elements

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.R

@Suppress("LongParameterList", "FunctionNaming")

/**
 * Конфигурация текста, используемая для настройки отображения текстовых компонентов.
 *
 * @property textStyle Стиль текста.
 * @property overflow Поведение при переполнении текста.
 * @property softWrap Включение переноса текста.
 * @property maxLines Максимальное количество строк.
 * @property minLines Минимальное количество строк.
 * @property onTextLayout Обратный вызов для обработки результата компоновки текста.
 */
@Immutable
data class AppTextConfig(
    val textStyle: TextStyle = TextStyle.Default,
    val overflow: TextOverflow = TextOverflow.Clip,
    val softWrap: Boolean = true,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = 1,
    val onTextLayout: ((TextLayoutResult) -> Unit)? = null,
)

/**
 * Запоминает конфигурацию текста для использования в текстовых компонентах.
 */
@Composable
@Suppress("LongParameterList")
fun rememberAppTextConfig(
    textStyle: TextStyle = TextStyle.Default,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
): AppTextConfig {
    return remember {
        AppTextConfig(
            textStyle,
            overflow,
            softWrap,
            maxLines,
            minLines,
            onTextLayout
        )
    }
}

/**
 * Базовый компонент для отображения текста с поддержкой конфигурации.
 *
 * @param stringRes Идентификатор строкового ресурса.
 * @param modifier Модификатор для настройки внешнего вида.
 * @param color Цвет текста.
 * @param style Стиль текста.
 * @param appTextConfig Конфигурация текста.
 */
@Composable
@Suppress("LongParameterList", "FunctionNaming")
fun AppText(
    @StringRes stringRes: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = LocalTextStyle.current,
    appTextConfig: AppTextConfig = rememberAppTextConfig(),
) {
    val mergedStyle = style.merge(appTextConfig.textStyle).copy(color = color)

    Text(
        text = stringResource(stringRes),
        modifier = modifier,
        style = mergedStyle,
        overflow = appTextConfig.overflow,
        softWrap = appTextConfig.softWrap,
        maxLines = appTextConfig.maxLines,
        minLines = appTextConfig.minLines,
        onTextLayout = appTextConfig.onTextLayout
    )
}

@Composable
@Suppress("LongParameterList", "FunctionNaming")
@Deprecated(
    message = "Use AppText with @StringRes for proper localization. Hardcoded string is discouraged",
    replaceWith = ReplaceWith("AppText(stringRes = R.string.your_string_resource, appTextConfig = appTextConfig)")
)
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = LocalTextStyle.current,
    appTextConfig: AppTextConfig = rememberAppTextConfig(),
) {
    val mergedStyle = style.merge(appTextConfig.textStyle).copy(color = color)

    Text(
        text = text,
        modifier = modifier,
        style = mergedStyle,
        overflow = appTextConfig.overflow,
        softWrap = appTextConfig.softWrap,
        maxLines = appTextConfig.maxLines,
        minLines = appTextConfig.minLines,
        onTextLayout = appTextConfig.onTextLayout
    )
}

/**
 * Компонент для редактирования текста с поддержкой метки и подсказки.
 *
 * @param modifier Модификатор для настройки внешнего вида.
 * @param text Текущее значение текста.
 * @param onValueChanged Обратный вызов при изменении текста.
 * @param labelRes Идентификатор строкового ресурса для метки.
 * @param label Компонент для отображения метки.
 * @param placeholderRes Идентификатор строкового ресурса для подсказки.
 * @param placeholder Компонент для отображения подсказки.
 */
@Composable
@Suppress("LongParameterList")
fun AppEditOutlinedText(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit,
    @StringRes labelRes: Int? = R.string.model_hint,
    @StringRes placeholderRes: Int? = R.string.edit,
    label: @Composable (() -> Unit)? = labelRes?.let { { AppText(it) } },
    placeholder: @Composable (() -> Unit)? = placeholderRes?.let {
        { AppText(it, Modifier.alpha(0.33f)) }
    },
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(


        focusedTextColor = Color.White, //MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedTextColor = Color.White, //MaterialTheme.colorScheme.onPrimaryContainer,
        disabledTextColor = Color.White, //MaterialTheme.colorScheme.onPrimaryContainer,
        errorTextColor = Color.White, //MaterialTheme.colorScheme.onPrimaryContainer,

        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        errorContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),

        cursorColor = MaterialTheme.colorScheme.primary,
        errorCursorColor = MaterialTheme.colorScheme.error,

        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        ),

        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
        errorBorderColor = MaterialTheme.colorScheme.error,

        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLeadingIconColor = MaterialTheme.colorScheme.error,

        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorTrailingIconColor = MaterialTheme.colorScheme.error,

        focusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor = MaterialTheme.colorScheme.error,

        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorPlaceholderColor = MaterialTheme.colorScheme.error,

        focusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,

        focusedPrefixColor = MaterialTheme.colorScheme.primary,
        unfocusedPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorPrefixColor = MaterialTheme.colorScheme.error,

        focusedSuffixColor = MaterialTheme.colorScheme.primary,
        unfocusedSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorSuffixColor = MaterialTheme.colorScheme.error,
    ),
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChanged(it) },
        label = label,
        colors = colors,
        placeholder = placeholder,
        modifier = modifier,
        textStyle = textStyle
    )
}

@Suppress("LongParameterList", "UNUSED_ANONYMOUS_PARAMETER")
@Composable
fun AppEditText(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit,
    @StringRes labelRes: Int? = null,
    @StringRes placeholderRes: Int? = null,
    label: @Composable (() -> Unit)? = labelRes?.let { { AppText(it) } },
    placeholder: @Composable (() -> Unit)? = placeholderRes?.let {
        { AppText(it, Modifier.alpha(0.33f)) }
    },
    colors: TextFieldColors = TextFieldDefaults.colors(

    ),
    supportingText: @Composable (() -> Unit)? = null,
) {
    TextField(
        value = text,
        onValueChange = { onValueChanged(it) },
        supportingText = null, //It is useless
    )
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
fun AppEditOutlinedTextWhitePreview() {

    val text = remember { mutableStateOf("Text Text Text Text Text Text") }
    AppTheme {
        AppEditOutlinedText(
            modifier = Modifier.padding(4.dp),
            text = text.value,
            labelRes = R.string.model_hint,
            placeholderRes = R.string.edit,
            onValueChanged = { text.value = it },
        )
    }
}

/**
 * Превью компонента AppEditText.
 */
@Composable
@Preview
fun AppEditTextPreview() {
    val text = remember { mutableStateOf("") }
    AppEditOutlinedText(
        text = text.value,
        labelRes = R.string.model_hint,
        onValueChanged = { text.value = it },
    )
}