package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.AuthResponse
import com.example.quizmaster.data.model.LoginRequest
import com.example.quizmaster.data.model.RegisterRequest
import com.example.quizmaster.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * API service for user authentication and management matching Swagger spec
 */
interface AuthApiService {
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<User>
    
    @GET("users/profile")
    suspend fun getCurrentUser(): Response<User>
}
