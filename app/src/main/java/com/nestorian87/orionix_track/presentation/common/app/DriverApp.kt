package com.nestorian87.orionix_track.presentation.common.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nestorian87.orionix_track.presentation.common.navigation.AppNavHost
import com.nestorian87.orionix_track.presentation.theme.OrionixTrackTheme

@Composable
fun DriverApp(
    rootViewModel: RootViewModel = hiltViewModel()
) {
    val uiState = rootViewModel.uiState.collectAsStateWithLifecycle()

    OrionixTrackTheme(themeMode = uiState.value.themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost(
                uiState = uiState.value,
                onThemeModeChange = rootViewModel::onThemeModeChange,
                onLogout = rootViewModel::logout
            )
        }
    }
}
