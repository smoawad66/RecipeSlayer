package com.example.recipeslayer.ui.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.recipeslayer.R
import com.example.recipeslayer.ui.auth.AuthActivity
import com.example.recipeslayer.utils.Auth
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.TextMessage
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RecipeActivity : AppCompatActivity() {
    lateinit var bottomBar : ChipNavigationBar
    private var isMenuVisible = false
    private lateinit var profilePic: ImageView
    private lateinit var fragment_title: TextView
    private lateinit var navController: NavController
    private val favouriteViewModel: FavouriteViewModel by viewModels()

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
//    val navController = findNavController(R.id.nav_host_fragment)
//    private var _binding: ActivityRecipyBinding? = null
//    private val binding get() = _binding!!
//
//    private val sharedPreferences: SharedPreferences
//        get() = getSharedPreferences("Flags", Context.MODE_PRIVATE)
//
//    lateinit var logout : TextView

    private var conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    lateinit var conversations: ArrayList<TextMessage>

    lateinit var smartReplyGenerator: SmartReplyGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)



        profilePic = findViewById(R.id.profile_pic)
        profilePic.setOnClickListener {

//                bottomBar.setItemSelected(R.id.home, false)
//                bottomBar.setItemSelected(R.id.favorites, false)
//                bottomBar.setItemSelected(R.id.search, false)
//                bottomBar.setItemSelected(R.id.ideas, false)

        }


        ////////////////////////////////////////////////////////////////
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.ARABIC)
            .build()

        val englishArabicTranslator = Translation.getClient(options)
        var result: String = ""
        englishArabicTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                englishArabicTranslator.translate("Apple pie koshari")
                    .addOnSuccessListener { translatedText ->
                        result = translatedText
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }


        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        fragment_title = findViewById(R.id.fragment_title)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggleButton: ImageView = findViewById(R.id.option_menu)
        toggleButton.setOnClickListener {
            showPopupMenu(toggleButton)
        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomBar = findViewById(R.id.bottom_bar)
        bottomBar.setItemSelected(R.id.home, true)
        favouriteViewModel.getFavourites(Auth.id())
        favouriteViewModel.favouriteRecipes.observe(this) {
            if (it != null) {
                bottomBar.showBadge(R.id.favorites, it.size)
            }
        }//        bottomBar.visibility = INVISIBLE
//        bottomBar.visibility = VISIBLE

        bottomBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                    fragment_title.text = getString(R.string.home)
                }
                R.id.search -> {
                    navController.navigate(R.id.searchFragment)
                    fragment_title.text = getString(R.string.search)
                }
                R.id.favorites -> {
                    navController.navigate(R.id.favoriteFragment)
                    fragment_title.text = getString(R.string.favorites)
                    bottomBar.dismissBadge(R.id.favorites)
                }
                R.id.ideas -> {
                    navController.navigate(R.id.ideasFragment)
                    fragment_title.text = getString(R.string.ideas)
                }

            }

        }
//        navController.navigate(R.id.imageLabelingFragment)
//        navController.navigate(R.id.imageFragment)

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            bottomBar.setItemSelected(destination.id, true)
//        }
//
////        bottomBar.showBadge(R.id.favorites, 7)
//
////        logout = findViewById(R.id.recipeFragment)
////
////        logout.setOnClickListener {
////            val editor = sharedPreferences.edit()
////            editor.putInt("isLoggedIn", 0)
////            editor.putBoolean("fromRecipeActivity", true)
////            }

    }

    private fun showPopupMenu(view: ImageView) {
//        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
//        val popupMenu = PopupMenu(wrapper, view)
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.options_menu, popupMenu.menu)
//        try {
//            val fields = popupMenu.javaClass.declaredFields
//            for (field in fields) {
//                if ("mPopup" == field.name) {
//                    field.isAccessible = true
//                    val menuPopupHelper = field.get(popupMenu)
//                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
//                    val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
//                    setForceIcons.invoke(menuPopupHelper, true)
//                    break
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
//                    // Handle logout
                    Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()

//                    // Add your logout logic here, e.g., clearing SharedPreferences
//                    val editor = getSharedPreferences("Flags", Context.MODE_PRIVATE).edit()
//                    editor.putInt("isLoggedIn", 0)
//                    editor.putBoolean("fromRecipeActivity", true)
//                    editor.apply()

                    // Navigate to AuthActivity
                    Auth.logout()
                    val intent = Intent(this, AuthActivity::class.java)
                    intent.putExtra("splashTime", 0L)
                    startActivity(intent)
                    finishAffinity()
                    true
                }
                R.id.menu_about -> {
                    // Handle about
                    navController.navigate(R.id.aboutFragment)
//                    actionBarCl.visibility = View.GONE
//                    bottomBar.visibility = View.GONE
                    bottomBar.setItemSelected(R.id.home, false)
                    bottomBar.setItemSelected(R.id.favorites, false)
                    bottomBar.setItemSelected(R.id.search, false)
                    fragment_title.text = getString(R.string.about_us)


//                    Toast.makeText(this, "About Clicked!", Toast.LENGTH_SHORT).show()
                    true
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

}




