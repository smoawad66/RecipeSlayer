package com.example.recipeslayer.utils

import android.content.Context
import android.net.ConnectivityManager

object Internet {

    fun isInternetAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo
        return activeNetwork != null
    }
}