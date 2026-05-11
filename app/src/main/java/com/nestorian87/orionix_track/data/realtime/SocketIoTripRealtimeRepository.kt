package com.nestorian87.orionix_track.data.realtime

import com.nestorian87.orionix_track.domain.repository.AuthRepository
import com.nestorian87.orionix_track.domain.repository.TripRealtimeRepository
import com.nestorian87.orionix_track.domain.realtime.TripRealtimeEvent
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

@Singleton
class SocketIoTripRealtimeRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val realtimeUrlProvider: RealtimeUrlProvider,
    private val json: Json,
    private val logger: RealtimeLogger
) : TripRealtimeRepository {
    override fun observeTrip(tripId: Long): Flow<TripRealtimeEvent> {
        return observeTrips(setOf(tripId))
    }

    override fun observeTrips(tripIds: Set<Long>): Flow<TripRealtimeEvent> = callbackFlow {
        val subscribedTripIds = tripIds.filter { it > 0L }.toSet()
        if (subscribedTripIds.isEmpty()) {
            logger.debug("observeTrips skipped: no valid trip ids")
            close()
            return@callbackFlow
        }

        val token = authRepository.session.first()?.accessToken
        if (token.isNullOrBlank()) {
            logger.warn("observeTrips failed: missing access token")
            trySend(TripRealtimeEvent.Error(message = "Missing access token"))
            close()
            return@callbackFlow
        }

        val options = IO.Options.builder()
            .setAuth(mapOf(AUTH_TOKEN_KEY to token))
            .setReconnection(true)
            .build()
        val realtimeUrl = realtimeUrlProvider.trackingUrl()
        val socket = IO.socket(realtimeUrl, options)
        logger.debug("socket created url=$realtimeUrl tripIds=$subscribedTripIds")

        val onConnect = Emitter.Listener {
            logger.debug("socket connected id=${socket.id()} tripIds=$subscribedTripIds")
            trySend(TripRealtimeEvent.Connected)
            subscribedTripIds.forEach { tripId ->
                logger.debug("emit $SUBSCRIBE_TRIP_EVENT tripId=$tripId")
                socket.emit(SUBSCRIBE_TRIP_EVENT, tripId.toSocketTripSubscription(json))
            }
        }
        val onReconnect = Emitter.Listener {
            logger.debug("socket reconnected tripIds=$subscribedTripIds")
            trySend(TripRealtimeEvent.Reconnected)
            subscribedTripIds.forEach { tripId ->
                logger.debug("emit $SUBSCRIBE_TRIP_EVENT after reconnect tripId=$tripId")
                socket.emit(SUBSCRIBE_TRIP_EVENT, tripId.toSocketTripSubscription(json))
            }
        }
        val onDisconnect = Emitter.Listener { args ->
            logger.debug("socket disconnected args=${args.toDebugString()}")
            trySend(TripRealtimeEvent.Disconnected)
        }
        val onConnectError = Emitter.Listener { args ->
            val message = args.firstOrNull()?.toString()
            logger.warn("socket connect error message=$message")
            trySend(TripRealtimeEvent.Error(message = message))
        }
        val onTelemetry = Emitter.Listener { args ->
            logger.debug("received $TELEMETRY_UPDATE_EVENT payload=${args.toDebugString()}")
            val event = args.firstOrNull()
                ?.toTelemetryEvent(json)
                ?.takeIf { it.tripId in subscribedTripIds }
            if (event != null) {
                trySend(event)
            } else {
                logger.warn("ignored $TELEMETRY_UPDATE_EVENT: invalid or unsubscribed payload")
            }
        }
        val onStatus = Emitter.Listener { args ->
            logger.debug("received $TRIP_STATUS_EVENT payload=${args.toDebugString()}")
            val event = args.firstOrNull()
                ?.toStatusEvent(json)
                ?.takeIf { it.tripId in subscribedTripIds }
            if (event != null) {
                trySend(event)
            } else {
                logger.warn("ignored $TRIP_STATUS_EVENT: invalid or unsubscribed payload")
            }
        }

        socket
            .on(Socket.EVENT_CONNECT, onConnect)
            .on(RECONNECT_EVENT, onReconnect)
            .on(Socket.EVENT_DISCONNECT, onDisconnect)
            .on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            .on(TELEMETRY_UPDATE_EVENT, onTelemetry)
            .on(TRIP_STATUS_EVENT, onStatus)

        logger.debug("socket connecting tripIds=$subscribedTripIds")
        socket.connect()

        awaitClose {
            subscribedTripIds.forEach { tripId ->
                logger.debug("emit $UNSUBSCRIBE_TRIP_EVENT tripId=$tripId")
                socket.emit(UNSUBSCRIBE_TRIP_EVENT, tripId.toSocketTripSubscription(json))
            }
            socket.off(Socket.EVENT_CONNECT, onConnect)
            socket.off(RECONNECT_EVENT, onReconnect)
            socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
            socket.off(TELEMETRY_UPDATE_EVENT, onTelemetry)
            socket.off(TRIP_STATUS_EVENT, onStatus)
            socket.disconnect()
            socket.close()
            logger.debug("socket closed tripIds=$subscribedTripIds")
        }
    }

    private fun Array<out Any>.toDebugString(): String {
        return joinToString(prefix = "[", postfix = "]") { it.toString() }
    }

    companion object {
        private const val AUTH_TOKEN_KEY = "token"
        private const val SUBSCRIBE_TRIP_EVENT = "subscribe:trip"
        private const val UNSUBSCRIBE_TRIP_EVENT = "unsubscribe:trip"
        private const val TELEMETRY_UPDATE_EVENT = "telemetry:update"
        private const val TRIP_STATUS_EVENT = "trip:status"
        private const val RECONNECT_EVENT = "reconnect"
    }
}
