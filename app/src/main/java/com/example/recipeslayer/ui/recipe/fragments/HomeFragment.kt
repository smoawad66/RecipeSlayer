package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.ui.recipe.viewModels.RecipeViewModel
import com.example.recipeslayer.ui.recipe.viewModels.RecommendViewModel
import com.example.recipeslayer.ui.recipe.adapters.FilterAdapter
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE
import com.example.recipeslayer.utils.Internet
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val recommendViewModel: RecommendViewModel by viewModels()
    private var currentPosition = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("POSITION", currentPosition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("POSITION", 0)
        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recommendRecipes()
        val filterAdapter = FilterAdapter()
        binding.rvFilter.adapter = filterAdapter
        val recipeAdapter = RecipeAdapter(emptyList())
        binding.rvRecipes.adapter = recipeAdapter

        filterAdapter.setSelectedPosition(currentPosition)

        filterAdapter.setOnItemClickListener { position ->
            val category = filterAdapter.getData()[position]
            currentPosition = position

            lifecycleScope.launch {
                if (!isInternetAvailable()) {
                    recipeAdapter.setData(listOf())
                    binding.rvRecipes.adapter = recipeAdapter
                    noInternetOverlay(VISIBLE)
                    return@launch
                }
                noInternetOverlay(GONE)
                recipeViewModel.filterRecipes(category)
            }
        }

        loadingOverlay(VISIBLE)
        lifecycleScope.launch {

            if (!isInternetAvailable()) {
                recipeAdapter.setData(listOf())
                binding.rvRecipes.adapter = recipeAdapter
                noInternetOverlay(VISIBLE)

                binding.overlay.noInternet.setOnClickListener {
                    activity?.recreate()
                }
                return@launch
            }


            withContext(IO) { recipeViewModel.getAllRecipes() }
            recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
                recipeAdapter.setData(recipes)
                binding.rvRecipes.adapter = recipeAdapter
            }

            recipeAdapter.setOnItemClickListener {
                val recipe = recipeAdapter.getData()[it]
                navigateToDetails(recipe.idMeal)
            }

            loadingOverlay(GONE)
        }

        binding.btnIdea.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToIdeasFragment()
            findNavController().navigate(action)
        }

        binding.btnImageLabeling.setOnClickListener { toast("This feature is coming soon!!") }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(
                true
            )
        }
    }

    private fun recommendRecipes() {
        val adapter = RecipeAdapter(emptyList())
        val rv = binding.rvRecommend
        rv.adapter = adapter

        recommendViewModel.recommendRecipes()

        recommendViewModel.recipes.observe(viewLifecycleOwner) {
            adapter.setData(it.shuffled())
            rv.adapter = adapter
            if (it.isNotEmpty()) {
                binding.tvRecommend.visibility = VISIBLE
                binding.tvRecommend.text = getString(R.string.recommended_recipes)
                rv.visibility = VISIBLE
                PagerSnapHelper().attachToRecyclerView(rv)
                autoScrollRecyclerView(rv, adapter.itemCount)

            } else {
                binding.tvRecommend.text = getString(R.string.no_recommendation_yet)
            }
        }

        adapter.setOnItemClickListener {
            val recipe = adapter.getData()[it]
            navigateToDetails(recipe.idMeal)
        }
    }

    private fun autoScrollRecyclerView(recyclerView: RecyclerView, itemCount: Int) {
        var position = 0
        lifecycleScope.launch {
            while (true) {
                delay(4000)
                recyclerView.smoothScrollToPosition(position++)
                if (position == itemCount) position = 0
            }
        }
    }

    private fun navigateToDetails(recipeId: Long) {
        val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipeId)
        findNavController().navigate(action)
    }

    private fun toast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun loadingOverlay(flag: Int) {
        binding.overlay.apply {
            loadingView.visibility = flag
            progressBar.visibility = flag
        }
    }

    private fun noInternetOverlay(flag: Int) {
        binding.overlay.apply {
            noInternet.visibility = flag
            progressBar.visibility = GONE
        }
    }
}
