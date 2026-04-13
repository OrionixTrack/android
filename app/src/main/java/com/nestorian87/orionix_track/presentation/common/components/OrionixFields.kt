package com.nestorian87.orionix_track.presentation.common.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun OrionixLabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.onSurface),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            },
            shape = RoundedCornerShape(22.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = remember { MutableInteractionSource() },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceVariant.copy(alpha = 0.52f),
                unfocusedContainerColor = colors.surfaceVariant.copy(alpha = 0.42f),
                disabledContainerColor = colors.surfaceVariant.copy(alpha = 0.24f),
                focusedBorderColor = colors.outline,
                unfocusedBorderColor = colors.outlineVariant,
                cursorColor = colors.primary,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface
            )
        )
    }
}
