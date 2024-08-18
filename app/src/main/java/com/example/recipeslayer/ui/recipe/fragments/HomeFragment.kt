package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.ui.recipe.RecipeAdapter
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val adapter = RecipeAdapter(emptyList())
        binding.rv.adapter = adapter

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes != null) {
                adapter.setData(recipes)
                binding.rv.adapter = adapter
            } else {
                Log.i("null", "Its null!")
            }
        }

        recipeViewModel.getAllRecipes()

    }
}