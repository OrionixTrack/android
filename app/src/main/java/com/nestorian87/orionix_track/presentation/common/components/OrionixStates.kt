package com.nestorian87.orionix_track.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.presentation.common.mapper.toDefaultText

@Composable
fun OrionixStatePanel(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    isError: Boolean = false,
    isLoading: Boolean = false,
    action: (@Composable () -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val contentColor = if (isError) colors.error else colors.onSurface

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = colors.surface.copy(alpha = 0.9f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = colors.primary,
                    strokeWidth = 3.dp
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            message?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
            action?.invoke()
        }
    }
}

@Composable
fun OrionixErrorPanel(
    error: AppError,
    modifier: Modifier = Modifier
) {
    OrionixStatePanel(
        title = stringResource(R.string.error_title),
        modifier = modifier,
        message = error.toDefaultText(),
        isError = true
    )
}
