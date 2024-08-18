package com.example.recipeslayer.repo

import com.example.recipeslayer.models.User

interface IRepo {
    suspend fun getUser(email: String) : User
    suspend fun insertUser(user: User): Long
}