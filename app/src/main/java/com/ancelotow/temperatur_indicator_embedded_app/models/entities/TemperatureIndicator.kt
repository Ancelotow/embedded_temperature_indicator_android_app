package com.ancelotow.temperatur_indicator_embedded_app.models.entities

import com.google.gson.annotations.SerializedName

class TemperatureIndicator(

    @SerializedName("celsius")
    val celsius: Double,

    @SerializedName("kelvin")
    val kelvin: Double,

    @SerializedName("farheineit")
    val fahrenheit: Double,

);