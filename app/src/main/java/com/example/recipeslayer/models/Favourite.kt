package com.example.recipeslayer.models

import androidx.room.Entity
import androidx.room.ForeignKey
import java.io.Serializable
import java.util.Date

@Entity(
    tableName = "favourites",
    primaryKeys = ["userId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            childColumns = ["userId"],
            parentColumns = ["id"],
            entity = User::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        ),
        ForeignKey(
            childColumns = ["recipeId"],
            parentColumns = ["idMeal"],
            entity = Recipe::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class Favourite(
    val userId: Long,
    val recipeId: Long,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
