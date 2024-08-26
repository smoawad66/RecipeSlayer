package com.example.recipeslayer.ui.recipe.fragments

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.os.LocaleListCompat
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentSettingsBinding
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Config
import com.example.recipeslayer.utils.Config.isArabic

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            if (Auth.sharedPreferences.getString("theme", "dark") == "dark") {
                lightDarkSwitch.toggle()
            }

            lightDarkSwitch.setOnClickListener {
                when (AppCompatDelegate.getDefaultNightMode()) {
                    MODE_NIGHT_NO -> {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                        Auth.sharedPreferences.edit().putString("theme", "dark").apply()
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                        Auth.sharedPreferences.edit().putString("theme", "light").apply()
                    }
                }
            }

            languageRadioGroup.check(if (isArabic()) R.id.arabic else R.id.english)

            languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.english -> setLocale("en")
                    R.id.arabic -> setLocale("ar")
                }
            }
        }
    }


    private fun setLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
        val sharedPref = Auth.sharedPreferences
        with(sharedPref.edit()) {
            putString("selected_language", languageCode)
            apply()
        }
        restart()
    }


    private fun restart() {
        activity?.finish().also {
            requireActivity().startActivity(Intent(activity, RecipeActivity::class.java))
        }

    }
}