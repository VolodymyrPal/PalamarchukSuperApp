package com.hfad.palamarchuksuperapp.core.ui.composables.basic

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Suppress("LongParameterList", "FunctionNaming")

/**
 * Конфигурация текста для использования в текстовых компонентах.
 *
 * @property textStyle Стиль текста. По умолчанию используется [androidx.compose.ui.text.TextStyle.Companion.Default].
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
 * @param textStyle Стиль текста. По умолчанию используется [androidx.compose.ui.text.TextStyle.Companion.Default].
 * @param overflow Определяет, как текст будет обрезаться, если он не помещается в доступное пространство.
 * @param softWrap Определяет, будет ли текст переноситься на новую строку, если он не помещается в одну строку.
 * @param maxLines Максимальное количество строк, которое может занимать текст.
 * @param minLines Минимальное количество строк, которое должен занимать текст.
 * @param onTextLayout Обратный вызов, который вызывается после того, как текст был отрисован.
 * @return [AppTextConfig] с переданными параметрами.
 */
@Composable
@Suppress("LongParameterList")
fun appTextConfig(
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

    return AppTextConfig(
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

@Immutable
data class AppEditOutlinedTextConfig(
    val isError: Boolean = false,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
    val singleLine: Boolean = false,
    val maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    val minLines: Int = 1,
    val interactionSource: MutableInteractionSource? = null,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val trailingIcon: @Composable (() -> Unit)? = null,
    val prefix: @Composable (() -> Unit)? = null,
    val suffix: @Composable (() -> Unit)? = null,
    val supportingText: @Composable (() -> Unit)? = null,
)

@Composable
@Suppress("LongParameterList")
fun appEditOutlinedTextConfig(
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
): AppEditOutlinedTextConfig {
    return AppEditOutlinedTextConfig(
        isError,
        enabled,
        readOnly,
        visualTransformation,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        minLines,
        interactionSource,
        leadingIcon,
        trailingIcon,
        prefix,
        suffix,
        supportingText,
    )
}


/**
 * Возвращает цвета по умолчанию для [androidx.compose.material3.OutlinedTextField] на основе текущей темы.
 *
 * @return [androidx.compose.material3.TextFieldColors] с настройками цветов для [androidx.compose.material3.OutlinedTextField].
 */
@Composable
fun OutlinedTextFieldDefaults.appColors(
    focusedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unfocusedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    errorTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    focusedContainerColor: Color = Color.Unspecified,
    unfocusedContainerColor: Color = Color.Unspecified,
    disabledContainerColor: Color = Color.Unspecified,
    errorContainerColor: Color = Color.Unspecified,
    cursorColor: Color = MaterialTheme.colorScheme.primary,
    errorCursorColor: Color = MaterialTheme.colorScheme.error,
    selectionColors: TextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    ),
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    disabledBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    errorBorderColor: Color = MaterialTheme.colorScheme.error,
    focusedLeadingIconColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLeadingIconColor: Color = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    errorTrailingIconColor: Color = MaterialTheme.colorScheme.error,
    focusedPrefixColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    errorPrefixColor: Color = MaterialTheme.colorScheme.error,
    focusedSuffixColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    errorSuffixColor: Color = MaterialTheme.colorScheme.error
) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = focusedTextColor,
    unfocusedTextColor = unfocusedTextColor,
    disabledTextColor = disabledTextColor,
    errorTextColor = errorTextColor,

    focusedContainerColor = focusedContainerColor,
    unfocusedContainerColor = unfocusedContainerColor,
    disabledContainerColor = disabledContainerColor,
    errorContainerColor = errorContainerColor,

    cursorColor = cursorColor,
    errorCursorColor = errorCursorColor,

    selectionColors = selectionColors,

    focusedBorderColor = focusedBorderColor,
    unfocusedBorderColor = unfocusedBorderColor,
    disabledBorderColor = disabledBorderColor,
    errorBorderColor = errorBorderColor,

    focusedLeadingIconColor = focusedLeadingIconColor,
    unfocusedLeadingIconColor = unfocusedLeadingIconColor,
    disabledLeadingIconColor = disabledLeadingIconColor,
    errorLeadingIconColor = errorLeadingIconColor,

    focusedTrailingIconColor = focusedTrailingIconColor,
    unfocusedTrailingIconColor = unfocusedTrailingIconColor,
    disabledTrailingIconColor = disabledTrailingIconColor,
    errorTrailingIconColor = errorTrailingIconColor,

    focusedPrefixColor = focusedPrefixColor,
    unfocusedPrefixColor = unfocusedPrefixColor,
    disabledPrefixColor = disabledPrefixColor,
    errorPrefixColor = errorPrefixColor,

    focusedSuffixColor = focusedSuffixColor,
    unfocusedSuffixColor = unfocusedSuffixColor,
    disabledSuffixColor = disabledSuffixColor,
    errorSuffixColor = errorSuffixColor
)

/**
 * Возвращает цвета по умолчанию для [androidx.compose.material3.TextField] на основе текущей темы.
 *
 * @return [androidx.compose.material3.TextFieldColors] с настройками цветов для [androidx.compose.material3.TextField].
 */
@Composable
fun TextFieldDefaults.appColors(
    focusedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unfocusedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    errorTextColor: Color = MaterialTheme.colorScheme.error,
    focusedContainerColor: Color = Color.Unspecified,
    unfocusedContainerColor: Color = Color.Unspecified,
    disabledContainerColor: Color = Color.Unspecified,
    errorContainerColor: Color = Color.Unspecified,
    cursorColor: Color = MaterialTheme.colorScheme.primary,
    errorCursorColor: Color = MaterialTheme.colorScheme.error,
    selectionColors: TextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    ),
    focusedLeadingIconColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLeadingIconColor: Color = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    errorTrailingIconColor: Color = MaterialTheme.colorScheme.error
) = TextFieldDefaults.colors(
    focusedTextColor = focusedTextColor,
    unfocusedTextColor = unfocusedTextColor,
    disabledTextColor = disabledTextColor,
    errorTextColor = errorTextColor,

    focusedContainerColor = focusedContainerColor,
    unfocusedContainerColor = unfocusedContainerColor,
    disabledContainerColor = disabledContainerColor,
    errorContainerColor = errorContainerColor,

    cursorColor = cursorColor,
    errorCursorColor = errorCursorColor,

    selectionColors = selectionColors,

    focusedLeadingIconColor = focusedLeadingIconColor,
    unfocusedLeadingIconColor = unfocusedLeadingIconColor,
    disabledLeadingIconColor = disabledLeadingIconColor,
    errorLeadingIconColor = errorLeadingIconColor,

    focusedTrailingIconColor = focusedTrailingIconColor,
    unfocusedTrailingIconColor = unfocusedTrailingIconColor,
    disabledTrailingIconColor = disabledTrailingIconColor,
    errorTrailingIconColor = errorTrailingIconColor
)

@Composable
@Preview(showBackground = false)
internal fun ColorTextFieldPreview() {

    Column {

        AppTextField(
            modifier = Modifier.padding(6.dp),
            value = "123",
            onValueChange = {},
            editTextConfig = rememberTextFieldConfig(
//            enabled = false,
            ),
            colors = TextFieldDefaults.appColors(

            ),
        )

        AppTextField(
            modifier = Modifier.padding(6.dp),
            value = "123",
            onValueChange = {},
            editTextConfig = rememberTextFieldConfig(
                enabled = false,
            )
        )
    }
}

@Immutable
data class AppTextFieldConfig(
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val isError: Boolean = false,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
    val singleLine: Boolean = false,
    val maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    val minLines: Int = 1,
    val interactionSource: MutableInteractionSource? = null,
    val leadingIcon: @Composable (() -> Unit)? = null, //It is rarely used
    val trailingIcon: @Composable (() -> Unit)? = null, //It is rarely used
    val prefix: @Composable (() -> Unit)? = null, //It is rarely used
    val suffix: @Composable (() -> Unit)? = null, //It is rarely used
    val supportingText: @Composable (() -> Unit)? = null, //It is rarely used

)

@Composable
@Suppress("LongParameterList")
fun rememberTextFieldConfig(
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    leadingIcon: @Composable (() -> Unit)? = null, //It is rarely used
    trailingIcon: @Composable (() -> Unit)? = null, //It is rarely used
    prefix: @Composable (() -> Unit)? = null, //It is rarely used
    suffix: @Composable (() -> Unit)? = null, //It is rarely used
    supportingText: @Composable (() -> Unit)? = null, //It is rarely used
): AppTextFieldConfig {
    return remember {
        AppTextFieldConfig(
            enabled,
            readOnly,
            isError,
            visualTransformation,
            keyboardOptions,
            keyboardActions,
            singleLine,
            maxLines,
            minLines,
            interactionSource,
            leadingIcon,
            trailingIcon,
            prefix,
            suffix,
            supportingText
        )
    }
}