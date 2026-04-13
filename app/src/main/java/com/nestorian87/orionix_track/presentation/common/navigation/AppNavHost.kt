package com.nestorian87.orionix_track.presentation.common.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nestorian87.orionix_track.presentation.auth.login.LoginScreen
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.driver.dashboard.DriverDashboardScreen
import com.nestorian87.orionix_track.presentation.common.app.RootAuthState
import com.nestorian87.orionix_track.presentation.common.app.RootUiState
import com.nestorian87.orionix_track.presentation.theme.AppThemeMode

@Composable
fun AppNavHost(
    uiState: RootUiState,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(uiState.authState) {
        val targetRoute = when (uiState.authState) {
            RootAuthState.Resolving -> return@LaunchedEffect
            RootAuthState.Unauthenticated -> AppDestination.LOGIN
            is RootAuthState.Authenticated -> AppGraphRoute.MAIN
        }

        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppGraphRoute.SPLASH
    ) {
        composable(AppGraphRoute.SPLASH) {
            SplashScreen()
        }
        navigation(
            route = AppGraphRoute.AUTH,
            startDestination = AppDestination.LOGIN
        ) {
            composable(AppDestination.LOGIN) {
                LoginScreen()
            }
        }
        navigation(
            route = AppGraphRoute.MAIN,
            startDestination = AppDestination.DASHBOARD
        ) {
            composable(AppDestination.DASHBOARD) {
                val authState = uiState.authState
                if (authState is RootAuthState.Authenticated) {
                    DriverDashboardScreen(
                        session = authState.session,
                        themeMode = uiState.themeMode,
                        isLogoutLoading = uiState.isLogoutLoading,
                        onThemeModeChange = onThemeModeChange,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    OrionixBackdrop {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
