package com.example.quizmaster

import android.app.Application
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.remote.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Restore saved auth token (if any) on app startup so ApiClient includes Authorization header
        val sessionManager = UserSessionManager.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = sessionManager.authToken.first()
                ApiClient.setAuthToken(token)
            } catch (t: Throwable) {
                // ignore - no token available or DataStore error
            }
        }
    }
}

