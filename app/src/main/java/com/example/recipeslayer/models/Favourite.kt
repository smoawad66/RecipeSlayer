package com.example.recipeslayer.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "favourites",
    foreignKeys = [
        ForeignKey(
            childColumns = ["userId"],
            parentColumns = ["id"],
            entity = User::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class Favourite(
    val userId: Long,
    val recipe: Recipe,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
): Serializable
