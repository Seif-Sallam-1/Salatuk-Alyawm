// In: ui/MainScreen.kt
package com.seif.salatukalyawm // Or your package

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class) // Opt-in for Accompanist Permissions
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // --- هذا هو الجزء الجديد لطلب إذن الإشعارات ---
    // تحقق مما إذا كان إصدار أندرويد 13 (TIRAMISU) أو أعلى
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // تذكر حالة إذن الإشعارات
        val notificationPermissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )

        // اطلب الإذن عند فتح الشاشة لأول مرة إذا لم يتم منحه
        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }
    // --- نهاية الجزء الجديد ---


    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    BottomNavItem.PrayerTimes,
                    BottomNavItem.Qibla,
                    BottomNavItem.Quran,
                    BottomNavItem.Adhkar,
                    BottomNavItem.Tasbih,
                    BottomNavItem.Settings
                )

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                        label = { Text(stringResource(id = screen.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController)
        }
    }
}
