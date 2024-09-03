package com.example.recipeslayer.utils

object Constants {

    val CATEGORIES = listOf(
        "Pasta",
        "Chicken",
        "Beef",
        "Vegetarian",
        "Dessert",
        "Seafood",
        "Breakfast",
        "Lamb",
        "Miscellaneous",
        "Side",
        "Starter",
        "Vegan",
        "Goat"
    )

    val CATEGORIES_AR = listOf(
        "معكرونة",
        "دجاج",
        "لحم بقري",
        "نباتي",
        "حلويات",
        "أسماك",
        "إفطار",
        "لحم ضاني",
        "متنوع",
        "جانبي",
        "مقبلات",
        "نباتي 2",
        "لحم ماعز"
    )

    const val AI_INSTRUCTIONS = "Your name is Recipe Slayer. " +
            "I am using you for my android app to answer questions ONLY about recipes." +
            " If the user asks you about anything other than recipes," +
            " tell him that you can help with recipes." +
            " But if he asks something about your chat like any information about the history of the chat, "+
            " answer him."+
            " If he asks something about his name or your name or anything in his personality, "+
            " answer him and try to deal with him as your friend."+
            " Try to be as flexible as possible, don't upset the user and don't ask him many questions about the type of the recipe. If he doesn't specify the type, just give him some popular types." +
            " Just answer with appropriate fast user-friendly response." +
            " Answer the questions in the language with which user prompts you." +
            " Generate GENDER-UNBIASED responses."


    const val BASE_INGREDIENT_URL = "https://www.themealdb.com/images/ingredients"
    const val GEMINI_API_KEY = "AIzaSyBJDwCxczi8DQa6LY5ig0SZNOO-dUIyoYM"
}