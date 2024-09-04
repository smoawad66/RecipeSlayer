package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
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
import com.example.recipeslayer.utils.Cache
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val recommendViewModel: RecommendViewModel by viewModels()
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var recipeAdapter: RecipeAdapter
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

        recommendRecipes()
        binding.internetErrorOverlay.tryAgain.setOnClickListener { filterRecipes(currentPosition) }

        filterAdapter = FilterAdapter()
        binding.rvFilter.adapter = filterAdapter
        filterAdapter.setSelectedPosition(currentPosition)
        filterAdapter.setOnItemClickListener { filterRecipes(it) }

        recipeAdapter = RecipeAdapter()
        binding.rvRecipes.adapter = recipeAdapter
        recipeAdapter.setOnItemClickListener {
            val recipe = recipeAdapter.getData()[it]
            navigateToDetails(recipe.idMeal)
        }


        loading(VISIBLE)
        internetError(GONE)
        lifecycleScope.launch {
            if (!isInternetAvailable() && !Cache.isFound(getString(R.string.all))) {
                recipeAdapter.setData(listOf())
                binding.rvRecipes.adapter = recipeAdapter
                internetError(VISIBLE)
                loading(GONE)
                return@launch
            }
            withContext(IO) { recipeViewModel.getAllRecipes() }

            loading(GONE)
        }

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.setData(recipes)
            binding.rvRecipes.adapter = recipeAdapter
        }


        binding.btnIdea.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToIdeasFragment()
            findNavController().navigate(action)
        }

        binding.btnImageLabeling.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                "This feature is coming soon!",
                Toast.LENGTH_SHORT
            ).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(
                true
            )
        }
    }


    private fun filterRecipes(position: Int) {
        val category = filterAdapter.getData()[position]

        currentPosition = position
        recipeAdapter.setData(listOf())
        binding.rvRecipes.adapter = recipeAdapter

        loading(VISIBLE)
        internetError(GONE)
        lifecycleScope.launch {
            if (!isInternetAvailable() && !Cache.isFound(category)) {
                internetError(VISIBLE)
                return@launch
            }
            recipeViewModel.filterRecipes(category)
            loading(GONE)
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

    private fun loading(flag: Int) {
        binding.loadingOverlay.all.visibility = flag
        binding.loadingOverlay.progressBar.apply {
            val params = layoutParams as ViewGroup.MarginLayoutParams
            layoutParams = params.apply { topMargin = (100 * context.resources.displayMetrics.density).toInt() }
        }
    }

    private fun internetError(flag: Int) {
        binding.internetErrorOverlay.all.visibility = flag
    }
}