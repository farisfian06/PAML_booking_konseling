package com.example.pamlan_bookingkonseling.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SupabaseService() {

//    private val storage by lazy {
//        try {
//            SupabaseClient.client.storage
//        } catch (e: Exception) {
//            Log.e("SupabaseService", "Failed to initialize storage", e)
//            throw e
//        }
//    }
    private val storage = SupabaseClient.client.storage
    private val bucketName = "ktm-images"

    suspend fun uploadKtmImage(imageBytes: ByteArray, userId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("UPLOAD", "Mulai upload gambar ke Supabase")
            val fileName = "$userId/${UUID.randomUUID()}.jpg"
            val bucket = storage.from("ktm-images")

            Log.d("UPLOAD", "Berhasil mendapatkan bucket")
            bucket.upload(path = fileName, data = imageBytes) {
                upsert = true
            }
            val publicUrl = bucket.publicUrl(fileName)

            Log.d("UPLOAD", "Berhasil upload. URL: $publicUrl")

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun uploadKtmImage(imageUri: Uri, userId: String): Result<String> {
//        return try {
//            withContext(Dispatchers.IO) {
//                val inputStream = context.contentResolver.openInputStream(imageUri)
//                    ?: return@withContext Result.failure(Exception("Could not open input stream for image URI."))
//
//                val fileBytes = inputStream.readBytes()
//                inputStream.close()
//
//                val fileName = "$userId/${UUID.randomUUID()}.jpg"
//
//                storage.from(bucketName).upload(fileName, fileBytes)
//
//                val publicUrl = storage.from(bucketName).publicUrl(fileName)
//
//                println("KTM image uploaded successfully: $publicUrl")
//                Result.success(publicUrl)
//            }
//        } catch (e: Exception) {
//            println("Upload KTM image error: ${e.message}")
//            Result.failure(e)
//        }
//    }
}
