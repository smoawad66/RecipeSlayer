package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.databinding.FragmentFavouriteBinding
import com.example.recipeslayer.ui.recipe.viewModels.FavouriteViewModel
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter
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

        loading(VISIBLE)
        favouriteViewModel.getFavouriteRecipes(Auth.id())
        favouriteViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.setData(recipes ?: listOf())
            binding.rvFavourite.adapter = adapter
            loading(GONE)
            binding.favFill.visibility = if (recipes.isEmpty()) VISIBLE else GONE
        }

        adapter.setOnItemClickListener{ position ->
            val recipe = adapter.getData()[position]
            val action = FavouriteFragmentDirections.actionFavoriteFragmentToRecipeDetailFragment(recipe.idMeal)
            findNavController().navigate(action)
        }
    }

    private fun loading(flag: Int) {
        binding.loadingOverlay.all.visibility = flag
    }
}