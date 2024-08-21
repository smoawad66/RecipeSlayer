package com.example.recipeslayer.ui.recipe.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.reflect.full.memberProperties
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val args: RecipeDetailFragmentArgs by navArgs()
    private lateinit var recipeDetails: Recipe
    private var ingredients: MutableList<Ingredient> = mutableListOf()
    private val BASE_INGREDIENT_URL = "https://www.themealdb.com/images/ingredients"

    private var userId = -1L
    private lateinit var recipeId: String
    private var favouriteId: Long? = null

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

        recipeId = args.recipe.idMeal
        userId = Auth.id()

        lifecycleScope.launch(IO) {

            if (args.favourite != null) {
                recipeDetails = args.recipe
                favouriteId = args.favourite!!.id
            }
            else {
                recipeDetails = recipeViewModel.getRecipeDetails(recipeId)!!
                favouriteId = favouriteViewModel.getFavouriteId(userId, recipeDetails)
            }

            withContext(Main) {
                if (favouriteId != null) { // if it is favourite, change the icon
                    binding.favBtn.setImageResource(R.drawable.fav_filled_icon)
                }
                bindRecipeData(view)
            }

            getIngredients()

            withContext(Main) {
                val adapter = IngredientAdapter(ingredients)
                binding.rvIngredients.adapter = adapter
            }
        }

        binding.favBtn.setOnClickListener { handleFavourite() }

    }

    private fun handleFavourite() = lifecycleScope.launch {
        if (favouriteId != null) { // if is favourite
            withContext(IO) { favouriteViewModel.deleteFavourite(Favourite(Auth.id(), recipeDetails, favouriteId!!)) }
            binding.favBtn.setImageResource(R.drawable.fav_icon)
            toast("Recipe unsaved.")
            favouriteId = null
        } else { // if is not favourite
            val newId = withContext(IO) { favouriteViewModel.insertFavourite(Favourite(Auth.id(), recipeDetails)) }
            binding.favBtn.setImageResource(R.drawable.fav_filled_icon)
            toast("Recipe saved.")
            favouriteId = newId
        }

    }

    @SuppressLint("SetTextI18n")
    private fun bindRecipeData(view: View) = binding.apply {
        loadVideo(recipeDetails.strYoutube)

        title.text = recipeDetails.strMeal
        tvCategory.text = recipeDetails.strCategory
        Glide.with(view)
            .load(recipeDetails.strMealThumb)
            .placeholder(R.drawable.loading)
            .error(R.drawable.baseline_error_24)
            .into(thumbnail)

        title.text = recipeDetails.strMeal
//        instructionsBreif.text = recipeDetails.strInstructions?.substringBefore(".") + "."
        instructionsComplete.text = recipeDetails.strInstructions


        // Handle more details click
        moreDetails.setOnClickListener {
            if (instructionsComplete.visibility == View.VISIBLE) {
                instructionsComplete.visibility = View.GONE
                moreDetailsBtn.text = "Show instructions"
                moreDetailsIconBtn.setImageResource(R.drawable.more_btn_icon)
            } else {
                instructionsComplete.visibility = View.VISIBLE
                moreDetailsBtn.text = "Hide instructions"
                moreDetailsIconBtn.setImageResource(R.drawable.less_btn_icon)
            }
        }

    }

    private fun loadVideo(youtubeLink: String?) {
        val webView = binding.webview

//        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
//        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//        webView.settings.domStorageEnabled = true
//        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

        val videoId = youtubeLink?.substringAfter("=")
        val videoUrl = "https://www.youtube.com/embed/$videoId"
        val htmlData = """
            <html>
            <body style="margin:0;padding:0;">
            <iframe width="100%" height="100%" src="$videoUrl" frameborder="0" allowfullscreen></iframe>
            </body>
            </html>
        """
        webView.loadData(htmlData, "text/html", "utf-8")
    }


    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getIngredients() {
        // Get Ingredients
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
            ?.get(recipeDetails) as? String
    }
}
