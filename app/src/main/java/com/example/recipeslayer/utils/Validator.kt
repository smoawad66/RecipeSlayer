package com.example.recipeslayer.utils

import java.util.regex.Pattern

object Validator {

    fun validatePassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$"
        return matchRegex(passwordPattern, password)
    }

    fun validateEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return matchRegex(emailPattern, email)
    }

    private fun matchRegex(pattern: String, value: String): Boolean {
        return Pattern.compile(pattern).matcher(value).matches()
    }
}