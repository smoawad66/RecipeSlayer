package com.example.recipeslayer

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Constants.CATEGORIES
import com.example.recipeslayer.utils.ContextWrapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class App : Application() {

    override fun onCreate() {
        RecipeRoomDatabase.initRoom(applicationContext)
        Auth.sharedPreferences = getSharedPreferences("Flags", MODE_PRIVATE)

        if (Auth.sharedPreferences.getString("theme", "dark") == "dark"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate()

        loadSavedLanguage()

    }


    private fun loadSavedLanguage() {
        val languageCode = Auth.sharedPreferences.getString("selected_language", "en")
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    override fun attachBaseContext(newBase: Context) {
        val newLocale = AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault()
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }
}