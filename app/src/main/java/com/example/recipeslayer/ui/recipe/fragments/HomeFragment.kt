package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.ui.recipe.adapters.FilterAdapter
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter
import com.example.recipeslayer.utils.Constants.CATEGORIES

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel: RecipeViewModel by viewModels()
    private var currentPosition = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("POSITION", currentPosition)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("POSITION", 0)
        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterAdapter = FilterAdapter()

        filterAdapter.setSelectedPosition(currentPosition)
        binding.rvFilter.adapter = filterAdapter

        filterAdapter.setOnItemClickListener{ position ->
            val category = filterAdapter.getData()[position]
            recipeViewModel.getRecipes(category)
            currentPosition = position
        }

        val recipeAdapter = RecipeAdapter(emptyList())
        binding.rvRecipes.adapter = recipeAdapter

        recipeViewModel.getRecipes(filterAdapter.getData()[currentPosition])
        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.setData(recipes)
            binding.rvRecipes.adapter = recipeAdapter
        }

        recipeAdapter.setOnItemClickListener{
            val recipe = recipeAdapter.getData()[it]
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(null, recipe)
            findNavController().navigate(action)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { requireActivity().moveTaskToBack(true) }
    }
}