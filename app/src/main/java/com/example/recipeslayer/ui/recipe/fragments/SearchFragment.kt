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
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private val recipeViewModel: RecipeViewModel by viewModels()
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
        recyclerView = binding.searchRv

        binding.searchView.setOnQueryTextListener(this)

        val adapter = RecipeAdapter(emptyList())
        recyclerView.adapter = adapter

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.setData(recipes.filter { it.strCategory != "Pork" })
            recyclerView.adapter = adapter

            binding.searchFill.visibility = if (recipes.isEmpty()) VISIBLE else GONE
        }


        adapter.setOnItemClickListener { position ->
            val recipe = adapter.getData()[position]
            val action = SearchFragmentDirections.actionSearchFragmentToRecipeDetailFragment(recipe.idMeal)
            findNavController().navigate(action)
        }

    }

    override fun onQueryTextSubmit(query: String): Boolean {

        lifecycleScope.launch {
            if (!isInternetAvailable()){
                noInternetOverlay(VISIBLE)
                return@launch
            }
            noInternetOverlay(GONE)
            loadingOverlay(VISIBLE)
            recipeViewModel.searchByName(query)
            loadingOverlay(GONE)
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        lifecycleScope.launch {
            if (!isInternetAvailable()){
                noInternetOverlay(VISIBLE)
                return@launch
            }
            noInternetOverlay(GONE)
            loadingOverlay(VISIBLE)
            recipeViewModel.searchByName(newText)
            loadingOverlay(GONE)
        }
        return true
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
        binding.searchFill.visibility = GONE
    }
}
