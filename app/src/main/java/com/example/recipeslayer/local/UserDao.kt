package com.example.recipeslayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.recipeslayer.models.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUser(email: String): User

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User

    @Update
    suspend fun updateUser(user: User)

    @Insert
    suspend fun insertUser(user: User): Long
}