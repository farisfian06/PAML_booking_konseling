package com.example.pamlan_bookingkonseling.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pamlan_bookingkonseling.R
import com.example.pamlan_bookingkonseling.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Observe login success
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            onLoginSuccess() // Panggil callback untuk navigasi ke MainScreen
        }
    }

    // Observe error messages
    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.clearError() // Bersihkan error setelah ditampilkan
        }
    }

    // Warna yang digunakan dalam desain Anda
    val primaryColor = Color(0xFF002843)
    val secondaryColor = Color(0xFF002843)
    val inputBackgroundColor = Color(0xFFE8F0FE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Konsol Image",
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TELEMEDICINE",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF1E3A5F),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Booking Konseling",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Spacer(modifier = Modifier.height(20.dp))

        // Input Email (sebelumnya NIM)
        Text(
            text = "Email", // Ubah label ke Email
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = primaryColor,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(inputBackgroundColor, RoundedCornerShape(8.dp)),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = secondaryColor,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = inputBackgroundColor,
                unfocusedContainerColor = inputBackgroundColor,
                disabledContainerColor = inputBackgroundColor,
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Input Password
        Text(
            text = "Password",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = primaryColor,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(inputBackgroundColor, RoundedCornerShape(8.dp)),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = secondaryColor,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = inputBackgroundColor,
                unfocusedContainerColor = inputBackgroundColor,
                disabledContainerColor = inputBackgroundColor,
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Login
        Button(
            onClick = { authViewModel.login(email, password) }, // Panggil fungsi login dari ViewModel
            enabled = !authState.isLoading, // Nonaktifkan tombol saat loading
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(2.dp, secondaryColor, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Color.White) // Tampilkan loading indicator
            } else {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        TextButton(
            onClick = onNavigateToRegister // Panggil callback untuk navigasi ke RegisterScreen
        ) {
            Text(
                "Belum punya akun? Daftar",
                color = Color(0xFF1E3A5F)
            )
        }
    }
}
