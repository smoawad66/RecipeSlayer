package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.databinding.FragmentFavouriteBinding
import com.example.recipeslayer.databinding.FragmentRecipeDetailBinding
import com.example.recipeslayer.ui.recipe.FavouriteViewModel
import com.example.recipeslayer.ui.recipe.RecipeAdapter
import com.example.recipeslayer.utils.Auth

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecipeAdapter(emptyList())
        binding.rvFavourite.adapter = adapter

        favouriteViewModel.getFavourites(Auth.id())

        favouriteViewModel.favouriteRecipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes != null) {
                adapter.setData(recipes)
                binding.rvFavourite.adapter = adapter
            } else {
                Log.i("null", "Its null!")
            }
        }

        adapter.setOnItemClickListener{ position ->
            val recipe = adapter.getData()[position]
            val action = FavouriteFragmentDirections.actionFavoriteFragmentToRecipeDetailFragment(recipe)
            findNavController().navigate(action)
        }
    }


}