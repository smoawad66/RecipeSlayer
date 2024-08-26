package com.example.recipeslayer.utils

import com.example.recipeslayer.models.Recipe

object Cache {
    var RECIPES_CACHE: MutableMap<String, List<Recipe>> = mutableMapOf()

    var RECIPES_CACHE_AR: MutableMap<String, List<Recipe>> = mutableMapOf()
}