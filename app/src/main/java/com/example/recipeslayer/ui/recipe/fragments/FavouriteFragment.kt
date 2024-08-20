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
import com.example.recipeslayer.ui.recipe.FavouriteViewModel
import com.example.recipeslayer.ui.recipe.adapters.FavouriteAdapter
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

        val adapter = FavouriteAdapter(emptyList())
        binding.rvFavourite.adapter = adapter

        favouriteViewModel.getFavourites(Auth.id())
        favouriteViewModel.favourites.observe(viewLifecycleOwner) { favourites ->
            adapter.setData(favourites ?: listOf())
            binding.rvFavourite.adapter = adapter

            if (favourites.isNullOrEmpty()) {
                binding.favFill.visibility = View.VISIBLE
            } else {
                binding.favFill.visibility = View.GONE
            }
        }

        adapter.setOnItemClickListener{ position ->
            val favourite = adapter.getData()[position]
            val action = FavouriteFragmentDirections.actionFavoriteFragmentToRecipeDetailFragment(favourite, favourite.recipe)
            findNavController().navigate(action)
        }
    }


}