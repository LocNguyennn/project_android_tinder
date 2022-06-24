package com.example.chattingapp

import android.app.Application
import com.example.chattingapp.sharePreference.MySharedPreferences

class ChattingApp : Application() {
    lateinit var prefs: MySharedPreferences
    override fun onCreate() {
        super.onCreate()
        prefs = MySharedPreferences()
        prefs.init(this)
    }
}