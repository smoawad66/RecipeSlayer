package com.example.recipeslayer.ui.recipe.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentAboutBinding
import com.example.recipeslayer.utils.Config.isArabic
import com.example.recipeslayer.utils.Constants.ELSAYED_LINK
import com.example.recipeslayer.utils.Constants.HABSA_LINK
import com.example.recipeslayer.utils.Constants.YASMEEN_LINK
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import kotlinx.coroutines.launch
import com.example.recipeslayer.utils.Toast.toast

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            ivElsayed.setOnClickListener { goToUrl(ELSAYED_LINK) }
            iv7absa.setOnClickListener { goToUrl(HABSA_LINK) }
            ivYasmeen.setOnClickListener { goToUrl(YASMEEN_LINK) }
        }
    }

    private fun goToUrl(url: String) {
        lifecycleScope.launch {
            if (!isInternetAvailable()) {
                val c = if (isArabic()) 24 else 30
                toast(requireContext(), getString(R.string.check_your_internet_connection).substring(0, c) + '!')
                return@launch
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
