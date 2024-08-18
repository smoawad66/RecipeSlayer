package com.example.recipeslayer.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeslayer.R
import com.example.recipeslayer.utils.Auth

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

//        Log.i("user", "${Auth.sharedPreferences.getLong("userId", -1)}")
    }
}
