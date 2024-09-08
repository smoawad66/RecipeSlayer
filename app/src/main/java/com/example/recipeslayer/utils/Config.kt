package com.example.recipeslayer.utils

object Config {

    fun isArabic(): Boolean {
        return Auth.sharedPreferences.getString("language", "en") == "ar"
    }
}