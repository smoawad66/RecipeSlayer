package com.example.recipeslayer.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeslayer.databinding.ActivityRecipeBinding
import com.example.recipeslayer.ui.auth.AuthActivity
import com.example.recipeslayer.utils.Auth

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Log.i("user", "${Auth.sharedPreferences.getLong("userId", -1)}")

        onBackPressedDispatcher.addCallback(this) { moveTaskToBack(true) }

        binding.logout.setOnClickListener {
            Auth.logout()
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("splashTime", 0L)
            startActivity(intent)
            finish()
        }
    }
}