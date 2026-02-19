package com.statichumstudio.slipshell.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Servers(route = "servers", title = "Servers", icon = Icons.Filled.Storage),
    Terminal(route = "terminal", title = "Terminal", icon = Icons.Filled.Computer),
    Settings(route = "settings", title = "Settings", icon = Icons.Filled.Settings);

    companion object {
        val bottomNavItems = listOf(Servers, Terminal, Settings)
    }
}
