package com.example.pamlan_bookingkonseling.data.model

import com.google.firebase.Timestamp

data class Booking(
    val id: String = "",
    val userId: String = "",
    val namaMahasiswa: String = "",
    val nimMahasiswa: String = "",
    val prodiMahasiswa: String = "",
    val nomorHP: String = "",
    val tanggal: Timestamp = Timestamp.now(),
    val sesi: String = "",
    val konselor: String = "",
    val status: String = "Pending", // Pending, Ongoing, Completed, Cancelled
    val ktmImageUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)