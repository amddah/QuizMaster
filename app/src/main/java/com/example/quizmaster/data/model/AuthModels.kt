package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for user login
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Request model for user registration
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("role")
    val role: UserRole,
    
    @SerializedName("department")
    val department: String? = null  // Only for professors
)

/**
 * Response model for authentication
 */
data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("token")
    val token: String? = null,
    
    @SerializedName("user")
    val user: User? = null
)
