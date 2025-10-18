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
    
    @SerializedName("first_name")
    val firstName: String,
    
    @SerializedName("last_name") 
    val lastName: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("role")
    val role: UserRole
)

/**
 * Response model for authentication
 */
data class AuthResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("user")
    val user: User
)
