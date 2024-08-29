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

    const val arabicPrompt = " أنا أستخدمك لتطبيق الأندرويد الخاص بي. أنا أستخدمك للإجابة على الأسئلة في سياق الطبخ والطعام بشكل عام. سيعطيك المستخدم موجهًا. إذا كان خارج سياق الطعام والطبخ،أجب بطريقة محترمة وجيدة بأنك تعرف فقط عن الطبخ. حاول ان تكون مرن بقدر الامكان اذا أخطأ المستخدم في كتابة بعض الحروف. ها هو الموجه: "
    const val englishPrompt = "I am using you for my android app. I am using you to answer questions in the context of cooking or food generally. The user will give you a prompt. If it is out of the context of food, just answer that you only know about food. Try to be as flexible as possible if the user enters words with spelling mistakes. Here is the prompt: "


    const val BASE_INGREDIENT_URL = "https://www.themealdb.com/images/ingredients"
    const val GEMINI_API_KEY = "AIzaSyBJDwCxczi8DQa6LY5ig0SZNOO-dUIyoYM"
}