// In: ui/settings/SettingsScreen.kt
package com.seif.salatukalyawm.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seif.salatukalyawm.R

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity

    // استمع لحدث إعادة الإنشاء لتحديث اللغة
    LaunchedEffect(Unit) {
        settingsViewModel.recreateActivity.collect {
            activity?.recreate()
        }
    }

    // الواجهة الرئيسية للشاشة
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(stringResource(id = R.string.title_settings), style = MaterialTheme.typography.headlineMedium)

        LanguageSettings(settingsViewModel)
        ThemeSettings(settingsViewModel)
        PrayerMethodSettings(settingsViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSettings(settingsViewModel: SettingsViewModel) {
    val languageOptions = settingsViewModel.languageOptions
    val currentLanguageKey by settingsViewModel.currentLanguage.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    // ابحث عن النص المعروض للاختيار الحالي
    val selectedText = languageOptions.find { it.key == currentLanguageKey }?.displayText ?: ""

    Column {
        Text(stringResource(id = R.string.settings_section_language), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.settings_label_app_language)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                languageOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayText) },
                        onClick = {
                            settingsViewModel.setLanguage(option.key)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSettings(settingsViewModel: SettingsViewModel) {
    val themeOptions = settingsViewModel.themeOptions
    val currentThemeKey by settingsViewModel.currentThemeMode.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    val selectedText = themeOptions.find { it.key == currentThemeKey }?.displayText ?: ""

    Column {
        Text(stringResource(id = R.string.settings_section_appearance), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.settings_label_theme)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                themeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayText) },
                        onClick = {
                            settingsViewModel.setThemeMode(option.key)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerMethodSettings(settingsViewModel: SettingsViewModel) {
    val calculationMethods = settingsViewModel.calculationMethods
    val currentMethodKey by settingsViewModel.currentMethod.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    val selectedText = calculationMethods.find { it.key == currentMethodKey }?.displayText ?: ""

    Column {
        Text(stringResource(id = R.string.settings_section_prayer_times), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.settings_label_calculation_method)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                calculationMethods.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayText) },
                        onClick = {
                            settingsViewModel.setPrayerMethod(option.key)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}
