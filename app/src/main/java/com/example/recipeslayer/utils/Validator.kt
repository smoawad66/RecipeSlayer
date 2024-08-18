package com.example.recipeslayer.utils

import java.util.regex.Pattern

object Validator {

    fun validatePassword(password: String) = password.length >= 8

    fun validateEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        val matcher = Pattern.compile(emailPattern).matcher(email)
        return matcher.matches()
    }
}