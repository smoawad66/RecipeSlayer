package com.example.recipeslayer.utils

import android.content.SharedPreferences

object Auth {
    lateinit var sharedPreferences: SharedPreferences

    fun login(userId: Long) {
        sharedPreferences.edit().apply {
            putBoolean("isLoggedIn", true)
            putLong("userId", userId)
            commit()
        }
    }

    fun logout() {
        sharedPreferences.edit().apply {
            putBoolean("isLoggedIn", false)
            putLong("userId", -1)
            commit()
        }
    }
}