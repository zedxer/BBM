package com.example.naqi.bebettermuslim.controllers

import com.example.naqi.bebettermuslim.Utils.PrayTimeCalculation
import java.util.*
import kotlin.collections.ArrayList

class HomeApp(private var lat: Double, long: Double, date: Calendar, timeZone: Double) {
    private var prayerTimings: PrayTimeCalculation

    init {
        prayerTimings = PrayTimeCalculation()
    }

    fun providePrayerTiming(date: Calendar, lat: Double, long: Double, timeZone: Double): ArrayList<String> {
        return prayerTimings.getPrayerTimes(date, lat, long, timeZone)
    }

}