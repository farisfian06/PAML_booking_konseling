package com.example.pamlan_bookingkonseling.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookingkonseling.presentation.screen.RegisterScreen
import com.example.pamlan_bookingkonseling.data.model.Booking
import com.example.pamlan_bookingkonseling.presentation.screen.BookingDetailScreen
import com.example.pamlan_bookingkonseling.presentation.screen.BookingHistoryScreen
import com.example.pamlan_bookingkonseling.presentation.screen.CreateBookingScreen
import com.example.pamlan_bookingkonseling.presentation.screen.LoginScreen
import com.example.pamlan_bookingkonseling.presentation.screen.MainScreen
import com.example.pamlan_bookingkonseling.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavigate(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()

    // State untuk menyimpan booking yang dipilih
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    // PERBAIKAN: Tampilkan loading saat initial check
    if (!authState.isInitialized) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1E3A5F)
            )
        }
        return
    }

    // Effect untuk menangani navigasi otomatis setelah status auth berubah
    LaunchedEffect(authState.isLoggedIn) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        if (authState.isLoggedIn) {
            // User logged in, navigate to MainApp
            if (currentRoute != AppScreen.MainApp.route) {
                navController.navigate(AppScreen.MainApp.route) {
                    popUpTo(navController.graph.id) { // Hapus semua dari back stack
                        inclusive = true
                    }
                }
            }
        } else {
            // User logged out or not logged in, navigate to Login
            if (currentRoute != AppScreen.Login.route && currentRoute != AppScreen.Register.route) {
                navController.navigate(AppScreen.Login.route) {
                    popUpTo(navController.graph.id) { // Hapus semua dari back stack
                        inclusive = true
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppScreen.Register.route)
                },
                onLoginSuccess = {
                    // Navigasi otomatis akan ditangani oleh LaunchedEffect di atas
                },
                authViewModel = authViewModel
            )
        }
        composable(AppScreen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Navigasi otomatis akan ditangani oleh LaunchedEffect di atas
                },
                authViewModel = authViewModel
            )
        }
        composable(AppScreen.MainApp.route) {
            MainScreen(
                mainNavController = navController,
                authViewModel = authViewModel,
                onNavigateToBookingDetail = { booking ->
                    // Simpan booking yang dipilih dan navigate ke detail
                    selectedBooking = booking
                    navController.navigate(AppScreen.BookingDetail.route)
                }
            )
        }
        composable(AppScreen.CreateBooking.route) {
            CreateBookingScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppScreen.BookingDetail.route) {
            selectedBooking?.let { booking ->
                BookingDetailScreen(
                    booking = booking,
                    onNavigateBack = {
                        selectedBooking = null // Clear state
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Register : AppScreen("register")
    object MainApp : AppScreen("main_app") // Rute untuk MainScreen (dengan BottomNav)
    object CreateBooking : AppScreen("create_booking")
    object BookingDetail : AppScreen("booking_detail")
    // Tambahkan rute lain yang tidak termasuk dalam bottom navigation di sini
}

// Rute untuk tab di dalam MainScreen (jika perlu direferensikan di luar MainScreen)
// Atau bisa juga didefinisikan langsung di MainScreen jika hanya digunakan di sana
sealed class MainAppScreen(val route: String) {
    object Home : MainAppScreen("home_tab")
    object History : MainAppScreen("history_tab")
    object Profile : MainAppScreen("profile_tab")
}