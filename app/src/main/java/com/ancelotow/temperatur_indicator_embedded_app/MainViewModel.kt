package com.ancelotow.temperatur_indicator_embedded_app

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedRepository
import com.ancelotow.temperatur_indicator_embedded_app.models.repositories.BluetoothEmbeddedState
import kotlinx.coroutines.launch

class MainViewModel(private val device: BluetoothDevice): ViewModel() {

    private val _bluetooth = MutableLiveData<BluetoothEmbeddedState>()
    val bluetooth = _bluetooth

    class Factory(private val device: BluetoothDevice) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(device) as T
        }

    }

    init {
        getMineParty()
    }

    private fun getMineParty() {
        viewModelScope.launch {
           BluetoothEmbeddedRepository.getMessage(device).collect {
                _bluetooth.value = it
            }
        }
    }

}