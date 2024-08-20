package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
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

        // Initially select the "All" button
//        setSelectedButton(binding.all)

        recipeViewModel.getAllRecipes()
//
//        binding.all.setOnClickListener {
//            setSelectedButton(binding.all)
////            recipeViewModel.setCategories(listOf("Dessert", "Beef", "Breakfast", "Seafood", "Vegetarian", "Chicken", "Lamb", "Miscellaneous", "Pasta", "Side", "Starter", "Vegan", "Goat"))
//            recipeViewModel.getAllRecipes()
//        }
//
//        binding.vegetarian.setOnClickListener {
//            setSelectedButton(binding.vegetarian)
////            recipeViewModel.setCategories(listOf("Vegetarian"))
//            recipeViewModel.getAllRecipes()
//        }
//
//        binding.beef.setOnClickListener {
//            setSelectedButton(binding.beef)
////            recipeViewModel.setCategories(listOf("Beef"))
//            recipeViewModel.getAllRecipes()
//        }
//
//        binding.chicken.setOnClickListener {
//            setSelectedButton(binding.chicken)
////            recipeViewModel.setCategories(listOf("Chicken"))
//            recipeViewModel.getAllRecipes()
//        }

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes != null) {
                adapter.setData(recipes)
                binding.rv.adapter = adapter
            } else {
                Log.i("null", "Its null!")
            }
        }

        adapter.setOnItemClickListener{
            val recipe = adapter.getData()[it]
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipe)
            findNavController().navigate(action)
        }
    }

//    private fun setSelectedButton(selectedButton: Button) {
//        // List of all buttons
//        val buttons = listOf(
//            binding.all,
//            binding.vegetarian,
//            binding.beef,
//            binding.chicken
//            // Add more buttons here if needed
//        )
//
//        for (button in buttons) {
//            if (button == selectedButton) {
//                button.setBackgroundColor(resources.getColor(R.color.button_selected_background))
//                button.setTextColor(resources.getColor(R.color.button_selected_text))
//            } else {
//                button.setBackgroundColor(resources.getColor(R.color.button_unselected_background))
//                button.setTextColor(resources.getColor(R.color.button_unselected_text))
//            }
//        }
//    }
}
