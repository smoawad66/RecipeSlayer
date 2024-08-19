package com.example.recipeslayer.ui.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.ui.auth.AuthActivity
import com.example.recipeslayer.utils.Auth
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class RecipeActivity : AppCompatActivity() {
    lateinit var bottomBar : ChipNavigationBar
    private var isMenuVisible = false
    private lateinit var navController: NavController
    private lateinit var actionBarCl: ConstraintLayout
    private lateinit var fragment_title: TextView


    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
//    val navController = findNavController(R.id.nav_host_fragment)
//    private var _binding: ActivityRecipyBinding? = null
//    private val binding get() = _binding!!
//
//    private val sharedPreferences: SharedPreferences
//        get() = getSharedPreferences("Flags", Context.MODE_PRIVATE)
//
//    lateinit var logout : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        actionBarCl =findViewById<ConstraintLayout>(R.id.cl)
        actionBarCl.visibility = View.VISIBLE

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fragment_title = findViewById(R.id.fragment_title)
        val toggleButton: ImageView = findViewById(R.id.option_menu)
        toggleButton.setOnClickListener {
            showPopupMenu(toggleButton)
        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomBar = findViewById(R.id.bottom_bar)
//        bottomBar.visibility = View.VISIBLE
        bottomBar.setItemSelected(R.id.home, true)
        bottomBar.showBadge(R.id.favorites, 7)
//        bottomBar.visibility = INVISIBLE
//        bottomBar.visibility = VISIBLE

        bottomBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                    fragment_title.text = "Home"
                }
                R.id.search -> {
                    navController.navigate(R.id.searchFragment)
                    fragment_title.text = "Search"
                }
                R.id.favorites -> {
                    navController.navigate(R.id.favoriteFragment)
                    fragment_title.text = "Favorites"
                    bottomBar.dismissBadge(R.id.favorites)
                }

            }
        }
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

                    Auth.logout()

                    // Navigate to AuthActivity
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
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
                    fragment_title.text = "About Us"


//                    Toast.makeText(this, "About Clicked!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

}




