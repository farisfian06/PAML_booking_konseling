package com.example.pamlan_bookingkonseling.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pamlan_bookingkonseling.data.model.Booking
import com.example.pamlan_bookingkonseling.data.repository.BookingRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BookingUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class BookingViewModel (private val context: Context) : ViewModel() {
    private val repository = BookingRepository()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState

    init {
        loadUserBookings()
    }

    fun convertUriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            Log.d("DEBUG", "1. Mulai convert URI ke byte array")
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadUserBookings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                currentUser?.let { user ->
                    repository.getUserBookings(user.uid)
                        .onSuccess { bookings ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                bookings = bookings,
                                errorMessage = null
                            )
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Terjadi kesalahan"
                            )
                        }
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }

    fun createBooking(
        nama: String,
        nim: String,
        prodi: String,
        nomorHP: String,
        tanggal: Timestamp,
        sesi: String,
        konselor: String,
        ktmImageUri: Uri?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            if (ktmImageUri == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Silakan unggah gambar KTM terlebih dahulu"
                )
                return@launch
            }

            try {
                currentUser?.let { user ->
                    val booking = Booking(
                        userId = user.uid,
                        namaMahasiswa = nama,
                        nimMahasiswa = nim,
                        prodiMahasiswa = prodi,
                        nomorHP = nomorHP,
                        tanggal = tanggal,
                        sesi = sesi,
                        konselor = konselor
                    )


                    val ktmImageBytes = convertUriToByteArray(context, ktmImageUri)

                    if (ktmImageBytes != null) {
                        repository.createBookingWithKtm(booking, ktmImageBytes)
                            .onSuccess {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true
                                )
                                loadUserBookings()
                            }
                            .onFailure { e ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = e.message
                                )
                            }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Gambar KTM belum dipilih"
                        )
                    }
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }


//    fun createBooking(
//        nama: String,
//        nim: String,
//        prodi: String,
//        nomorHP: String,
//        tanggal: Timestamp,
//        sesi: String,
//        konselor: String
//    ) {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
//
//            try {
//                currentUser?.let { user ->
//                    val booking = Booking(
//                        userId = user.uid,
//                        namaMahasiswa = nama,
//                        nimMahasiswa = nim,
//                        prodiMahasiswa = prodi,
//                        nomorHP = nomorHP,
//                        tanggal = tanggal,
//                        sesi = sesi,
//                        konselor = konselor
//                    )
//
//                    repository.createBooking(booking)
//                        .onSuccess {
//                            _uiState.value = _uiState.value.copy(
//                                isLoading = false,
//                                isSuccess = true,
//                                errorMessage = null
//                            )
//                            loadUserBookings()
//                        }
//                        .onFailure { exception ->
//                            _uiState.value = _uiState.value.copy(
//                                isLoading = false,
//                                errorMessage = exception.message ?: "Gagal membuat booking"
//                            )
//                        }
//                } ?: run {
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        errorMessage = "User tidak ditemukan"
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
//                )
//            }
//        }
//    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                repository.updateBookingStatus(bookingId, "Cancelled")
                    .onSuccess {
                        loadUserBookings()
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = exception.message ?: "Gagal membatalkan booking"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }


}