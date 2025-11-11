package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.*
import com.example.quizmaster.data.remote.AuthApiService
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.remote.ApiClient

/**
 * Repository for authentication operations with error handling
 */
class AuthRepository(
    private val authApiService: AuthApiService,
    private val sessionManager: UserSessionManager
) {
    
    /**
     * Login user and save session
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApiService.login(request)
            
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    // Save token and user session
                    sessionManager.saveSession(authResponse.token, authResponse.user)
                    // Ensure ApiClient has the token for subsequent requests
                    ApiClient.setAuthToken(authResponse.token)
                    Result.success(authResponse.user)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Register new user
     */
    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<User> {
        return try {
            val request = RegisterRequest(email, firstName, lastName, password, role)
            val response = authApiService.register(request)

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    // Save token and user session (backend returns token + user on register)
                    sessionManager.saveSession(authResponse.token, authResponse.user)
                    ApiClient.setAuthToken(authResponse.token)
                    Result.success(authResponse.user)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get current user profile
     */
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = authApiService.getCurrentUser()
            
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Result.success(user)
                } ?: Result.failure(Exception("Empty user data"))
            } else {
                Result.failure(Exception("Failed to get user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateUser(user: User): Result<User> {
        return try {
            // For updating, we'd need an update API endpoint, but it's not in the Swagger spec
            // So we'll just update locally for now
            sessionManager.updateUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Logout user and clear session
     */
    suspend fun logout(): Result<Unit> {
        return try {
            // Clear session
            sessionManager.clearSession()
            // Remove token from ApiClient
            ApiClient.setAuthToken(null)
            Result.success(Unit)
        } catch (e: Exception) {
            // Clear session even on error
            sessionManager.clearSession()
            ApiClient.setAuthToken(null)
            Result.success(Unit)
        }
    }
}
