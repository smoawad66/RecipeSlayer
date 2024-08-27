package com.example.recipeslayer.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object Internet {

//    fun isInternetAvailable(context: Context): Boolean {
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        val network = connectivityManager.activeNetwork ?: return false
//        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
//
//        return when {
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//            else -> false
//        }
//    }

    suspend fun isInternetAvailable(): Boolean {
        return withContext(IO) {
            try {
                val timeoutMs = 2000
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                socket.connect(socketAddress, timeoutMs)
                socket.close()
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}