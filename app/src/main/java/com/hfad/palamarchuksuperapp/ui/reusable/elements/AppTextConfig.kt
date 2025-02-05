package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Suppress("LongParameterList", "FunctionNaming")

/**
 * Конфигурация текста для использования в текстовых компонентах.
 *
 * @property textStyle Стиль текста. По умолчанию используется [TextStyle.Default].
 * @property overflow Определяет, как текст будет обрезаться, если он не помещается в доступное пространство.
 * @property softWrap Определяет, будет ли текст переноситься на новую строку, если он не помещается в одну строку.
 * @property maxLines Максимальное количество строк, которое может занимать текст.
 * @property minLines Минимальное количество строк, которое должен занимать текст.
 * @property onTextLayout Обратный вызов, который вызывается после того, как текст был отрисован.
 */
@Immutable
data class AppTextConfig(
    val textStyle: TextStyle = TextStyle.Default,
    val overflow: TextOverflow = TextOverflow.Clip,
    val softWrap: Boolean = true,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = 1,
    val onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    val fontSize: TextUnit = TextUnit.Unspecified,
    val fontStyle: FontStyle? = null,
    val fontWeight: FontWeight? = null,
    val fontFamily: FontFamily? = null,
    val letterSpacing: TextUnit = TextUnit.Unspecified,
    val textDecoration: TextDecoration? = null,
    val textAlign: TextAlign? = null,
    val lineHeight: TextUnit = TextUnit.Unspecified,
)

/**
 * Запоминает конфигурацию текста для использования в текстовых компонентах.
 *
 * @param textStyle Стиль текста. По умолчанию используется [TextStyle.Default].
 * @param overflow Определяет, как текст будет обрезаться, если он не помещается в доступное пространство.
 * @param softWrap Определяет, будет ли текст переноситься на новую строку, если он не помещается в одну строку.
 * @param maxLines Максимальное количество строк, которое может занимать текст.
 * @param minLines Минимальное количество строк, которое должен занимать текст.
 * @param onTextLayout Обратный вызов, который вызывается после того, как текст был отрисован.
 * @return [AppTextConfig] с переданными параметрами.
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
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
): AppTextConfig {

    return remember {
        AppTextConfig(
            textStyle,
            overflow,
            softWrap,
            maxLines,
            minLines,
            onTextLayout,
            fontSize,
            fontStyle,
            fontWeight,
            fontFamily,
            letterSpacing,
            textDecoration,
            textAlign,
            lineHeight,
        )
    }
}

/**
 * Возвращает цвета по умолчанию для [OutlinedTextField] на основе текущей темы.
 *
 * @return [TextFieldColors] с настройками цветов для [OutlinedTextField].
 */
@Composable
fun OutlinedTextFieldDefaults.appColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    errorTextColor = MaterialTheme.colorScheme.onPrimaryContainer,

    focusedContainerColor = Color.Unspecified,
    unfocusedContainerColor = Color.Unspecified,
    disabledContainerColor = Color.Unspecified,
    errorContainerColor = Color.Unspecified,

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
)

/**
 * Возвращает цвета по умолчанию для [TextField] на основе текущей темы.
 *
 * @return [TextFieldColors] с настройками цветов для [TextField].
 */
@Composable
fun TextFieldDefaults.appColors() = TextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    errorTextColor = MaterialTheme.colorScheme.error,

    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
    errorContainerColor = MaterialTheme.colorScheme.errorContainer,

    cursorColor = MaterialTheme.colorScheme.primary,
    errorCursorColor = MaterialTheme.colorScheme.error,

    selectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    ),

    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
    disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
    errorIndicatorColor = MaterialTheme.colorScheme.error,

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
)