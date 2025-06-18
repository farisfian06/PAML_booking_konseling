package com.example.pamlan_bookingkonseling.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.pamlan_bookingkonseling.data.model.Booking
import com.example.pamlan_bookingkonseling.utils.SupabaseService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class BookingRepository() {
    private val firestore = FirebaseFirestore.getInstance()
    // Lazy initialization untuk supabase service
    private val supabaseService = SupabaseService()

    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            // Generate booking ID
            val bookingId = firestore.collection("bookings").document().id
            val bookingData = booking.copy(id = bookingId)

            // Simpan booking ke Firestore
            firestore.collection("bookings")
                .document(bookingId)
                .set(bookingData)
                .await()

            println("Booking created successfully with ID: $bookingId")
            Result.success(bookingId)
        } catch (e: Exception) {
            println("Create booking error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createBookingWithKtm(booking: Booking, imageBytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.e("CREATE", "1. Mulai create booking")
            val imageUploadResult = supabaseService.uploadKtmImage(imageBytes, booking.userId)
            Log.e("CREATE", "berhasil up image")

            imageUploadResult.fold(
                onSuccess = { publicUrl ->
                    val bookingWithKtm = booking.copy(ktmImageUrl = publicUrl)
                    firestore.collection("bookings").document(booking.id).set(bookingWithKtm).await()
                    Result.success(publicUrl)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun createBooking(booking: Booking, ktmImageUri: Uri?): Result<String> {
//        return withContext(Dispatchers.IO) {
//        try {
//            val bookingId = firestore.collection("bookings").document().id
//            var bookingToSave = booking.copy(id = bookingId)
//
//            if (ktmImageUri != null) {
//                val uploadResult = supabaseService.uploadKtmImage(ktmImageUri, booking.userId)
//
//                uploadResult.fold(
//                    onSuccess = { imageUrl ->
//                        bookingToSave = bookingToSave.copy(ktmImageUrl = imageUrl)
//                    },
//                    onFailure = { exception ->
//                        println("Failed to upload KTM image: ${exception.message}")
//                        return Result.failure(exception)
//                    }
//                )
//            }
//
//            firestore.collection("bookings")
//                .document(bookingId)
//                .set(bookingToSave)
//                .await()
//
//            println("Booking created successfully with ID: $bookingId")
//            Result.success(bookingId)
//
//        } catch (e: Exception) {
//            println("Create booking error: ${e.message}")
//            Result.failure(e)
//        }
//        }
//    }


    suspend fun getUserBookings(userId: String): Result<List<Booking>> {
        return try {
            val querySnapshot = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Booking::class.java)
                } catch (e: Exception) {
                    println("Error parsing booking: ${e.message}")
                    null
                }
            }

            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .update("status", status)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBooking(booking: Booking): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(booking.id)
                .set(booking)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBooking(bookingId: String): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookingById(bookingId: String): Result<Booking?> {
        return try {
            val document = firestore.collection("bookings")
                .document(bookingId)
                .get()
                .await()

            val booking = document.toObject(Booking::class.java)
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}