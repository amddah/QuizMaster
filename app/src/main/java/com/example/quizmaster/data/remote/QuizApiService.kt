package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.QuizModel
import com.example.quizmaster.data.model.QuizApiModel
import retrofit2.Response
import retrofit2.http.*

/**
 * API service for quiz management matching Swagger spec
 */
interface QuizApiService {
    
    @GET("quizzes")
    suspend fun getAllQuizzes(
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("status") status: String? = null
    ): Response<List<QuizApiModel>>
    
    @GET("quizzes/{id}")
    suspend fun getQuizById(
        @Path("id") quizId: String
    ): Response<QuizApiModel>
    
    @POST("quizzes")
    suspend fun createQuiz(
        @Body quiz: QuizApiModel
    ): Response<QuizApiModel>

    @DELETE("quizzes/{id}")
    suspend fun deleteQuiz(
        @Path("id") quizId: String
    ): Response<Unit>
    
    @PUT("quizzes/{id}/approve")
    suspend fun approveQuiz(
        @Path("id") quizId: String
    ): Response<QuizApiModel>
}
