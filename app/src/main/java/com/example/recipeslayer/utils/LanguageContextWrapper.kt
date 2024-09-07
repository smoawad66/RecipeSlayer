package com.example.recipeslayer.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.LocaleList
import java.util.*

class LanguageContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var newContext = context
            val configuration = newContext.resources.configuration
            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            newContext = newContext.createConfigurationContext(configuration)
            return ContextWrapper(newContext)
        }
    }
}