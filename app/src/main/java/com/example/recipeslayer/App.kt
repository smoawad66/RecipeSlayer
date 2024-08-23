package com.example.recipeslayer

import android.app.Application
import android.util.Log
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        RecipeRoomDatabase.initRoom(applicationContext)
        Auth.sharedPreferences = getSharedPreferences("Flags", MODE_PRIVATE)
    }
}