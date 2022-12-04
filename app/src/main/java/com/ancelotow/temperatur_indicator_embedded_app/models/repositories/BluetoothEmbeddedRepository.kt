package com.ancelotow.temperatur_indicator_embedded_app.models.repositories

import android.bluetooth.BluetoothDevice
import com.ancelotow.temperatur_indicator_embedded_app.models.services.BluetoothEmbeddedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object BluetoothEmbeddedRepository {

    suspend fun getMessage(device: BluetoothDevice): Flow<BluetoothEmbeddedState> {
        val service = BluetoothEmbeddedService(device);
        return flow {
            emit(BluetoothEmbeddedStateLoading)
            try {
                emit(BluetoothEmbeddedStateSuccess(service.getMessage()))
            } catch (e: Exception) {
                emit(BluetoothEmbeddedStateError(e))
            }
        }.flowOn(Dispatchers.IO)
    }

}

sealed class BluetoothEmbeddedState
object BluetoothEmbeddedStateLoading: BluetoothEmbeddedState()
data class BluetoothEmbeddedStateSuccess(val message: String?): BluetoothEmbeddedState()
data class BluetoothEmbeddedStateError(val ex: Exception): BluetoothEmbeddedState()