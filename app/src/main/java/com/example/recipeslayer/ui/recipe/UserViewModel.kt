package com.example.recipeslayer.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {

    val repo = Repo()
    suspend fun getUser(id: Long): User {
        return repo.getUser(id)
    }
}