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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nestorian87.orionix_track.presentation.auth.forgot.ForgotPasswordScreen
import com.nestorian87.orionix_track.presentation.auth.login.LoginScreen
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.common.app.RootAuthState
import com.nestorian87.orionix_track.presentation.common.app.RootUiState
import com.nestorian87.orionix_track.presentation.driver.trips.DriverTripsScreen
import com.nestorian87.orionix_track.presentation.driver.trips.details.TripDetailsScreen
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
                LoginScreen(
                    onForgotPasswordClick = {
                        navController.navigate(AppDestination.FORGOT_PASSWORD)
                    }
                )
            }
            composable(AppDestination.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        navigation(
            route = AppGraphRoute.MAIN,
            startDestination = AppDestination.TRIPS
        ) {
            composable(AppDestination.TRIPS) {
                val authState = uiState.authState
                if (authState is RootAuthState.Authenticated) {
                    DriverTripsScreen(
                        onTripClick = { tripId ->
                            navController.navigate(AppDestination.tripDetails(tripId))
                        },
                        isLogoutLoading = uiState.isLogoutLoading,
                        onLogout = onLogout
                    )
                }
            }
            composable(
                route = AppDestination.TRIP_DETAILS,
                arguments = listOf(
                    navArgument(AppDestination.TRIP_ID_ARG) {
                        type = NavType.StringType
                    }
                )
            ) {
                TripDetailsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
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
