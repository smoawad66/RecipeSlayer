package com.example.recipeslayer

import android.app.Application
import android.util.Log
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.utils.Auth
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val gson: Gson = GsonBuilder().serializeNulls().create()
        Log.i("jjj", "onCreate: ${gson.toJson(null)}")


        RecipeRoomDatabase.initRoom(applicationContext)
        Auth.sharedPreferences = getSharedPreferences("Flags", MODE_PRIVATE)
    }
}