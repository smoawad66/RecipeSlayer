package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.R
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.RecipeAdapter
import com.example.recipeslayer.ui.recipe.RecipeViewModel

class SearchFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the ViewModel
        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.searc_rv)
        recipeAdapter = RecipeAdapter(emptyList())
        recyclerView.adapter = recipeAdapter

        // Find the SearchView
        val searchView = view.findViewById<SearchView>(R.id.search_view)

        // Set up SearchView query listener for live search results
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Search for recipes by name
                    searchRecipesByName(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchRecipesByName(it)
                    } else {
                        clearSearchResults()
                    }
                }
                return true
            }
        })
    }

    private fun searchRecipesByName(name: String) {
        // Fetch recipes from the ViewModel and update the adapter
        recipeViewModel.searchByName(name).observe(viewLifecycleOwner, { recipes ->
            updateRecyclerView(recipes)
        })
    }

    private fun updateRecyclerView(recipes: List<Recipe>) {
        // Update the RecyclerView with the search results
        recipeAdapter.setData(recipes)
        recipeAdapter.notifyDataSetChanged()
    }

    private fun clearSearchResults() {
        // Clear the RecyclerView by submitting an empty list to the adapter
        recipeAdapter.setData(emptyList())
        recipeAdapter.notifyDataSetChanged()
    }
}
