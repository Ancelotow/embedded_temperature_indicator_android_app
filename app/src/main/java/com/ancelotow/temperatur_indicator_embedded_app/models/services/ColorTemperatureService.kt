package com.ancelotow.temperatur_indicator_embedded_app.models.services

import android.graphics.Color

private const val LIMIT_HOT = 28.0
private const val HOT = 25.0
private const val GOOD = 21.0
private const val COLD = 18.0
private const val LIMIT_COLD = 13.0

class ColorTemperatureService(
    private val temperature: Double
) {


    fun getColor(): Int {
        return Color.rgb(getRed(), getGreen(), getBlue())
    }

    private fun getBlue(): Int {
        return if(temperature <= COLD) {
            255
        } else if(temperature < GOOD){
            val deltaConst = GOOD - COLD
            val deltaTemp = GOOD - temperature
            ((deltaTemp * 255) / deltaConst).toInt()
        } else {
            0
        }
    }

    private fun getRed(): Int {
        return if(temperature >= HOT) {
            255
        } else if(temperature > GOOD){
            val deltaConst = HOT - GOOD
            val deltaTemp = temperature - GOOD
            ((deltaTemp * 255) / deltaConst).toInt()
        } else {
            0
        }
    }

    private fun getGreen(): Int {
        return if(temperature in COLD..HOT) {
            255
        } else if(temperature < COLD){
            val deltaConst = COLD - LIMIT_COLD
            val deltaTemp = COLD - temperature
            ((deltaTemp * 255) / deltaConst).toInt()
        } else if(temperature > HOT){
            val deltaConst = LIMIT_HOT - HOT
            val deltaTemp = temperature - HOT
            255 - ((deltaTemp * 255) / deltaConst).toInt()
        } else {
            0
        }
    }

}