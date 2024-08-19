package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.recipeslayer.databinding.FragmentRecipeDetailBinding
import com.example.recipeslayer.ui.recipe.FavouriteViewModel

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





    }


}