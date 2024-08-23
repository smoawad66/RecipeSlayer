package com.example.recipeslayer.utils

import android.content.SharedPreferences
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Auth {
    lateinit var sharedPreferences: SharedPreferences

    fun id() = sharedPreferences.getLong("userId", -1)

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