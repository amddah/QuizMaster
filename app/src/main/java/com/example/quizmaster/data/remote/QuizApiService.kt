package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.ApprovalStatus
import com.example.quizmaster.data.model.QuizModel
import retrofit2.Response
import retrofit2.http.*

/**
 * API service for quiz management
 */
interface QuizApiService {
    
    // Quiz CRUD operations
    
    @GET("api/quizzes")
    suspend fun getAllQuizzes(
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("status") status: ApprovalStatus? = null
    ): Response<List<QuizModel>>
    
    @GET("api/quizzes/{quizId}")
    suspend fun getQuizById(
        @Path("quizId") quizId: String
    ): Response<QuizModel>
    
    @POST("api/quizzes")
    suspend fun createQuiz(
        @Header("Authorization") token: String,
        @Body quiz: QuizModel
    ): Response<QuizModel>
    
    @PUT("api/quizzes/{quizId}")
    suspend fun updateQuiz(
        @Path("quizId") quizId: String,
        @Header("Authorization") token: String,
        @Body quiz: QuizModel
    ): Response<QuizModel>
    
    @DELETE("api/quizzes/{quizId}")
    suspend fun deleteQuiz(
        @Path("quizId") quizId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
    
    // Quiz approval operations (Professor only)
    
    @GET("api/quizzes/pending")
    suspend fun getPendingQuizzes(
        @Header("Authorization") token: String
    ): Response<List<QuizModel>>
    
    @POST("api/quizzes/{quizId}/approve")
    suspend fun approveQuiz(
        @Path("quizId") quizId: String,
        @Header("Authorization") token: String
    ): Response<QuizModel>
    
    @POST("api/quizzes/{quizId}/reject")
    suspend fun rejectQuiz(
        @Path("quizId") quizId: String,
        @Header("Authorization") token: String,
        @Body reason: Map<String, String>
    ): Response<QuizModel>
    
    // Student-specific operations
    
    @GET("api/students/{studentId}/available-quizzes")
    suspend fun getAvailableQuizzesForStudent(
        @Path("studentId") studentId: String,
        @Header("Authorization") token: String
    ): Response<List<QuizModel>>
    
    @GET("api/quizzes/created-by/{userId}")
    suspend fun getQuizzesCreatedByUser(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<List<QuizModel>>
}
