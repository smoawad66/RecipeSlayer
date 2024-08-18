package com.example.recipeslayer.local

import com.example.recipeslayer.models.User

interface ILocalSource {

    suspend fun getUser(email: String): User
    suspend fun insertUser(user: User): Long
}