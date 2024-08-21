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
    override suspend fun getRecipe(recipeId: Long) = api.getRecipe(recipeId).meals?.first()
    override suspend fun searchRecipes(name: String) = api.searchRecipes(name).meals
}