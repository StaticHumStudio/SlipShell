package com.statichumstudio.slipshell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.statichumstudio.slipshell.ui.navigation.Screen
import com.statichumstudio.slipshell.ui.screens.AddEditServerScreen
import com.statichumstudio.slipshell.ui.screens.ServersScreen
import com.statichumstudio.slipshell.ui.screens.SettingsScreen
import com.statichumstudio.slipshell.ui.screens.TerminalScreen
import com.statichumstudio.slipshell.ui.theme.SlipShellTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlipShellTheme {
                SlipShellMain()
            }
        }
    }
}

@Composable
fun SlipShellMain() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Hide bottom nav on add/edit screen
            if (currentRoute?.startsWith("addEditServer") != true) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination

                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Servers.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Servers.route) {
                ServersScreen(
                    onAddServer = { navController.navigate("addEditServer") },
                    onEditServer = { id -> navController.navigate("addEditServer?serverId=$id") },
                )
            }
            composable(Screen.Terminal.route) { TerminalScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(
                route = "addEditServer?serverId={serverId}",
                arguments = listOf(
                    navArgument("serverId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                ),
            ) { backStackEntry ->
                val serverId = backStackEntry.arguments?.getString("serverId")
                AddEditServerScreen(
                    serverId = serverId,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
