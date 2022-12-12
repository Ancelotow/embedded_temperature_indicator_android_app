package com.ancelotow.temperatur_indicator_embedded_app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ancelotow.temperatur_indicator_embedded_app.models.entities.TemperatureIndicator
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateError
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateJsonError
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateLoading
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedStateSuccess
import com.ancelotow.temperatur_indicator_embedded_app.models.services.ColorTemperatureService
import java.util.*


class MainActivity : AppCompatActivity() {

    private val deviceName = "HC-06"
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var viewModel: MainViewModel
    private val stringsTemperature = intArrayOf(
        R.string.temperature_kelvin,
        R.string.temperature_celsius,
        R.string.temperature_fahrenheit
    )
    private var currentStringTemperature = stringsTemperature[0]

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

    @SuppressLint("MissingPermission", "ClickableViewAccessibility")
    private fun bluetoothConnection() {
        val txtTemperature = findViewById<TextView>(R.id.txtTemperature)
        val circleIndicator = findViewById<ImageView>(R.id.circle_indicator)
        val txtFeltIndicator = findViewById<TextView>(R.id.txtFeltIndicator)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        this.bluetoothAdapter = bluetoothManager.getAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if (!checkPermissionBluetoothDevice(context = this)) {
                return
            }
        }
        txtTemperature.setOnTouchListener { _, event ->
            handleTemperatureIndicator()
            true
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
                        txtTemperature.text = getString(
                            R.string.temperature_celsius,
                            it.response.temperature.celsius
                        )
                        val color = ColorTemperatureService(it.response.temperature.celsius).getColor()
                        circleIndicator.background = getShape(color)
                        drawFeltIndicator(txtFeltIndicator, it.response.temperature.celsius)
                    }
                }
            }
        }
    }

    private fun getShape(color: Int): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setStroke(50, color)
        return shape
    }

    private fun drawFeltIndicator(txtFeltIndicator: TextView, temperatureCelsius: Double) {
        var color = getColor(R.color.tmp_unknow)
        var text = getString(R.string.temperature_felt_unknow)
        if (temperatureCelsius > 28 || temperatureCelsius < 13) { /// State: Insupportable
            color = getColor(R.color.tmp_unbearable)
            text = getString(R.string.temperature_felt_unbearable)
        } else if (temperatureCelsius <= 28 && temperatureCelsius > 24) { /// State: Trop chaud
            color = getColor(R.color.tmp_too_hot)
            text = getString(R.string.temperature_felt_too_hot)
        } else if (temperatureCelsius <= 24 && temperatureCelsius > 22) { /// State: Chaud
            color = getColor(R.color.tmp_hot)
            text = getString(R.string.temperature_felt_hot)
        } else if (temperatureCelsius in 20.0..22.0) { /// State: Bon
            color = getColor(R.color.tmp_good)
            text = getString(R.string.temperature_felt_good)
        } else if (temperatureCelsius < 20 && temperatureCelsius > 17) { /// State: Frais
            color = getColor(R.color.tmp_cold)
            text = getString(R.string.temperature_felt_cold)
        } else if (temperatureCelsius <= 17 && temperatureCelsius > 13) { /// State: Trop froid
            color = getColor(R.color.tmp_too_cold)
            text = getString(R.string.temperature_felt_too_cold)
        }
        txtFeltIndicator.setBackgroundColor(color);
        txtFeltIndicator.text = text;
    }

    private fun handleTemperatureIndicator(){
        val index = stringsTemperature.indexOf(currentStringTemperature)
        currentStringTemperature = if(stringsTemperature.indexOf(currentStringTemperature) == stringsTemperature.size - 1) {
            stringsTemperature[0]
        } else {
            stringsTemperature[index + 1]
        }

    }

    private fun getTextTemperature(temperature: TemperatureIndicator): Double {
        return if(currentStringTemperature == R.string.temperature_kelvin) {
            temperature.kelvin
        } else if(currentStringTemperature == R.string.temperature_celsius) {
            temperature.celsius
        } else {
            temperature.fahrenheit
        }
    }

}

