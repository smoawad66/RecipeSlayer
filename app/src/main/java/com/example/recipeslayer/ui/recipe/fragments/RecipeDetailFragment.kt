package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.recipeslayer.R
import android.graphics.Color // Make sure to import android.graphics.Color
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.ui.recipe.IngredientAdapter

class RecipeDetailFragment : Fragment() {
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        var recyclerView = view.findViewById<RecyclerView>(R.id.rv)
        var webView = view.findViewById<WebView>(R.id.webview)
        var image = view.findViewById<ImageView>(R.id.recipeimg)
        var title = view.findViewById<TextView>(R.id.title)
        var instruction1 = view.findViewById<TextView>(R.id.instruction1)
        var instruction2 = view.findViewById<TextView>(R.id.instruction2)

        //val args = RecipeDetailFragmentArgs.fromBundle(requireArguments())

        webView = view.findViewById(R.id.webview)

        // Table
//        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)
//        val args:RecipeDetailFragmentArgs by navArgs()
//        val recipe=args.recipe
//
//        for (i in 1..5) {
//            // Create a new TableRow
//            val tableRow = TableRow(requireContext())
//
//            // Set layout parameters for the TableRow
//            tableRow.layoutParams = TableRow.LayoutParams(
//                TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.WRAP_CONTENT
//            )
//
//            // Create TextViews for each cell in the row
//            val cell1 = TextView(requireContext()).apply {
//                text = "Row $i, Col 1"
//                setTextColor(Color.BLACK) // Use Color.BLACK instead of Color.Black
//                setPadding(16, 16, 16, 16)
//                gravity = Gravity.CENTER
//            }
//
//            val cell2 = TextView(requireContext()).apply { // Use requireContext()
//                text = "Row $i, Col 2"
//                setTextColor(Color.BLACK) // Use Color.BLACK instead of Color.Black
//                setPadding(16, 16, 16, 16)
//                gravity = Gravity.CENTER
//            }
//
//
//            tableRow.addView(cell1)
//            tableRow.addView(cell2)
//
//            // Add the TableRow to the TableLayout
//            tableLayout.addView(tableRow)
//        }

        // Load the video in the WebView
        val videoUrl = "https://www.youtube.com/embed/V2KCAfHjySQ?si=r2cL1eRk0aotRoiK"

        // Set WebViewClient to handle page navigation in the WebView
        webView.webViewClient = WebViewClient()

        // Enable JavaScript if needed
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Load the video in the WebView

        webView.loadUrl(videoUrl)
        val adapter=IngredientAdapter(listOf())
        recyclerView.adapter=adapter
    }
}
