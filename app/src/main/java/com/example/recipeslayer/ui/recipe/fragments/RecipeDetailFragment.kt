package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentRecipeDetailBinding
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Ingredient
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.FavouriteViewModel
import com.example.recipeslayer.ui.recipe.IngredientAdapter
import com.example.recipeslayer.ui.recipe.RecipeViewModel
import com.example.recipeslayer.utils.Auth
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.full.memberProperties


class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val args: RecipeDetailFragmentArgs by navArgs()
    private var recipeDetails: Recipe? = null
    private var ingredients: MutableList<Ingredient> = mutableListOf()
    private val BASE_INGREDIENT_URL = "https://www.themealdb.com/images/ingredients"

    private lateinit var recipe: Recipe
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

        recipe = args.recipe
        recipeId = recipe.idMeal
        userId = Auth.id()

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            findNavController().popBackStack()
//        }

        lifecycleScope.launch(IO) {

            favouriteId = favouriteViewModel.getFavouriteId(userId, recipe)

            withContext(Main) {
                if (favouriteId != null) { // if it is favourite, change the icon
                    binding.favBtn.setImageResource(R.drawable.fav_filled_icon)
                }
            }

            recipeDetails = recipeViewModel.getRecipeDetails(recipeId)
            withContext(Main) { bindRecipeData(view) }

            // Get Ingredients
            for (i in 1..20) {
                val name = getMember("strIngredient$i")
                val measure = getMember("strMeasure$i")
                val image = "$BASE_INGREDIENT_URL/$name.png"
                if (!name.isNullOrBlank()) {
                    ingredients.add(Ingredient(name, measure!!, image))
                }
            }

            withContext(Main) {
                val adapter = IngredientAdapter(ingredients)
                binding.rvIngredients.adapter = adapter
            }
        }


        binding.favBtn.setOnClickListener { handleFavourite() }

    }


    private fun handleFavourite() {

        lifecycleScope.launch {
            if (favouriteId != null) { // if is favourite
                withContext(IO) { favouriteViewModel.deleteFavourite(Favourite(Auth.id(), recipe, favouriteId!!)) }
                binding.favBtn.setImageResource(R.drawable.fav_icon)
                toast("Recipe unsaved.")
                favouriteId = null
            } else { // if is not favourite
                val newId = withContext(IO) { favouriteViewModel.insertFavourite(Favourite(Auth.id(), recipe)) }
                binding.favBtn.setImageResource(R.drawable.fav_filled_icon)
                toast("Recipe saved.")
                favouriteId = newId
            }

        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun bindRecipeData(view: View) {
        binding.apply {
            title.text = recipe.strMeal
            Glide.with(view)
                .load(recipe.strMealThumb)
                .placeholder(R.drawable.loading)
                .error(R.drawable.baseline_error_24)
                .into(thumbnail)

            title.text = recipe.strMeal
            instructionsBreif.text = recipeDetails?.strInstructions
            instructionsComplete.text = recipeDetails?.strInstructions


//            Log.i("url", "${recipeDetails?.strYoutube}")

            loadVideo(recipeDetails?.strYoutube)

            // Handle more details click
            moreDetails.setOnClickListener {
                if (instructionsComplete.visibility == View.VISIBLE) {
                    instructionsComplete.visibility = View.GONE
                    instructionsBreif.visibility = View.VISIBLE
                    moreDetailsBtn.text = "show more"
                    moreDetailsIconBtn.setImageResource(R.drawable.more_btn_icon)
                } else {
                    instructionsComplete.visibility = View.VISIBLE
                    instructionsBreif.visibility = View.GONE
                    moreDetailsBtn.text = "show less"
                    moreDetailsIconBtn.setImageResource(R.drawable.less_btn_icon)
                }
            }
        }
    }

    private fun loadVideo(youtubeLink: String?) {
        val webView = binding.webview

        // Enable JavaScript
        webView.settings.javaScriptEnabled = true

        // Enable media playback
        webView.settings.mediaPlaybackRequiresUserGesture = false

        // Set WebView client
        webView.webViewClient = WebViewClient()

        // Set WebChromeClient to handle loading progress
        webView.webChromeClient = WebChromeClient()

        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.settings.domStorageEnabled = true
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

        // Load the YouTube video
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


    private fun getMember(memberName: String): String? {
        return Recipe::class.memberProperties
            .firstOrNull { it.name == memberName }
            ?.get(recipeDetails!!) as? String
    }
}
