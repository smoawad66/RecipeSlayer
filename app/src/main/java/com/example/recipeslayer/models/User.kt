package com.example.recipeslayer.models

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    var name: String,
    var email: String,
    var password: String,
    var picture: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
): Serializable