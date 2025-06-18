package com.example.pamlan_bookingkonseling.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(MainAppScreen.Home.route, Icons.Default.Home, "Home"),
    BottomNavItem(MainAppScreen.History.route, Icons.Default.Menu, "History"),
    BottomNavItem(MainAppScreen.Profile.route, Icons.Default.Person, "Profile")
)
