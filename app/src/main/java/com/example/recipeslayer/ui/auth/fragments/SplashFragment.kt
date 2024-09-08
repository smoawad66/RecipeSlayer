package com.example.recipeslayer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.example.recipeslayer.utils.Auth
import kotlin.math.min

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isLoggedIn = Auth.sharedPreferences.getBoolean("isLoggedIn", false)

        val extras = activity?.intent?.extras
        val splashTime = extras?.getLong("splashTime", 2000L) ?: 5000L

        if (isLoggedIn) {
            splash(::navigateToHome, min(splashTime, 2000L))
            return
        }

        splash(::navigateToLogin, splashTime)
    }


    private fun splash(function: () -> Unit, time: Long) {
        Handler(Looper.getMainLooper()).postDelayed({ function() }, time)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), RecipeActivity::class.java)
        startActivity(intent)
    }
}