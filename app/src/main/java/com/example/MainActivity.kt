package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.data.local.AppDatabase
import com.example.core.data.preferences.AppPreferences
import com.example.core.data.repository.ServerRepository
import com.example.core.scanner.ServerScraper
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.logs.LogsScreen
import com.example.ui.logs.LogsViewModel
import com.example.ui.navigation.Screen
import com.example.ui.scanner.ScannerScreen
import com.example.ui.scanner.ScannerViewModel
import com.example.ui.servers.ServersScreen
import com.example.ui.servers.ServersViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LonakTheme
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise

class MainActivity : ComponentActivity() {

    private lateinit var repository: ServerRepository
    private lateinit var homeViewModel: HomeViewModel

    private val vpnPrepareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            homeViewModel.onVpnPermissionGranted(this)
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission granted/denied handled gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize core dependencies
        val db = AppDatabase.getInstance(applicationContext)
        val prefs = AppPreferences(applicationContext)
        val scraper = ServerScraper(db.serverDao(), db.logDao())
        repository = ServerRepository(db.serverDao(), db.logDao(), prefs, scraper)
        homeViewModel = HomeViewModel(repository)

        val serversViewModel = ServersViewModel(repository)
        val scannerViewModel = ScannerViewModel(scraper)
        val settingsViewModel = SettingsViewModel(repository)
        val logsViewModel = LogsViewModel(repository)

        val initialAction = intent?.action

        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val layoutDirection = if (settings.appLanguage == "en") LayoutDirection.Ltr else LayoutDirection.Rtl

            LonakTheme(darkTheme = settings.isDarkMode) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    LonakApp(
                        homeViewModel = homeViewModel,
                        serversViewModel = serversViewModel,
                        scannerViewModel = scannerViewModel,
                        settingsViewModel = settingsViewModel,
                        logsViewModel = logsViewModel,
                        initialAction = initialAction,
                        onPrepareVpn = { prepareIntent ->
                            vpnPrepareLauncher.launch(prepareIntent)
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShortcutAction(intent.action)
    }

    private fun handleShortcutAction(action: String?) {
        when (action) {
            "ir.lonak.vpn.CONNECT_FAST" -> {
                homeViewModel.quickConnect(this) { prepareIntent ->
                    vpnPrepareLauncher.launch(prepareIntent)
                }
            }
        }
    }
}

@Composable
fun LonakApp(
    homeViewModel: HomeViewModel,
    serversViewModel: ServersViewModel,
    scannerViewModel: ScannerViewModel,
    settingsViewModel: SettingsViewModel,
    logsViewModel: LogsViewModel,
    initialAction: String?,
    onPrepareVpn: (Intent) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    LaunchedEffect(initialAction) {
        when (initialAction) {
            "ir.lonak.vpn.SCAN_SERVERS" -> navController.navigate(Screen.Scanner.route) { launchSingleTop = true }
            "ir.lonak.vpn.MY_SERVERS" -> navController.navigate(Screen.Servers.route) { launchSingleTop = true }
            "ir.lonak.vpn.SETTINGS" -> navController.navigate(Screen.Settings.route) { launchSingleTop = true }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                for (screen in Screen.bottomNavItems) {
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.titlePersian
                            )
                        },
                        label = {
                            Text(
                                text = screen.titlePersian,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = PersianTurquoise,
                            indicatorColor = PersianTurquoise,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToServers = { navController.navigate(Screen.Servers.route) },
                        onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                        onPrepareVpn = onPrepareVpn
                    )
                }

                composable(Screen.Servers.route) {
                    ServersScreen(
                        viewModel = serversViewModel,
                        onServerSelected = { navController.navigate(Screen.Home.route) }
                    )
                }

                composable(Screen.Scanner.route) {
                    ScannerScreen(viewModel = scannerViewModel)
                }

                composable(Screen.Logs.route) {
                    LogsScreen(viewModel = logsViewModel)
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }
}
