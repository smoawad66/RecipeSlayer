package com.example.recipeslayer.utils

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.recipeslayer.R

object toast {


    fun toast(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.toast, null)
        val textView: TextView = layout.findViewById(R.id.text)
        textView.text = message
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 120)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}