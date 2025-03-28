package com.hfad.palamarchuksuperapp.core.ui.composables.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Suppress("LongParameterList", "FunctionNaming", "LongMethod")
@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    description: String,
    confirmText: String = "Подтвердить",
    dismissText: String = "Отмена",
    icon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            tint = MaterialTheme.colorScheme.error
        )
    },
) {
    if (show) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                modifier = modifier
                    // .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Иконка
                    icon?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                        ) {
                            it()
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Заголовок
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Описание
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Кнопки
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(dismissText, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onConfirm()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text(confirmText, color = MaterialTheme.colorScheme.background)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun ConfirmationDialogPreview() {
    MaterialTheme {
        ConfirmationDialog(
            show = true,
            onDismiss = {},
            onConfirm = {},
            title = "Удалить чат?",
            description = "Это действие нельзя будет отменить. Все сообщения в этом чате будут удалены навсегда."
        )
    }
} 