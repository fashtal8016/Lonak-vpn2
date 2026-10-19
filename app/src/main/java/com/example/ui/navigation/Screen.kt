package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val titleResId: Int,
    val titlePersian: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", com.example.R.string.nav_home, "خانه", Icons.Filled.Home, Icons.Outlined.Home)
    object Servers : Screen("servers", com.example.R.string.nav_servers, "سرورها", Icons.Filled.Dns, Icons.Outlined.Dns)
    object Scanner : Screen("scanner", com.example.R.string.nav_scanner, "اسکنر", Icons.Filled.Radar, Icons.Outlined.Radar)
    object Logs : Screen("logs", com.example.R.string.nav_logs, "لاگ‌ها", Icons.Filled.Terminal, Icons.Outlined.Terminal)
    object Settings : Screen("settings", com.example.R.string.nav_settings, "تنظیمات", Icons.Filled.Settings, Icons.Outlined.Settings)

    companion object {
        val bottomNavItems = listOf(Home, Servers, Scanner, Logs, Settings)
    }
}
