package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.ui.recipe.RecommendViewModel
import com.example.recipeslayer.ui.recipe.UserViewModel
import com.example.recipeslayer.ui.recipe.adapters.FilterAdapter
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val filterAdapter = FilterAdapter()
        binding.rvFilter.adapter = filterAdapter

        val recipeAdapter = RecipeAdapter(emptyList())
        binding.rvRecipes.adapter = recipeAdapter

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

        recipeAdapter.setOnItemClickListener{
            val recipe = recipeAdapter.getData()[it]
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipe.idMeal)
            findNavController().navigate(action)
        }

        handleRecommendedRecipes()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { requireActivity().moveTaskToBack(true) }
    }

    private fun handleRecommendedRecipes() {

        lifecycleScope.launch {
            val user = withContext(IO) { userViewModel.getUser(5)}
            Log.i("user", "$user")
            return@launch
        }

        val adapter = RecipeAdapter(emptyList())
        val rv = binding.rvRecommend
        rv.adapter = adapter
        recommendViewModel.recommendRecipes()
        recommendViewModel.recipes.observe(viewLifecycleOwner){
            adapter.setData(it)
            rv.adapter = adapter
            if (it.isNotEmpty()) {
                binding.tvRecommend.visibility = VISIBLE
                rv.visibility = VISIBLE
                PagerSnapHelper().attachToRecyclerView(rv)
            }

            Log.i("rec", "${it.size}")
        }

        adapter.setOnItemClickListener{
            val recipe = adapter.getData()[it]
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipe.idMeal)
            findNavController().navigate(action)
        }
    }

}