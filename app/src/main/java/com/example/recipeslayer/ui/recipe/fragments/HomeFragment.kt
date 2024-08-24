package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.ui.recipe.RecommendViewModel
import com.example.recipeslayer.ui.recipe.UserViewModel
import com.example.recipeslayer.ui.recipe.adapters.FilterAdapter
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val recommendViewModel: RecommendViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
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


        val recipeAdapter = RecipeAdapter(emptyList())
        binding.rvRecipes.adapter = recipeAdapter
        recipeAdapter.setOnItemClickListener{
            val recipe = recipeAdapter.getData()[it]
            navigateToDetails(recipe.idMeal)
        }

        val filterAdapter = FilterAdapter()
        binding.rvFilter.adapter = filterAdapter
        filterAdapter.setSelectedPosition(currentPosition)

        recipeViewModel.getRecipes(filterAdapter.getData()[currentPosition])
        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.setData(recipes)

            binding.rvRecipes.adapter = recipeAdapter
        }

        filterAdapter.setOnItemClickListener{ position ->
            val category = filterAdapter.getData()[position]
            recipeViewModel.getRecipes(category)
            currentPosition = position
        }


        binding.btnIdea.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToIdeasFragment()
            findNavController().navigate(action)
        }

        recommendRecipes()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { requireActivity().moveTaskToBack(true) }
    }

    private fun recommendRecipes() {
        val adapter = RecipeAdapter(emptyList())
        val rv = binding.rvRecommend
        rv.adapter = adapter
        recommendViewModel.recommendRecipes()
        recommendViewModel.recipes.observe(viewLifecycleOwner){
            adapter.setData(it.shuffled())
            rv.adapter = adapter
            if (it.isNotEmpty()) {
                binding.tvRecommend.visibility = VISIBLE
                rv.visibility = VISIBLE
                PagerSnapHelper().attachToRecyclerView(rv)
            }
        }

        adapter.setOnItemClickListener{
            val recipe = adapter.getData()[it]
            navigateToDetails(recipe.idMeal)
        }
    }


    private fun navigateToDetails(recipeId: Long) {
        val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipeId)
        findNavController().navigate(action)
    }

}