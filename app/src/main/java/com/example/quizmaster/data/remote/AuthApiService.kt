package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.AuthResponse
import com.example.quizmaster.data.model.LoginRequest
import com.example.quizmaster.data.model.RegisterRequest
import com.example.quizmaster.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * API service for user authentication and management
 */
interface AuthApiService {
    
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<User>
    
    @PUT("api/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<User>
    
    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>
}
