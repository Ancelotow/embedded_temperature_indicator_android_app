package com.ancelotow.temperatur_indicator_embedded_app.models.repositories

import android.bluetooth.BluetoothDevice
import com.ancelotow.temperatur_indicator_embedded_app.models.entities.BluetoothResponse
import com.ancelotow.temperatur_indicator_embedded_app.models.services.BluetoothEmbeddedService
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

object BluetoothEmbeddedRepository {

    suspend fun getMessage(device: BluetoothDevice): Flow<BluetoothEmbeddedState> {
        val service = BluetoothEmbeddedService(device);
        service.connect();
        return flow {
            emit(BluetoothEmbeddedStateLoading)
            try {
                while(currentCoroutineContext().isActive) {
                    try{
                        emit(BluetoothEmbeddedStateSuccess(service.read()))
                    } catch (e: JsonSyntaxException) {
                        emit(BluetoothEmbeddedStateJsonError(e))
                    }
                    delay(1000)
                }
            } catch (e: Exception) {
                emit(BluetoothEmbeddedStateError(e))
            }
        }.flowOn(Dispatchers.IO)
    }

}

sealed class BluetoothEmbeddedState
object BluetoothEmbeddedStateLoading: BluetoothEmbeddedState()
data class BluetoothEmbeddedStateSuccess(val response: BluetoothResponse): BluetoothEmbeddedState()
data class BluetoothEmbeddedStateError(val ex: Exception): BluetoothEmbeddedState()
data class BluetoothEmbeddedStateJsonError(val ex: JsonSyntaxException): BluetoothEmbeddedState()