package com.ancelotow.temperatur_indicator_embedded_app.models.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.*

const val MESSAGE_READ: Int = 0

@SuppressLint("MissingPermission")
class BluetoothEmbeddedService(
    private val device: BluetoothDevice
) {
    private val socket: BluetoothSocket;

    init {
        val list = device.uuids
        val uuid = list[0].uuid
        socket = device.createRfcommSocketToServiceRecord(uuid)
    }

    suspend fun getMessage(): String {
        socket.connect()
        val numBytes: Int
        val inStream: InputStream = socket.inputStream
        while (true) {
            Log.e("Test", "1")

            val buffer = ByteArray(10)
            numBytes = withContext(Dispatchers.IO) {
                inStream.read(buffer, 0, 10)
            }
            Log.e("Test", "2")
            return String(buffer, 0, numBytes)
        }
    }

}