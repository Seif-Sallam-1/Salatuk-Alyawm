// In: Navigation.kt
package com.seif.salatukalyawm

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.seif.salatukalyawm.ui.PrayerTimesScreen
import com.seif.salatukalyawm.ui.QiblaScreen
import com.seif.salatukalyawm.ui.quran.QuranReaderScreen
import com.seif.salatukalyawm.ui.quran.QuranViewModel
import com.seif.salatukalyawm.ui.quran.SurahIndexScreen
import com.seif.salatukalyawm.ui.settings.SettingsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val PRAYER_TIMES = "prayer_times"
    const val QIBLA = "qibla"
    const val QURAN_INDEX = "quran_index"
    const val ADHKAR_CATEGORIES = "adhkar_categories"
    const val TASBIH = "tasbih"
    const val SETTINGS = "settings"

    const val QURAN_READER = "quran_reader/{startPage}"
    const val ADHKAR_LIST = "adhkar_list/{categoryName}"

    fun createQuranReaderRoute(startPage: Int): String {
        return "quran_reader/$startPage"
    }

    fun createAdhkarListRoute(categoryName: String): String {
        val encodedCategory = URLEncoder.encode(categoryName, StandardCharsets.UTF_8.toString())
        return "adhkar_list/$encodedCategory"
    }
}

sealed class BottomNavItem(val route: String, val icon: Int, val titleResId: Int) {
    object PrayerTimes : BottomNavItem(Routes.PRAYER_TIMES, R.drawable.ic_prayer_time, R.string.title_prayer_times)
    object Qibla : BottomNavItem(Routes.QIBLA, R.drawable.ic_qibla_compass, R.string.title_qibla)
    object Quran : BottomNavItem(Routes.QURAN_INDEX, R.drawable.ic_quran, R.string.title_quran)
    object Adhkar : BottomNavItem(Routes.ADHKAR_CATEGORIES, R.drawable.dua, R.string.title_adhkar)
    object Tasbih : BottomNavItem(Routes.TASBIH, R.drawable.tasbih, R.string.title_tasbih)
    object Settings : BottomNavItem(Routes.SETTINGS, R.drawable.ic_settings, R.string.title_settings)
}

@Composable
fun NavigationGraph(navController: NavHostController) {

    // The ViewModel is scoped to the NavHost, so all Quran screens can share it.
    val quranViewModel: QuranViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.PRAYER_TIMES) {

        composable(Routes.PRAYER_TIMES) { PrayerTimesScreen() }
        composable(Routes.QIBLA) { QiblaScreen() }
        composable(Routes.TASBIH) { TasbihScreen() }
        composable(Routes.SETTINGS) { SettingsScreen() }

        // Adhkar routes (unchanged)
        composable(Routes.ADHKAR_CATEGORIES) {
            AdhkarCategoriesScreen(
                onCategoryClick = { categoryName ->
                    navController.navigate(Routes.createAdhkarListRoute(categoryName))
                }
            )
        }
        composable(
            route = Routes.ADHKAR_LIST,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: ""
            AdhkarListScreen(categoryName = categoryName)
        }

        // --- THIS IS THE FIX ---
        // The logic is now simple, direct, and correct.
        composable(Routes.QURAN_INDEX) {
            SurahIndexScreen(
                quranViewModel = quranViewModel,
                // 1. The Index Screen gives us the correct startPage directly.
                onSurahClick = { startPage ->
                    // 2. We immediately navigate using that correct page number.
                    navController.navigate(Routes.createQuranReaderRoute(startPage))
                }
            )
        }

        composable(
            route = Routes.QURAN_READER,
            arguments = listOf(navArgument("startPage") { type = NavType.IntType })
        ) { backStackEntry ->
            // 3. The Reader Screen receives the correct page number.
            val startPage = backStackEntry.arguments?.getInt("startPage") ?: 1
            QuranReaderScreen(
                quranViewModel = quranViewModel,
                startPage = startPage
            )
        }
    }
}
