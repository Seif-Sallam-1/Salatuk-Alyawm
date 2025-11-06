// In: MainActivity.kt
package com.seif.salatukalyawm

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.seif.salatukalyawm.data.SettingsManager
import com.seif.salatukalyawm.ui.theme.SalatukAlyawmTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // هذا هو المكان الصحيح لتطبيق اللغة قبل إنشاء أي شيء
        val settingsManager = SettingsManager(newBase)
        // نقرأ اللغة بشكل متزامن هنا لأننا في مرحلة مبكرة جدًا
        val language = runBlocking { settingsManager.languageFlow.first() }
        val localeToSet = if (language == "System") {
            newBase.resources.configuration.locales[0]
        } else {
            Locale(language)
        }
        val updatedContext = updateResources(newBase, localeToSet)
        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // الآن setContent أصبح بسيطًا جدًا مرة أخرى
            SalatukAlyawmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale) // مهم للغات RTL مثل العربية
        return context.createConfigurationContext(configuration)
    }
}
