// In: data/SettingsManager.kt
package com.seif.salatukalyawm.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// إنشاء DataStore على مستوى التطبيق
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // --- مفاتيح الإعدادات ---
        val PRAYER_METHOD_KEY = intPreferencesKey("prayer_method")
        const val DEFAULT_METHOD = 5

        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        const val DEFAULT_THEME = "System" // القيم الممكنة: "Light", "Dark", "System"

        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        const val DEFAULT_LANGUAGE = "System" // القيم الممكنة: "System", "ar", "en"
    }

    // --- حفظ طريقة الحساب ---
    suspend fun setPrayerMethod(method: Int) {
        dataStore.edit { settings ->
            settings[PRAYER_METHOD_KEY] = method
        }
    }

    // --- قراءة طريقة الحساب ---
    val prayerMethodFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PRAYER_METHOD_KEY] ?: DEFAULT_METHOD
        }

    // --- حفظ وضع المظهر ---
    suspend fun setThemeMode(themeMode: String) {
        dataStore.edit { settings ->
            settings[THEME_MODE_KEY] = themeMode
        }
    }

    // --- قراءة وضع المظهر ---
    val themeModeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: DEFAULT_THEME
        }

    // --- حفظ اللغة ---
    suspend fun setLanguage(language: String) {
        dataStore.edit { settings ->
            settings[LANGUAGE_KEY] = language
        }
    }

    // --- قراءة اللغة ---
    val languageFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: DEFAULT_LANGUAGE
        }
}
