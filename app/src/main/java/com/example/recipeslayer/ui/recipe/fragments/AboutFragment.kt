package com.example.recipeslayer.ui.recipe.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class AboutFragment : Fragment() {

//    private lateinit var bottomBar: ChipNavigationBar
//    private lateinit var actionBarCl: ConstraintLayout
//    private lateinit var closeBtn: ConstraintLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Initialize NavController
        navController = findNavController()

        // Initialize views
//        closeBtn = view.findViewById(R.id.main_about_cl)
//
//        // Set click listener for the close button
//        closeBtn.setOnClickListener {
//            // Optionally, you can make the bottom bar and action bar visible if needed
////            actionBarCl = view?.findViewById(R.id.cl)!!
////            actionBarCl.visibility = View.VISIBLE
////
////            bottomBar = view?.findViewById(R.id.bottom_bar)!!
////            bottomBar.visibility = View.VISIBLE
//
//            // Pop this fragment from the back stack
////            navController.popBackStack()
//            requireActivity().supportFragmentManager.popBackStack()
////            requireActivity().finish()
//        }

        return view
    }
}
