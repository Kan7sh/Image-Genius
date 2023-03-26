package com.main.mainproject

import java.util.*
import  android.text.format.DateFormat
object Methods {

    fun formatTimestamp(timestamp: Long):String{

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp

        return   DateFormat.format("dd/MM/yyyy",calendar).toString()

    }

}