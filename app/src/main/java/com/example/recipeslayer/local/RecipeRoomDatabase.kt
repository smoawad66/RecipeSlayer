package com.example.recipeslayer.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeslayer.models.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class RecipeRoomDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeRoomDatabase? = null

        fun initRoom(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        RecipeRoomDatabase::class.java,
                        "recipes_db"
                    ).build()
                }
            }
        }

        fun getInstance() = INSTANCE as RecipeRoomDatabase
    }
}