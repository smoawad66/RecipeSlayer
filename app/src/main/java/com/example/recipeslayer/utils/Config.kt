package com.example.recipeslayer.utils

object Config {

    fun isArabic(): Boolean {
        return Auth.sharedPreferences.getString("selected_language", "en") == "ar"
    }
}