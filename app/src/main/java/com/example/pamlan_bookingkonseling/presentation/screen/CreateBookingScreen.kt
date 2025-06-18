package com.example.pamlan_bookingkonseling.presentation.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.pamlan_bookingkonseling.R
import com.example.pamlan_bookingkonseling.presentation.viewmodel.BookingViewModel
import com.google.firebase.Timestamp
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val viewModel = remember { BookingViewModel(context) }

    // State untuk form
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var nomorHP by remember { mutableStateOf("") }
    var tanggalString by remember { mutableStateOf("") }
    var selectedSesi by remember { mutableStateOf("") }
    var selectedKonselor by remember { mutableStateOf("") }

    var selectedKtmImageUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk dropdown
    var expandedSesi by remember { mutableStateOf(false) }
    var expandedKonselor by remember { mutableStateOf(false) }

    val sesiOptions = listOf(
        "Sesi 1 (10.00 - 11.00)",
        "Sesi 2 (11.00 - 12.00)",
        "Sesi 3 (13.00 - 14.00)",
        "Sesi 4 (14.00 - 15.00)"
    )

    val konselorOptions = listOf(
        "Dr. Konselor A",
        "Dr. Konselor B",
        "Dr. Konselor C",
        "Dr. Konselor D"
    )

    // Launcher untuk memilih gambar dari galeri
    val pickKtmImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedKtmImageUri = uri
    }

    // Observe UI state
    val uiState by viewModel.uiState.collectAsState()

    // Jika berhasil create booking, kembali
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Buat Reservasi",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A5F)
                )
            )
        },
        containerColor = Color(0xFF1E3A5F)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Orange Warning Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF9800))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info, // Using a built-in Material icon
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mohon isi data di bawah ini dengan benar dan lengkap",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E3A5F))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Form Fields
                CustomTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = "Nama Mahasiswa",
                    placeholder = "Masukkan nama lengkap",
                    enabled = !uiState.isLoading
                )

                CustomTextField(
                    value = nim,
                    onValueChange = { nim = it },
                    label = "NIM Mahasiswa",
                    placeholder = "Masukkan NIM",
                    enabled = !uiState.isLoading
                )

                CustomTextField(
                    value = prodi,
                    onValueChange = { prodi = it },
                    label = "Program Studi",
                    placeholder = "Masukkan nama program studi",
                    enabled = !uiState.isLoading
                )

                CustomTextField(
                    value = nomorHP,
                    onValueChange = { nomorHP = it },
                    label = "Nomor HP",
                    placeholder = "Masukkan nomor HP aktif",
                    enabled = !uiState.isLoading
                )

                CustomTextField(
                    value = tanggalString,
                    onValueChange = { tanggalString = it },
                    label = "Pilih Tanggal",
                    placeholder = "Contoh: Senin, 10/9/2025",
                    enabled = !uiState.isLoading
                )

                CustomDropdown(
                    value = selectedSesi,
                    onValueChange = { selectedSesi = it },
                    label = "Pilih Sesi",
                    options = sesiOptions,
                    expanded = expandedSesi,
                    onExpandedChange = { expandedSesi = it },
                    placeholder = "Pilih waktu sesi konseling",
                    enabled = !uiState.isLoading
                )

                CustomDropdown(
                    value = selectedKonselor,
                    onValueChange = { selectedKonselor = it },
                    label = "Pilih Konselor",
                    options = konselorOptions,
                    expanded = expandedKonselor,
                    onExpandedChange = { expandedKonselor = it },
                    placeholder = "Pilih konselor yang diinginkan",
                    enabled = !uiState.isLoading
                )

                // Unggah KTM Section
                Column {
                    Text(
                        text = "Unggah KTM",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = {
                            pickKtmImageLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF60A5FA), // Warna tombol yang berbeda
                            disabledContainerColor = Color(0xFF60A5FA).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = if (selectedKtmImageUri == null) "Pilih Gambar KTM" else "Ganti Gambar KTM",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    selectedKtmImageUri?.let { uri ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Preview KTM Image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Transparent)
                            )
                        }
                    } ?: run {
                        // Placeholder jika belum ada gambar yang dipilih
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Pratinjau Gambar KTM",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Info Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x20FFB74D)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Informasi Penting:",
                            color = Color(0xFFFFCC80),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Pastikan data yang diisi sesuai dengan identitas Anda\n" +
                                    "• Reservasi akan dikonfirmasi dalam 1x24 jam\n" +
                                    "• Harap datang tepat waktu sesuai jadwal yang dipilih\n" +
                                    "• Jika berhalangan hadir, segera hubungi konselor",
                            color = Color(0xFFFFCC80),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        val currentDate = Date()
                        val timestamp = Timestamp(currentDate)

                        viewModel.createBooking(
                            nama = nama,
                            nim = nim,
                            prodi = prodi,
                            nomorHP = nomorHP,
                            tanggal = timestamp,
                            sesi = selectedSesi,
                            konselor = selectedKonselor,
                            ktmImageUri = selectedKtmImageUri
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        disabledContainerColor = Color(0xFFFF9800).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isLoading &&
                            nama.isNotEmpty() &&
                            nim.isNotEmpty() &&
                            prodi.isNotEmpty() &&
                            nomorHP.isNotEmpty() &&
                            tanggalString.isNotEmpty() &&
                            selectedSesi.isNotEmpty() &&
                            selectedKonselor.isNotEmpty()
                ) {
                    if (uiState.isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Menyimpan...",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Text(
                            "Kirim Reservasi",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                // Error message
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x20FF5722)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFFAB91),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = Color(0xFFFFAB91),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Add some bottom padding
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Color(0xFF60A5FA),
                    fontSize = 16.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White.copy(alpha = 0.6f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color(0xFF60A5FA),
                disabledIndicatorColor = Color(0xFF60A5FA).copy(alpha = 0.6f),
                cursorColor = Color.White
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            OutlinedTextField(
                value = value.ifEmpty { placeholder },
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (value.isEmpty()) Color(0xFF60A5FA) else Color.White,
                    unfocusedTextColor = if (value.isEmpty()) Color(0xFF60A5FA) else Color.White,
                    disabledTextColor = Color.White.copy(alpha = 0.6f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color(0xFF60A5FA),
                    disabledBorderColor = Color(0xFF60A5FA).copy(alpha = 0.6f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (enabled) {
                                onExpandedChange(!expanded)
                            }
                        },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                enabled = enabled
            )

            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}
