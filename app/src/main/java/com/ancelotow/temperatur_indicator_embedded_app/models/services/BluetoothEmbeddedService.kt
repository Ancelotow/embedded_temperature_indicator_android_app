package com.ancelotow.temperatur_indicator_embedded_app.models.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.ancelotow.temperatur_indicator_embedded_app.models.entities.BluetoothResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.*


const val MESSAGE_READ: Int = 0

@SuppressLint("MissingPermission")
class BluetoothEmbeddedService(
    private val device: BluetoothDevice
) {
    private val socket: BluetoothSocket

    init {
        val list = device.uuids
        val uuid = list[0].uuid
        socket = device.createRfcommSocketToServiceRecord(uuid)
    }

    fun connect() {
        socket.connect()
    }

    suspend fun read(): BluetoothResponse {
        if(socket.isConnected) {
            val numBytes: Int
            val inStream: InputStream = socket.inputStream
            val buffer = ByteArray(1024)
            numBytes = withContext(Dispatchers.IO) {
                inStream.read(buffer, 0, 1024)
            }
            val responseStr = String(buffer, 0, numBytes)
            return Gson().fromJson(responseStr, BluetoothResponse::class.java)
        } else {
            throw Exception("Bluetooth non connecté")
        }
    }

}