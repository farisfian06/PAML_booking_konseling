package com.example.pamlan_bookingkonseling.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pamlan_bookingkonseling.data.model.Booking
import com.example.pamlan_bookingkonseling.navigation.BottomNavItem
import com.example.pamlan_bookingkonseling.navigation.BottomNavigationBar
import com.example.pamlan_bookingkonseling.navigation.MainAppScreen
import com.example.pamlan_bookingkonseling.presentation.viewmodel.AuthViewModel
import com.example.pamlan_bookingkonseling.presentation.viewmodel.BookingViewModel

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    authViewModel: AuthViewModel,
    onNavigateToBookingDetail: (Booking) -> Unit
) {
    // NavController internal untuk Bottom Navigation
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // PERBAIKAN: Gunakan route yang konsisten dari sealed class
    val bottomNavItems = listOf(
        BottomNavItem(MainAppScreen.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(MainAppScreen.History.route, Icons.Default.Menu, "History"),
        BottomNavItem(MainAppScreen.Profile.route, Icons.Default.Person, "Profile")
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                navController = bottomNavController,
                onItemClick = { route ->
                    // PERBAIKAN: Tambahkan error handling dan logging
                    try {
                        bottomNavController.navigate(route) {
                            // Hindari multiple copies dari destination yang sama
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        println("Navigation error: ${e.message}")
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = MainAppScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainAppScreen.Home.route) {
                HomeScreen(mainNavController = mainNavController)
            }
            composable(MainAppScreen.History.route) {
                BookingHistoryScreen(
                    onNavigateToDetail = onNavigateToBookingDetail
                )
            }
            composable(MainAppScreen.Profile.route) {
                ProfileScreen(authViewModel = authViewModel)
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
