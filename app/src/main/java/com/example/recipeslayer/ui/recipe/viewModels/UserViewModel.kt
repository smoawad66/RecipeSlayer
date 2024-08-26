package com.example.recipeslayer.ui.recipe.viewModels

import androidx.lifecycle.ViewModel
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo

class UserViewModel : ViewModel() {

    val repo = Repo()
    suspend fun getUser(id: Long): User {
        return repo.getUser(id)
    }
}