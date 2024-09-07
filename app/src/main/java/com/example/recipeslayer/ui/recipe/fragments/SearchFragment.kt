package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.databinding.FragmentSearchBinding
import com.example.recipeslayer.ui.recipe.viewModels.RecipeViewModel
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter
import com.example.recipeslayer.utils.AutoSpanCount.setupRecyclerView
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        internetError(GONE)
        loading(GONE)

        binding.searchView.isIconified = false
        binding.searchView.requestFocus()

        binding.internetErrorOverlay.tvTryAgain.setOnClickListener {
            val query = binding.searchView.query.toString()
            onQueryTextSubmit(query)
        }

        recyclerView = binding.rvSearch
        binding.searchView.setOnQueryTextListener(this)
        recipeAdapter = RecipeAdapter(emptyList())
        recyclerView.adapter = recipeAdapter
        setupRecyclerView(binding.rvSearch, 180)

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.setData(recipes.filter { it.strCategory != "Pork" })
            recyclerView.adapter = recipeAdapter
            binding.searchFill.visibility = if (recipes.isEmpty()) VISIBLE else GONE
        }


        recipeAdapter.setOnItemClickListener { position ->
            val recipe = recipeAdapter.getData()[position]
            val action = SearchFragmentDirections.actionSearchFragmentToRecipeDetailFragment(recipe.idMeal)
            findNavController().navigate(action)
        }

    }

    override fun onQueryTextChange(newText: String): Boolean {
        internetError(GONE)
        loading(VISIBLE)
        lifecycleScope.launch {
            if (!isInternetAvailable()) {
                recipeAdapter.setData(listOf())
                binding.rvSearch.adapter = recipeAdapter
                internetError(VISIBLE)
                loading(GONE)
                return@launch
            }

            recipeViewModel.searchByName(newText)
            loading(GONE)
        }
        return true
    }

    override fun onQueryTextSubmit(query: String) = onQueryTextChange(query)

    private fun loading(flag: Int) {
        binding.searchFill.visibility = GONE
        binding.loadingOverlay.all.visibility = flag
    }

    private fun internetError(flag: Int) {
        binding.searchFill.visibility = GONE
        binding.internetErrorOverlay.all.visibility = flag
    }

}
