package com.example.recipeslayer

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.recipeslayer.local.RecipeRoomDatabase
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.ContextWrapper
import kotlinx.coroutines.delay
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
        val languageCode = Auth.sharedPreferences.getString("language", "en")
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    override fun attachBaseContext(newBase: Context) {
        val newLocale = AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault()
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

    object AppUtils {
        fun restartApplication(context: Context) {
            val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            restartIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            restartIntent?.putExtra("splashTime", 0L)

            Handler(Looper.getMainLooper()).post {
                context.startActivity(restartIntent)
                Runtime.getRuntime().exit(0)
            }
        }
    }

}