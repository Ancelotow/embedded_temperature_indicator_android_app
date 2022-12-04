package com.ancelotow.temperatur_indicator_embedded_app.models.entities

import com.google.gson.annotations.SerializedName

class BluetoothResponse(
    @SerializedName("temperature")
    val temperature: TemperatureIndicator,
)