package com.example.recipeslayer

import android.app.Application
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.utils.Auth

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        RecipeRoomDatabase.initRoom(applicationContext)
        Auth.sharedPreferences = getSharedPreferences("Flags", MODE_PRIVATE)
    }
}