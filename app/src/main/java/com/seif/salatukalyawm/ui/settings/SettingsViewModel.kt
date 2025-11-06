// In: ui/settings/SettingsViewModel.kt
package com.seif.salatukalyawm.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seif.salatukalyawm.R
import com.seif.salatukalyawm.data.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// data class لتمثيل أي خيار في الإعدادات بشكل منظم
data class SettingOption<T>(
    val key: T, // المفتاح الذي سيتم حفظه (مثل "ar", "en", 5)
    val displayText: String // النص الذي سيظهر للمستخدم (مترجم وجاهز)
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val resources = application.resources
    private val settingsManager = SettingsManager(application)

    // SharedFlow لإرسال حدث لمرة واحدة لإعادة إنشاء الـ Activity
    private val _recreateActivity = MutableSharedFlow<Unit>()
    val recreateActivity = _recreateActivity.asSharedFlow()

    // --- Language Logic ---
    val currentLanguage: StateFlow<String> = settingsManager.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsManager.DEFAULT_LANGUAGE)

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(language)
            _recreateActivity.emit(Unit) // أرسل الحدث بعد الحفظ
        }
    }

    // قائمة خيارات اللغة (مترجمة وجاهزة)
    val languageOptions: List<SettingOption<String>> = listOf(
        SettingOption("System", resources.getString(R.string.language_system)),
        SettingOption("ar", "العربية"),
        SettingOption("en", "English")
    )

    // --- Theme Logic ---
    val currentThemeMode: StateFlow<String> = settingsManager.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsManager.DEFAULT_THEME)

    fun setThemeMode(themeMode: String) {
        viewModelScope.launch {
            settingsManager.setThemeMode(themeMode)
        }
    }

    // قائمة خيارات المظهر (مترجمة وجاهزة)
    val themeOptions: List<SettingOption<String>> = listOf(
        SettingOption("Light", resources.getString(R.string.theme_light)),
        SettingOption("Dark", resources.getString(R.string.theme_dark)),
        SettingOption("System", resources.getString(R.string.theme_system_default))
    )

    // --- Prayer Method Logic ---
    val currentMethod: StateFlow<Int> = settingsManager.prayerMethodFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsManager.DEFAULT_METHOD)

    fun setPrayerMethod(method: Int) {
        viewModelScope.launch {
            settingsManager.setPrayerMethod(method)
        }
    }

    // قائمة خيارات طريقة الحساب (مترجمة وجاهزة)
    val calculationMethods: List<SettingOption<Int>> = listOf(
        SettingOption(5, resources.getString(R.string.calculation_method_egyptian_general_authority)),
        SettingOption(4, resources.getString(R.string.calculation_method_umm_al_qura_university)),
        SettingOption(3, resources.getString(R.string.calculation_method_muslim_world_league)),
        SettingOption(1, resources.getString(R.string.calculation_method_islamic_society_of_north_america))
        // يمكنك إضافة المزيد من الخيارات هنا إذا أردت
    )
}
