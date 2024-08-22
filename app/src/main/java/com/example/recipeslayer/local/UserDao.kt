package com.example.recipeslayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.recipeslayer.models.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUser(email: String): User

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: Long): User

    @Insert
    suspend fun insertUser(user: User): Long
}