package com.nestorian87.orionix_track.presentation.driver.trips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.common.components.OrionixErrorPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixLabeledTextField
import com.nestorian87.orionix_track.presentation.common.components.OrionixLoadingDialog
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixStatePanel
import com.nestorian87.orionix_track.presentation.common.formatter.formatIsoDateTime
import com.nestorian87.orionix_track.presentation.driver.profile.DriverProfileScreen
import com.nestorian87.orionix_track.presentation.driver.trips.components.TripCard
import com.nestorian87.orionix_track.presentation.driver.trips.components.TripStatusChip
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun DriverTripsScreen(
    onTripClick: (Long) -> Unit,
    onLogout: () -> Unit,
    isLogoutLoading: Boolean,
    viewModel: DriverTripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(DriverMainTab.CURRENT) }
    var hasHandledInitialTabSelection by rememberSaveable { mutableStateOf(false) }
    var hasHandledInitialResume by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedTab) {
        if (!hasHandledInitialTabSelection) {
            hasHandledInitialTabSelection = true
            return@LaunchedEffect
        }

        when (selectedTab) {
            DriverMainTab.CURRENT -> viewModel.refreshCurrentTrips()
            DriverMainTab.HISTORY -> viewModel.refreshHistoryTrips()
            DriverMainTab.PROFILE -> Unit
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (!hasHandledInitialResume) {
            hasHandledInitialResume = true
            return@LifecycleEventEffect
        }

        when (selectedTab) {
            DriverMainTab.CURRENT -> viewModel.refreshCurrentTrips()
            DriverMainTab.HISTORY -> viewModel.refreshHistoryTrips()
            DriverMainTab.PROFILE -> Unit
        }
    }

    OrionixBackdrop {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                DriverBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { padding ->
            when (selectedTab) {
                DriverMainTab.CURRENT -> CurrentTripsTab(
                    uiState = uiState,
                    contentPadding = padding,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onTripClick = onTripClick,
                    onLoadMore = viewModel::loadMoreAssignedTrips
                )

                DriverMainTab.HISTORY -> HistoryTripsTab(
                    uiState = uiState,
                    contentPadding = padding,
                    onSearchQueryChanged = viewModel::onHistorySearchQueryChanged,
                    onTripClick = onTripClick,
                    onLoadMore = viewModel::loadMoreHistoryTrips
                )

                DriverMainTab.PROFILE -> DriverProfileScreen(
                    contentPadding = padding,
                    isLogoutLoading = isLogoutLoading,
                    onLogout = onLogout
                )
            }
        }
        OrionixLoadingDialog(
            isVisible = isLogoutLoading,
            message = stringResource(R.string.loading_text)
        )
    }
}

@Composable
private fun DriverBottomBar(
    selectedTab: DriverMainTab,
    onTabSelected: (DriverMainTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 0.dp
    ) {
        val colors = MaterialTheme.colorScheme
        val selectedColor = colors.tertiary
        DriverMainTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.titleResId),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = stringResource(tab.titleResId))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    indicatorColor = selectedColor.copy(alpha = 0.2f),
                    unselectedIconColor = colors.onSurfaceVariant.copy(alpha = 0.76f),
                    unselectedTextColor = colors.onSurfaceVariant.copy(alpha = 0.82f)
                )
            )
        }
    }
}

@Composable
private fun CurrentTripsTab(
    uiState: DriverTripsUiState,
    contentPadding: PaddingValues,
    onSearchQueryChanged: (String) -> Unit,
    onTripClick: (Long) -> Unit,
    onLoadMore: () -> Unit
) {
    TripListTab(
        title = stringResource(R.string.current_trips_title),
        subtitle = stringResource(R.string.current_trips_subtitle),
        activeTrip = uiState.activeTrip,
        isActiveTripLoading = uiState.activeTripLoading,
        activeTripError = uiState.activeTripError,
        searchQuery = uiState.searchQuery,
        trips = uiState.assignedTrips,
        isLoading = uiState.assignedTripsLoading,
        isAppending = uiState.assignedTripsAppending,
        hasMore = uiState.hasMoreAssignedTrips,
        error = uiState.assignedTripsError,
        emptyTitle = stringResource(R.string.current_trips_empty),
        contentPadding = contentPadding,
        onSearchQueryChanged = onSearchQueryChanged,
        onTripClick = onTripClick,
        onLoadMore = onLoadMore
    )
}

@Composable
private fun HistoryTripsTab(
    uiState: DriverTripsUiState,
    contentPadding: PaddingValues,
    onSearchQueryChanged: (String) -> Unit,
    onTripClick: (Long) -> Unit,
    onLoadMore: () -> Unit
) {
    TripListTab(
        title = stringResource(R.string.history_trips_title),
        subtitle = stringResource(R.string.history_trips_subtitle),
        searchQuery = uiState.historySearchQuery,
        trips = uiState.historyTrips,
        isLoading = uiState.historyTripsLoading,
        isAppending = uiState.historyTripsAppending,
        hasMore = uiState.hasMoreHistoryTrips,
        error = uiState.historyTripsError,
        emptyTitle = stringResource(R.string.history_trips_empty),
        contentPadding = contentPadding,
        onSearchQueryChanged = onSearchQueryChanged,
        onTripClick = onTripClick,
        onLoadMore = onLoadMore
    )
}

@Composable
private fun TripListTab(
    title: String,
    subtitle: String,
    activeTrip: Trip? = null,
    isActiveTripLoading: Boolean = false,
    activeTripError: AppError? = null,
    searchQuery: String,
    trips: List<Trip>,
    isLoading: Boolean,
    isAppending: Boolean,
    hasMore: Boolean,
    error: AppError?,
    emptyTitle: String,
    contentPadding: PaddingValues,
    onSearchQueryChanged: (String) -> Unit,
    onTripClick: (Long) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()
    val displayedTrips = trips.filterNot { it.id == activeTrip?.id }
    val canLoadMore = hasMore && !isLoading && !isAppending && error == null && displayedTrips.isNotEmpty()

    LaunchedEffect(listState, canLoadMore, displayedTrips.size) {
        if (!canLoadMore) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onLoadMore()
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item {
            ScreenTitle(
                title = title,
                subtitle = subtitle
            )
        }
        if (activeTrip != null) {
            item {
                ActiveTripHero(
                    trip = activeTrip,
                    onClick = { onTripClick(activeTrip.id) }
                )
            }
        } else if (isActiveTripLoading) {
            item {
                OrionixStatePanel(
                    title = stringResource(R.string.active_trip_loading),
                    isLoading = true
                )
            }
        } else if (activeTripError != null && trips.isEmpty()) {
            item {
                OrionixErrorPanel(error = activeTripError)
            }
        }
        item {
            OrionixLabeledTextField(
                label = stringResource(R.string.trips_search_label),
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = stringResource(R.string.trips_search_placeholder)
            )
        }

        items(
            items = displayedTrips,
            key = { it.id }
        ) { trip ->
            TripCard(
                trip = trip,
                onClick = { onTripClick(trip.id) }
            )
        }

        if (isLoading) {
            item {
                OrionixStatePanel(
                    title = stringResource(R.string.loading_text),
                    isLoading = true
                )
            }
        }
        if (error != null) {
            item {
                OrionixErrorPanel(error = error)
            }
        }
        if (isAppending) {
            item {
                OrionixStatePanel(
                    title = stringResource(R.string.loading_more_text),
                    isLoading = true
                )
            }
        }
        if (!isLoading && displayedTrips.isEmpty() && activeTrip == null && error == null) {
            item {
                OrionixStatePanel(
                    title = emptyTitle
                )
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun ActiveTripHero(
    trip: Trip,
    onClick: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val route = listOfNotNull(
        trip.startAddress?.compactAddress(),
        trip.finishAddress?.compactAddress()
    ).joinToString(" → ").ifBlank {
        stringResource(R.string.trip_route_unavailable)
    }

    OrionixPanel(
        modifier = Modifier.clickable(
            role = Role.Button,
            onClick = onClick
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.active_trip_title),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    TripStatusChip(status = trip.status)
                }
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = route,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatIsoDateTime(trip.actualStart ?: trip.plannedStart, locale),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.action_open_details),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenTitle(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private enum class DriverMainTab(
    val titleResId: Int,
    val icon: ImageVector
) {
    CURRENT(R.string.nav_current, Icons.Default.LocalShipping),
    HISTORY(R.string.nav_history, Icons.Default.History),
    PROFILE(R.string.nav_profile, Icons.Default.Person)
}

private const val LOAD_MORE_THRESHOLD = 4

private fun String.compactAddress(): String? {
    return split(',')
        .firstOrNull()
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}
