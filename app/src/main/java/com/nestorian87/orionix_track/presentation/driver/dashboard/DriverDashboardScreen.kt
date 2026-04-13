package com.nestorian87.orionix_track.presentation.driver.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.model.AuthSession
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixSecondaryButton
import com.nestorian87.orionix_track.presentation.theme.AppThemeMode

@Composable
fun DriverDashboardScreen(
    session: AuthSession,
    themeMode: AppThemeMode,
    isLogoutLoading: Boolean,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    OrionixBackdrop {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrionixPanel {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${session.driver.name} ${session.driver.surname}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = session.driver.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            if (themeMode == AppThemeMode.DARK) {
                                R.string.theme_dark
                            } else if (themeMode == AppThemeMode.LIGHT) {
                                R.string.theme_light
                            } else {
                                R.string.theme_system
                            }
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OrionixSecondaryButton(
                        text = stringResource(R.string.action_log_out),
                        enabled = !isLogoutLoading,
                        onClick = onLogout
                    )
                }
            }
        }
    }
}
