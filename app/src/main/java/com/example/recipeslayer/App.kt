package com.example.recipeslayer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.LanguageContextWrapper
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
        val context = LanguageContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }
}