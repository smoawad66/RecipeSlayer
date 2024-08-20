package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentSearchBinding
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.ui.recipe.adapters.RecipeAdapter

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

            if (recipes.isEmpty()) {
                binding.searchFill.visibility = View.VISIBLE
            } else {
                binding.searchFill.visibility = View.GONE
            }

        }


        adapter.setOnItemClickListener { position ->
            val recipe = adapter.getData()[position]
            val action = SearchFragmentDirections.actionSearchFragmentToRecipeDetailFragment(null, recipe)
            findNavController().navigate(action)
        }

    }

    override fun onQueryTextSubmit(query: String): Boolean {
        recipeViewModel.searchByName(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        recipeViewModel.searchByName(newText)
        return true
    }
}
