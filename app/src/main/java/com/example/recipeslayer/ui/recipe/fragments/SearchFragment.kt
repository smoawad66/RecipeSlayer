package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.R
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.RecipeAdapter
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class SearchFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchFill: ImageView

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

        searchFill = view.findViewById(R.id.search_fill)

        // Find the SearchView
        val searchView = view.findViewById<SearchView>(R.id.search_view)

        // Set up SearchView query listener for live search results
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchRecipesByName("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                var newTranslatedText = ""
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ARABIC)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build()

                val englishArabicTranslator = Translation.getClient(options)

                if(Locale.getDefault().language == "ar") {
                    englishArabicTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            if (newText != null) {
                                englishArabicTranslator.translate(newText)
                                    .addOnSuccessListener { translatedText ->
                                        run {
                                            if (translatedText.isNotEmpty())
                                                searchRecipesByName("%$translatedText%")
                                            else clearSearchResults()
                                        }
                                        Log.i(
                                            "lol",
                                            "onQueryTextChange: ____________-$translatedText"
                                        )
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.i("lol", "onBindViewHolder: $exception.message")
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.i("lol", "onBindViewHolder: $exception.message")
                        }
                }
                else {
                    if (newText.isNullOrEmpty()) {
                        clearSearchResults()
                    } else {
                        searchRecipesByName("%$newText%")
                    }
                }
                return true
            }
        })

        recipeAdapter.setOnItemClickListener{
            val recipe = recipeAdapter.getData()[it]
            val action = SearchFragmentDirections.actionSearchFragmentToRecipeDetailFragment(recipe)
            findNavController().navigate(action)
        }
    }

    private fun searchRecipesByName(name: String) {
        // Fetch recipes from the ViewModel and update the adapter
        recipeViewModel.searchByName("%$name%").observe(viewLifecycleOwner, { recipes ->
            if (recipes != null && recipes.isNotEmpty()) {
                updateRecyclerView(recipes)
                searchFill.visibility = View.GONE
            } else {
                clearSearchResults() // Clear RecyclerView if no recipes found
            }
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
        searchFill.visibility = View.VISIBLE
    }
}
