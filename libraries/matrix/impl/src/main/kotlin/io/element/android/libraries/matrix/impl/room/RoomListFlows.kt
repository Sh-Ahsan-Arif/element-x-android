package io.element.android.libraries.matrix.impl.room

import io.element.android.libraries.matrix.impl.util.mxCallbackFlow
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import org.matrix.rustcomponents.sdk.RoomList
import org.matrix.rustcomponents.sdk.RoomListEntriesListener
import org.matrix.rustcomponents.sdk.RoomListEntriesUpdate
import org.matrix.rustcomponents.sdk.RoomListEntry
import org.matrix.rustcomponents.sdk.RoomListState
import org.matrix.rustcomponents.sdk.RoomListStateListener
import org.matrix.rustcomponents.sdk.SlidingSyncListLoadingState
import org.matrix.rustcomponents.sdk.SlidingSyncListStateObserver

fun RoomList.stateFlow(): Flow<RoomListState> =
    mxCallbackFlow {
        val listener = object : RoomListStateListener {
            override fun onUpdate(state: RoomListState) {
                trySendBlocking(state)
            }
        }
        state(listener)
    }

fun RoomList.loadingStateFlow(): Flow<SlidingSyncListLoadingState> =
    mxCallbackFlow {
        val listener = object : SlidingSyncListStateObserver {
            override fun didReceiveUpdate(newState: SlidingSyncListLoadingState) {
                trySendBlocking(newState)
            }
        }
        val result = entriesLoadingState(listener)
        send(result.entriesLoadingState)
        result.entriesLoadingStateStream
    }

fun RoomList.roomListEntriesUpdateFlow(onInitialList: suspend (List<RoomListEntry>) -> Unit): Flow<RoomListEntriesUpdate> =
    mxCallbackFlow {
        val listener = object : RoomListEntriesListener {
            override fun onUpdate(roomEntriesUpdate: RoomListEntriesUpdate) {
                trySendBlocking(roomEntriesUpdate)
            }
        }
        val result = entries(listener)
        onInitialList(result.entries)
        result.entriesStream
    }

