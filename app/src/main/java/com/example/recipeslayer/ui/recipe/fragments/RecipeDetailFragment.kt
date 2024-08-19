package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentRecipeDetailBinding
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.FavouriteViewModel
import com.example.recipeslayer.utils.Auth
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val args: RecipeDetailFragmentArgs by navArgs()
    private lateinit var recipe: Recipe
    private lateinit var recipeId: String
    private var userId = -1L
    var favouriteId : Long?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipe = args.recipe
        recipeId = recipe.idMeal
        userId = Auth.id()


        lifecycleScope.launch {
            favouriteId = withContext(IO) { favouriteViewModel.getFavouriteId(userId, recipe) }
            if (favouriteId != null) { // Saved
                binding.btnFavourite.text = "Unsave"
            }
        }

        bindRecipeData(view)

        binding.btnFavourite.setOnClickListener { handleFavourite() }

    }

    private fun handleFavourite() {

        lifecycleScope.launch {
            if (favouriteId != null) {
                withContext(IO) { favouriteViewModel.deleteFavourite(Favourite(Auth.id(), recipe, favouriteId!!))  }
                binding.btnFavourite.text = "Save"
                toast("Recipe unsaved.")
                favouriteId = null
            } else {
                val newId = withContext(IO) { favouriteViewModel.insertFavourite(Favourite(Auth.id(), recipe)) }
                binding.btnFavourite.text = "Unsave"
                toast("Recipe saved.")
                favouriteId = newId
            }

        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun bindRecipeData(view: View) {
        binding.apply {
            tvTitle.text = recipe.strMeal
            Glide.with(view)
                .load(recipe.strMealThumb)
                .placeholder(R.drawable.loading)
                .error(R.drawable.baseline_error_24)
                .into(ivThumbnail)
        }
    }

}