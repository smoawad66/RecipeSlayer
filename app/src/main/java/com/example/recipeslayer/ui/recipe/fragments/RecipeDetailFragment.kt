package com.example.recipeslayer.ui.recipe.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import kotlin.reflect.full.memberProperties
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentRecipeDetailBinding
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Ingredient
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.FavouriteViewModel
import com.example.recipeslayer.ui.recipe.adapters.IngredientAdapter
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Constants.BASE_INGREDIENT_URL
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val args: RecipeDetailFragmentArgs by navArgs()
    private var recipe: Recipe? = null
    private var ingredients: MutableList<Ingredient> = mutableListOf()
    private var userId = -1L
    private var recipeId = -1L
    private var isFavourite = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = Auth.id()
        recipeId = args.recipeId

        lifecycleScope.launch(IO) {

            isFavourite = favouriteViewModel.isFavourite(userId, recipeId)

            if (isInternetAvailable(requireContext())) {
                recipe = recipeViewModel.getRecipeOnline(recipeId)
                recipeViewModel.insertRecipe(recipe!!)
            } else {
                recipe = recipeViewModel.getRecipeOffline(recipeId)
            }
            if (recipe == null) {
                Log.i("internet", "Check your internet connection and try again.")
                return@launch
            }

            getIngredients()
            withContext(Main) { bindRecipeData(view) }
        }

    }

    private fun handleFavouriteButton() = lifecycleScope.launch {
        if (isFavourite) {
            withContext(IO) { favouriteViewModel.deleteFavourite(Favourite(Auth.id(), recipeId)) }
            binding.favBtn.setImageResource(R.drawable.fav_icon)
            toast("Recipe unsaved.")
        } else {
            withContext(IO) { favouriteViewModel.insertFavourite(Favourite(Auth.id(), recipeId)) }
            binding.favBtn.setImageResource(R.drawable.fav_filled_icon)
            toast("Recipe saved.")
        }
        isFavourite = !isFavourite
    }

    @SuppressLint("SetTextI18n")
    private fun bindRecipeData(view: View) = binding.apply {

        loadVideo(recipe?.strYoutube)
        title.text = recipe?.strMeal
        tvCategory.text = recipe?.strCategory

        if (isFavourite)
            binding.favBtn.setImageResource(R.drawable.fav_filled_icon)
        binding.favBtn.setOnClickListener { handleFavouriteButton() }

        Glide.with(view)
            .load(recipe?.strMealThumb)
            .placeholder(R.drawable.loading)
            .error(R.drawable.baseline_error_24)
            .into(thumbnail)

        instructionsComplete.text = recipe?.strInstructions
        moreDetails.setOnClickListener {
            if (instructionsComplete.visibility == VISIBLE) {
                instructionsComplete.visibility = GONE
                moreDetailsBtn.text = "Show instructions"
                moreDetailsIconBtn.setImageResource(R.drawable.more_btn_icon)
            } else {
                instructionsComplete.visibility = VISIBLE
                moreDetailsBtn.text = "Hide instructions"
                moreDetailsIconBtn.setImageResource(R.drawable.less_btn_icon)
            }
        }

        rvIngredients.adapter = IngredientAdapter(ingredients)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadVideo(youtubeLink: String?) {
        val webView = binding.webview

        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.settings.domStorageEnabled = true
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

        val videoId = youtubeLink?.substringAfter("=")
        val htmlData = """
            <html>
            <body style="margin:0;padding:0;">
            <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoId" frameborder="0" allowfullscreen></iframe>
            </body>
            </html>
        """
        webView.loadData(htmlData, "text/html", "utf-8")
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getIngredients() {
        for (i in 1..20) {
            val name = getMember("strIngredient$i")
            val measure = getMember("strMeasure$i")
            val image = "$BASE_INGREDIENT_URL/$name.png"
            if (!name.isNullOrBlank()) {
                ingredients.add(Ingredient(name, measure!!, image))
            }
        }
    }

    private fun getMember(memberName: String): String? {
        return Recipe::class.memberProperties
            .firstOrNull { it.name == memberName }
            ?.get(recipe!!) as? String
    }
}
