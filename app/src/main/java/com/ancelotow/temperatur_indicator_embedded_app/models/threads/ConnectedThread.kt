package com.ancelotow.temperatur_indicator_embedded_app.models.threads

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import com.ancelotow.temperatur_indicator_embedded_app.models.services.MESSAGE_READ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectedThread(
    private val mmSocket: BluetoothSocket,
    private val handler: Handler
) : Thread() {

    private val tag = "ConnectedThread"
    private val mmInStream: InputStream = mmSocket.inputStream
    private val mmOutStream: OutputStream = mmSocket.outputStream
    private val mmBuffer: ByteArray = ByteArray(10)

    override fun run() {
        var numBytes: Int
        while (true) {
            if (mmInStream.available() == 0) {
                continue;
            }
            val count = mmInStream.available()
            val buffer = ByteArray(count)
            numBytes = mmInStream.read(buffer, 0, count)

            val readMsg = handler.obtainMessage(
                MESSAGE_READ, numBytes, -1,
                mmBuffer
            )
            readMsg.sendToTarget()
        }
    }

    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
            Log.e(tag, "Could not close the connect socket", e)
        }
    }
}