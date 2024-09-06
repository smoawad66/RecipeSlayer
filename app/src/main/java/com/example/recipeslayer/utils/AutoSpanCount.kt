package com.example.recipeslayer.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

object AutoSpanCount {
    private fun getSpanCount(context: Context, itemWidthDp: Int): Int {
        val displayMetrics = DisplayMetrics()
        (context as? Activity)?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidthPx = displayMetrics.widthPixels
        val itemWidthPx = dpToPx(context, itemWidthDp)
        return screenWidthPx / itemWidthPx
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun setupRecyclerView(recyclerView: RecyclerView, itemWidthDp: Int) {
        val spanCount = getSpanCount(recyclerView.context, itemWidthDp)
        val layoutManager = GridLayoutManager(recyclerView.context, spanCount)
        recyclerView.layoutManager = layoutManager
    }

}