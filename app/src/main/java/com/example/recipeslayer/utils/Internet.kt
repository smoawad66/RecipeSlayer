package com.example.recipeslayer.utils

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object Internet {

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