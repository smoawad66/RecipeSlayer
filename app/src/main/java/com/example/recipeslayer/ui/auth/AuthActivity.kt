package com.example.recipeslayer.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.recipeslayer.R
import com.example.recipeslayer.utils.Auth

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
    }
}
