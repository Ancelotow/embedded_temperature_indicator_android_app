package com.ancelotow.temperatur_indicator_embedded_app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import android.os.Handler
import android.os.Message
import android.provider.Settings.Global.DEVICE_NAME
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateError
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateJsonError
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateLoading
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateSuccess
import com.ancelotow.temperatur_indicator_embedded_app.models.services.BluetoothEmbeddedService
import com.ancelotow.temperatur_indicator_embedded_app.models.services.MESSAGE_READ
import java.util.*


class MainActivity : AppCompatActivity() {

    private val deviceName = "HC-06"
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        bluetoothConnection()
    }

    private fun checkPermissionBluetoothDevice(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun bluetoothConnection() {
        val txtTemperature = findViewById<TextView>(R.id.txtTemperature)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        this.bluetoothAdapter = bluetoothManager.getAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if (!checkPermissionBluetoothDevice(context = this)) {
                return
            } else {

            }
        }

        val deviceEmbedded = pairedDevices?.find { device -> device.name == deviceName }
        if (deviceEmbedded != null) {
            val vm: MainViewModel by viewModels { MainViewModel.Factory(deviceEmbedded) }
            viewModel = vm
            viewModel.bluetooth.observe(this) {
                when (it) {
                    is BluetoothEmbeddedStateError -> {
                        it.ex.message?.let { it1 -> Log.e("error", it1) }
                    }
                    is BluetoothEmbeddedStateJsonError -> {
                        it.ex.message?.let { it1 -> Log.e("error json", it1) }
                    }
                    BluetoothEmbeddedStateLoading -> {
                        // TODO:
                    }
                    is BluetoothEmbeddedStateSuccess -> {
                        txtTemperature.text = "${it.response.temperature.celsius}°C"
                    }
                }
            }
        }

    }

}

