package com.example.trackmate

import android.app.Application

class TrackMateApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPref.init(this)
    }
}