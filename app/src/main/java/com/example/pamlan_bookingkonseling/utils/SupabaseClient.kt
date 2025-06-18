package com.example.pamlan_bookingkonseling.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object  SupabaseClient {
    val client = createSupabaseClient (
        supabaseUrl = "https://zuocmphphgaiqvlvqpfh.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp1b2NtcGhwaGdhaXF2bHZxcGZoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTAxNTcxNDgsImV4cCI6MjA2NTczMzE0OH0.HX23cTQNcRhgZ7-6eCfd-DDce6D1fOdHkT9XEPH9g-w"
    ) {
        install(Storage)
    }
}