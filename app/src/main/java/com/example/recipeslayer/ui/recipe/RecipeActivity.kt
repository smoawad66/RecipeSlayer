package com.example.recipeslayer.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.NavController
import com.example.recipeslayer.R
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.ui.auth.AuthActivity
import com.example.recipeslayer.ui.recipe.fragments.AboutFragment
import com.example.recipeslayer.ui.recipe.fragments.FavouriteFragment
import com.example.recipeslayer.ui.recipe.fragments.HomeFragment
import com.example.recipeslayer.ui.recipe.fragments.IdeasFragment
import com.example.recipeslayer.ui.recipe.fragments.ProfileFragment
import com.example.recipeslayer.ui.recipe.fragments.RecipeDetailFragment
import com.example.recipeslayer.ui.recipe.fragments.SearchFragment
import com.example.recipeslayer.ui.recipe.fragments.SettingsFragment
import com.example.recipeslayer.ui.recipe.viewModels.FavouriteViewModel
import com.example.recipeslayer.utils.Auth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeActivity : AppCompatActivity() {
    lateinit var bottomBar: ChipNavigationBar
    private lateinit var fragmentTitle: TextView
    private lateinit var navController: NavController
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        fragmentTitle = findViewById(R.id.fragment_title)
        bottomBar = findViewById(R.id.bottom_bar)
        bottomBar.setItemSelected(R.id.home, true)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)

        val toggleButton: ImageView = findViewById(R.id.option_menu)
        toggleButton.setOnClickListener { showPopupMenu(toggleButton) }

        //profile_pic opens profile fragment
        val profilePic = findViewById<ImageView>(R.id.logo_46)
        profilePic.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }

        val profilePicIv: ImageView = findViewById(R.id.logo_46)
        val repo = Repo()
        val id = Auth.id()
        var profilePicPath: String? = null
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { repo.getUser(id) }

            withContext(Dispatchers.Main) {
                profilePicPath = user.picture

                if (!profilePicPath.isNullOrEmpty()) {
                    profilePicIv.setImageURI(profilePicPath?.toUri())
                }
            }
        }

        // Listen for any fragment that is resumed
        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                when (f) {
                    is HomeFragment -> selectNavItem(R.string.home)
                    is SearchFragment -> selectNavItem(R.string.search)
                    is FavouriteFragment -> selectNavItem(R.string.favorites)
                    is RecipeDetailFragment -> selectNavItem(R.string.details)
                    is AboutFragment -> selectNavItem(R.string.about_us)
                    is SettingsFragment -> selectNavItem(R.string.settings)
                    is IdeasFragment -> selectNavItem(R.string.ideas)
                    is ProfileFragment -> selectNavItem(R.string.profile)
                }
            }
        }, true)


        favouriteViewModel.getFavouriteRecipes(Auth.id())
        favouriteViewModel.recipes.observe(this) {
            favouriteViewModel.apply {
                if (it.size > preCount)
                    bottomBar.showBadge(R.id.favourites, it.size)
                preCount = it.size
            }
        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)


    }

    private fun showPopupMenu(view: ImageView) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.options_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    Auth.logout()
                    val intent = Intent(this, AuthActivity::class.java)
                    intent.putExtra("splashTime", 0L)
                    startActivity(intent)
                    finishAffinity()
                    true
                }

                R.id.menu_about -> {
                    navController.navigate(R.id.aboutFragment)
                    true
                }

                else -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
            }
        }
        popupMenu.show()
    }

    private fun selectNavItem(key: Int) {
        fragmentTitle.text = getString(key)
        val items = mapOf(R.string.home to R.id.home, R.string.search to R.id.search, R.string.favorites to R.id.favourites)
        if (items[key] == null) {
            items.forEach { bottomBar.setItemSelected(it.value, false) }
            return
        }

        bottomBar.setOnItemSelectedListener {}
        bottomBar.setItemSelected(items[key]!!, true)
        listenToBottomBar()
    }

    private fun listenToBottomBar() {
        bottomBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> navController.navigate(R.id.homeFragment)
                R.id.search -> navController.navigate(R.id.searchFragment)
                R.id.favourites -> {
                    navController.navigate(R.id.favouriteFragment)
                    bottomBar.dismissBadge(R.id.favourites)
                }
            }
        }
    }
}