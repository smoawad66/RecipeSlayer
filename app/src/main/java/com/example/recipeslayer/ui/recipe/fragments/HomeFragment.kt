package com.example.recipeslayer.ui.recipe.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.SCROLL_AXIS_HORIZONTAL
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.recipeslayer.utils.AutoSpanCount.setupRecyclerView
import com.example.recipeslayer.utils.Toast.toast
import java.net.SocketTimeoutException

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val recommendViewModel: RecommendViewModel by viewModels()
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recommendAdapter: RecipeAdapter
    private var categoryPosition = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("category_position", categoryPosition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            categoryPosition = savedInstanceState.getInt("category_position", 0)
        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recommendRecipes()
        binding.internetErrorOverlay.tvTryAgain.setOnClickListener { filterRecipes(categoryPosition) }
        binding.fabUp.setOnClickListener { binding.rvRecipes.smoothScrollToPosition(0) }
        handleUpFab()

        filterAdapter = FilterAdapter()
        binding.rvFilter.adapter = filterAdapter
        filterAdapter.setSelectedPosition(categoryPosition)
        filterAdapter.setOnItemClickListener { filterRecipes(it) }

        setupRecyclerView(binding.rvRecipes, 180)

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

            if (categoryPosition == 0) {
                try {
                    recipeViewModel.getAllRecipes()
                } catch (e: SocketTimeoutException) {
                    internetError(VISIBLE)
                }
            }

            loading(GONE)
        }

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.setData(recipes)
            if (recipeAdapter.itemCount > 0) {
                loading(GONE)
            }
            recipeAdapter.notifyDataSetChanged()
        }


        binding.btnIdea.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToIdeasFragment()
            findNavController().navigate(action)
        }

        binding.btnImageLabeling.setOnClickListener {
            toast(requireContext(), getString(R.string.this_feature_is_coming_soon))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(
                true
            )
        }
    }


    private fun filterRecipes(position: Int) {
        val category = filterAdapter.getData()[position]

        categoryPosition = position
        recipeAdapter.setData(listOf())
        binding.rvRecipes.adapter = recipeAdapter

        loading(VISIBLE)
        internetError(GONE)
        lifecycleScope.launch {
            if (!isInternetAvailable() && !Cache.isFound(category)) {
                internetError(VISIBLE)
                return@launch
            }
            try {
                if (position == 0)
                    recipeViewModel.getAllRecipes()
                else
                    recipeViewModel.filterRecipes(category)
            } catch (e: SocketTimeoutException) {
                internetError(VISIBLE)
            } finally {
                loading(GONE)
            }
        }
    }

    private fun recommendRecipes() {
        recommendAdapter = RecipeAdapter(emptyList())
        val rv = binding.rvRecommend
        rv.adapter = recommendAdapter

        recommendViewModel.recommendRecipes()

        recommendViewModel.recipes.observe(viewLifecycleOwner) {
            recommendAdapter.setData(it.shuffled())
            rv.adapter = recommendAdapter
            if (it.isNotEmpty()) {
                binding.tvRecommend.text = getString(R.string.recommended_recipes)
                rv.visibility = VISIBLE
                PagerSnapHelper().attachToRecyclerView(rv)
                autoScrollRecyclerView(rv, recommendAdapter.itemCount)

            } else {
                binding.tvRecommend.text = getString(R.string.no_recommendation_yet)
            }
        }

        recommendAdapter.setOnItemClickListener {
            val recipe = recommendAdapter.getData()[it]
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
            layoutParams =
                params.apply { topMargin = (90 * context.resources.displayMetrics.density).toInt() }
        }
    }

    private fun internetError(flag: Int) {
        binding.internetErrorOverlay.all.visibility = flag
    }

    private fun handleUpFab() {
        binding.rvRecipes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                binding.fabUp.visibility = if (position > 50) VISIBLE else GONE
            }
        })
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupRecyclerView(binding.rvRecipes, 180)
    }

}