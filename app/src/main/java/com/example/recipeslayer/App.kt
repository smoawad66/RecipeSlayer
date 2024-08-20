package com.example.recipeslayer

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.net.ConnectivityManagerCompat
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Converters
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        RecipeRoomDatabase.initRoom(applicationContext)
        Auth.sharedPreferences = getSharedPreferences("Flags", MODE_PRIVATE)

    }
}