package com.nestorian87.orionix_track.domain.repository

import com.nestorian87.orionix_track.domain.realtime.TripRealtimeEvent
import kotlinx.coroutines.flow.Flow

interface TripRealtimeRepository {
    fun observeTrip(tripId: Long): Flow<TripRealtimeEvent>

    fun observeTrips(tripIds: Set<Long>): Flow<TripRealtimeEvent>
}
