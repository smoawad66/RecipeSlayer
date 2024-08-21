package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter
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
        setSelectedButton(binding.all,binding.allcard)

        recipeViewModel.setCategories(listOf("Dessert", "Beef", "Breakfast", "Seafood", "Vegetarian", "Chicken", "Lamb", "Miscellaneous", "Pasta", "Side", "Starter", "Vegan", "Goat"))
        recipeViewModel.getCat()
        binding.allcard.setOnClickListener {
            setSelectedButton(binding.all,binding.allcard)
            recipeViewModel.setCategories(listOf("Dessert", "Beef", "Breakfast", "Seafood", "Vegetarian", "Chicken", "Lamb", "Miscellaneous", "Pasta", "Side", "Starter", "Vegan", "Goat"))
            recipeViewModel.getCat()
//          recipeViewModel.getAllRecipes()
        }

        binding.Vegetariancard.setOnClickListener {
            setSelectedButton(binding.Vegetarian,binding.Vegetariancard)
            recipeViewModel.setCategories(listOf("Vegetarian"))
            recipeViewModel.getCat()
//            recipeViewModel.getAllRecipes()
        }

        binding.Beefcard.setOnClickListener {
            setSelectedButton(binding.Beef,binding.Beefcard)
            recipeViewModel.setCategories(listOf("Beef"))
            recipeViewModel.getCat()
//            recipeViewModel.getAllRecipes()
        }
        binding.dessertcard.setOnClickListener{
            setSelectedButton(binding.dessert,binding.dessertcard)
            recipeViewModel.setCategories(listOf("Dessert"))
            recipeViewModel.getCat()
//            recipeViewModel.getAllRecipes()
        }
        //1
        binding.chicken.setOnClickListener{
            setSelectedButton(binding.chicken,binding.Chickencard)
            recipeViewModel.setCategories(listOf("Chicken"))
            recipeViewModel.getCat()
        }
        //2

        binding.Breakfast.setOnClickListener {
            setSelectedButton(binding.Breakfast,binding.Breakfastcard)
            recipeViewModel.setCategories(listOf("Breakfast"))
            recipeViewModel.getCat()
        }
        //3

        binding.Seafood.setOnClickListener {
            setSelectedButton(binding.Seafood,binding.Seafoodcard)
            recipeViewModel.setCategories(listOf("Seafood"))
            recipeViewModel.getCat()
        }
        //4
        binding.Lamb.setOnClickListener {
            setSelectedButton(binding.Lamb,binding.Lambcard)
            recipeViewModel.setCategories(listOf("Lamb"))
            recipeViewModel.getCat()
        }
        //5
        binding.Miscellaneous.setOnClickListener {
            setSelectedButton(binding.Miscellaneous,binding.Miscellaneouscard)
            recipeViewModel.setCategories(listOf("Miscellaneous"))
            recipeViewModel.getCat()
        }
        //6
        binding.Pasta.setOnClickListener {
            setSelectedButton(binding.Pasta,binding.Pastacard)
            recipeViewModel.setCategories(listOf("Pasta"))
            recipeViewModel.getCat()
        }
        //7
        binding.Side.setOnClickListener {
            setSelectedButton(binding.Side,binding.Sidecard)
            recipeViewModel.setCategories(listOf("Side"))
            recipeViewModel.getCat()
        }
        //8
        binding.Starter.setOnClickListener {
            setSelectedButton(binding.Starter,binding.Startercard)
            recipeViewModel.setCategories(listOf("Starter"))
            recipeViewModel.getCat()
        }
        //9
        binding.Vegan.setOnClickListener {
            setSelectedButton(binding.Vegan,binding.Vegancard)
            recipeViewModel.setCategories(listOf("Vegan"))
            recipeViewModel.getCat()
        }
        //10
        binding.Goat.setOnClickListener {
            setSelectedButton(binding.Goat,binding.Goatcard)
            recipeViewModel.setCategories(listOf("Goat"))
            recipeViewModel.getCat()
        }

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
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipe.idMeal)
            findNavController().navigate(action)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { requireActivity().moveTaskToBack(true) }
    }

    private fun setSelectedButton(selectedButton: TextView, lakds:CardView) {
        // List of all buttons
        // Dessert", "Beef", "Breakfast", "Seafood", "Vegetarian", "Chicken", "Lamb", "Miscellaneous", "Pasta", "Side", "Starter", "Vegan", "Goat"
        val buttons = listOf(
            binding.all,
            binding.Vegetarian,
            binding.Beef,
            binding.chicken,
            binding.dessert,
            binding.Breakfast,
            binding.Seafood,
            binding.Lamb,
            binding.Miscellaneous,
            binding.Pasta,
            binding.Side,
            binding.Starter,
            binding.Vegan,
            binding.Goat,

        )

        for (button in buttons) {
            if (button == selectedButton) {
                button.setBackgroundColor(resources.getColor(R.color.button_selected_background))
                button.setTextColor(resources.getColor(R.color.button_selected_text))
            } else {
                button.setBackgroundColor(resources.getColor(R.color.button_unselected_background))
                button.setTextColor(resources.getColor(R.color.button_unselected_text))
            }
        }
    }
}