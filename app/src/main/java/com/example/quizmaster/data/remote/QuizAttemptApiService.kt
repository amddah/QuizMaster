package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.QuizAttempt
import retrofit2.Response
import retrofit2.http.*

/**
 * Response for leaderboard entry
 */
data class LeaderboardEntry(
    val studentId: String,
    val studentName: String,
    val score: Int,
    val percentage: Double,
    val timeSeconds: Int,
    val rank: Int,
    val completedAt: Long
)

/**
 * Response for leaderboard
 */
data class LeaderboardResponse(
    val quizId: String,
    val quizTitle: String,
    val entries: List<LeaderboardEntry>,
    val totalAttempts: Int
)

/**
 * API service for quiz attempts and leaderboards
 */
interface QuizAttemptApiService {
    
    @POST("api/quiz-attempts")
    suspend fun submitQuizAttempt(
        @Header("Authorization") token: String,
        @Body attempt: QuizAttempt
    ): Response<QuizAttempt>
    
    @GET("api/quiz-attempts/{attemptId}")
    suspend fun getAttemptById(
        @Path("attemptId") attemptId: String
    ): Response<QuizAttempt>
    
    @GET("api/students/{studentId}/quiz-attempts")
    suspend fun getStudentAttempts(
        @Path("studentId") studentId: String,
        @Header("Authorization") token: String
    ): Response<List<QuizAttempt>>
    
    @GET("api/quizzes/{quizId}/leaderboard")
    suspend fun getQuizLeaderboard(
        @Path("quizId") quizId: String,
        @Query("limit") limit: Int = 50
    ): Response<LeaderboardResponse>
    
    @GET("api/students/{studentId}/rank/{quizId}")
    suspend fun getStudentRankForQuiz(
        @Path("studentId") studentId: String,
        @Path("quizId") quizId: String,
        @Header("Authorization") token: String
    ): Response<LeaderboardEntry>
}
