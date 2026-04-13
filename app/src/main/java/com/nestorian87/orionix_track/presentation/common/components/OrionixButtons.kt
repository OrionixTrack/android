package com.nestorian87.orionix_track.presentation.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OrionixPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colors.onPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.onPrimary.copy(alpha = 0.55f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.primary, colors.primaryContainer)
                    )
                )
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun OrionixSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, colors.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.onSurface,
            containerColor = colors.surface.copy(alpha = 0.72f),
            disabledContentColor = colors.onSurface.copy(alpha = 0.5f),
            disabledContainerColor = colors.surface.copy(alpha = 0.35f)
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
