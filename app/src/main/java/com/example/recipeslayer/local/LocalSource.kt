package com.example.recipeslayer.local

import com.example.recipeslayer.models.User

class LocalSource private constructor(): ILocalSource {

    private val userDao = RecipeRoomDatabase.getInstance().getUserDao()

    companion object {
        @Volatile
        private var INSTANCE: LocalSource? = null

        fun getInstance(): LocalSource {
            if (INSTANCE == null) {
                INSTANCE = synchronized(this) { LocalSource() }
            }
            return INSTANCE as LocalSource
        }
    }

    // User
    override suspend fun getUser(email: String) = userDao.getUser(email)
    override suspend fun insertUser(user: User) = userDao.insertUser(user)

}