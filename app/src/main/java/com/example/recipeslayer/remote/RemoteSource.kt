package com.example.recipeslayer.remote

class RemoteSource private constructor(): IRemoteSource {

    private val api = RetrofitConnection.getClient()

    companion object {
        @Volatile
        private var INSTANCE: RemoteSource? = null

        fun getInstance(): RemoteSource {
            if (INSTANCE == null) {
                INSTANCE = synchronized(this) { RemoteSource() }
            }
            return INSTANCE as RemoteSource
        }
    }

    override suspend fun getRecipes(category: String) = api.getRecipes(category).meals
    override suspend fun getRecipeById(recipeId: String) = api.getRecipeById(recipeId)
    override suspend fun searchByName(name: String) = api.searchByName(name).meals
}