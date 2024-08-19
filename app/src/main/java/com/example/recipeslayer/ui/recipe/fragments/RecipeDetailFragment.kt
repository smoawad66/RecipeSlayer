package com.example.recipeslayer.ui.recipe.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentRecipeDetailBinding
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.RecipeResponse
import com.example.recipeslayer.remote.ApiService
import com.example.recipeslayer.repo.Repo
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeDetailFragment : Fragment() {
    private lateinit var binding: FragmentRecipeDetailBinding

    private val args: RecipeDetailFragmentArgs by navArgs()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val repo = Repo()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use coroutine to make network request
        lifecycleScope.launch {
            try {
                val recipeResponse = repo.getRecipeById(args.recipe.idMeal)
                val recipe: Recipe? = recipeResponse

                if (recipe != null) {
                    // Update UI with recipe details
                    binding.title.text = recipe.strMeal
                    binding.instructionsBreif.text = recipe.strInstructions?.substringBefore(".") + "."
                    binding.instructionsComplete.text = recipe.strInstructions?.substringAfter(".")

                    // WebView setup
                    val webView = binding.webview
                    webView.webViewClient = WebViewClient()
                    val webSettings: WebSettings = webView.settings
                    webSettings.javaScriptEnabled = true
                    recipe.strYoutube?.let { webView.loadUrl(it) }

                    // Handle more details click
                    binding.moreDetails.setOnClickListener {
                        if (binding.instructionsComplete.visibility == View.VISIBLE) {
                            binding.instructionsComplete.visibility = View.GONE
                            binding.moreDetailsBtn.text = "show more"
                            binding.moreDetailsIconBtn.setImageResource(R.drawable.more_btn_icon)
                        } else {
                            binding.instructionsComplete.visibility = View.VISIBLE
                            binding.moreDetailsBtn.text = "show less"
                            binding.moreDetailsIconBtn.setImageResource(R.drawable.less_btn_icon)
                        }
                    }

                    // Favorite button logic
                    var isFav = false
                    binding.favBtn.setOnClickListener {
                        if (!isFav) {
                            binding.favBtn.setImageResource(R.drawable.faved_btn_icon)
                            isFav = true
                        } else {
                            binding.favBtn.setImageResource(R.drawable.fav_btn_icon)
                            isFav = false
                        }
                    }

                    Log.i("data", "onViewCreated: ${recipe.toString()}")

                    // Load image
//                    Glide.with(this)
//                        .load(recipe.strMealThumb)
//                        .error(R.drawable.baseline_error_24)
//                        .into(binding.thumbnail)

                } else {
                    // Handle the case where the recipe is not found
                    Log.e("RecipeDetailFragment", "Recipe not found")
                }
            } catch (e: Exception) {
                // Handle network errors
                e.printStackTrace()
                Log.e("RecipeDetailFragment", "Network request failed", e)
            }
        }
    }
}
