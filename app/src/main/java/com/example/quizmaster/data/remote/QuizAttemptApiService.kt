package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.QuizAttempt
import retrofit2.Response
import retrofit2.http.*

/**
 * Request models for quiz attempts
 */
data class StartAttemptRequest(
    val quiz_id: String
)

data class SubmitAnswerRequest(
    val attempt_id: String,
    val question_id: String,
    val answer: String,
    val time_taken: Int
)

/**
 * Response models for leaderboards
 */
data class LeaderboardEntry(
    val student_id: String,
    val student_name: String,
    val score: Double,
    val percentage: Double,
    val time_taken: Int,
    val rank: Int,
    val completed_at: String
)

/**
 * API service for quiz attempts and leaderboards matching Swagger spec
 */
interface QuizAttemptApiService {
    
    @GET("attempts")
    suspend fun getMyAttempts(): Response<List<QuizAttempt>>
    
    @POST("attempts/start")
    suspend fun startAttempt(
        @Body request: StartAttemptRequest
    ): Response<QuizAttempt>
    
    @POST("attempts/answer")
    suspend fun submitAnswer(
        @Body request: SubmitAnswerRequest
    ): Response<Map<String, Any>>
    
    @GET("attempts/{id}")
    suspend fun getAttemptById(
        @Path("id") attemptId: String
    ): Response<QuizAttempt>
    
    @PUT("attempts/{id}/complete")
    suspend fun completeAttempt(
        @Path("id") attemptId: String
    ): Response<QuizAttempt>
    
    @GET("leaderboards/global")
    suspend fun getGlobalLeaderboard(): Response<List<Map<String, Any>>>
    
    @GET("leaderboards/quiz/{quiz_id}")
    suspend fun getQuizLeaderboard(
        @Path("quiz_id") quizId: String
    ): Response<List<LeaderboardEntry>>
    
    @GET("leaderboards/quiz/{quiz_id}/my-rank")
    suspend fun getMyRank(
        @Path("quiz_id") quizId: String
    ): Response<Map<String, Any>>
}
