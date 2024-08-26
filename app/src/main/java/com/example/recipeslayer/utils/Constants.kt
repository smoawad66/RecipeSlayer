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

    const val arabicPrompt = " أنا أستخدمك لتطبيق الأندرويد الخاص بي. أنا أستخدمك للإجابة على الأسئلة في سياق الطبخ. سيعطيك المستخدم موجهًا. إذا كان خارج سياق الطبخ، فقط أجب بأنك تعرف فقط عن الطبخ. ها هو الموجه: "
    const val englishPrompt = "I am using you for my android app. I am using you to answer questions in the context of cooking. The user will give you a prompt. If it is out of the context of cooking, just answer that you only know about cooking. Here is the prompt: "


    const val BASE_INGREDIENT_URL = "https://www.themealdb.com/images/ingredients"
    const val GEMINI_API_KEY = "AIzaSyBJDwCxczi8DQa6LY5ig0SZNOO-dUIyoYM"
}