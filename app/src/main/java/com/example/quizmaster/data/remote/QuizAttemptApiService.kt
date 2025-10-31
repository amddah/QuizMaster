package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.QuizAttempt
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

/**
 * Request models for quiz attempts
 */
data class StartAttemptRequest(
    @SerializedName("quiz_id")
    val quizId: String
)

data class SubmitAnswerRequest(
    @SerializedName("attempt_id")
    val attemptId: String,
    
    @SerializedName("question_id")
    val questionId: String,
    
    @SerializedName("answer")
    val answer: String,
    
    @SerializedName("time_to_answer")
    val timeToAnswer: Int
)

data class CompleteAttemptRequest(
    @SerializedName("id")
    val id: String
)

/**
 * Response models for leaderboards
 */
data class LeaderboardEntry(
    @SerializedName("student_id")
    val studentId: String,
    
    @SerializedName("student_name")
    val studentName: String,
    
    @SerializedName("score")
    val score: Double,
    
    @SerializedName("max_score")
    val maxScore: Double,
    
    @SerializedName("percentage")
    val percentage: Double,
    
    @SerializedName("time_taken")
    val timeTaken: Int,
    
    @SerializedName("rank")
    val rank: Int,
    
    @SerializedName("completed_at")
    val completedAt: String
)

/**
 * API service for quiz attempts and leaderboards matching Swagger spec
 */
interface QuizAttemptApiService {
    
    /**
     * GET /attempts - Get all quiz attempts by the authenticated student
     */
    @GET("attempts")
    suspend fun getMyAttempts(): Response<List<QuizAttempt>>
    
    /**
     * POST /attempts/start - Start attempting a quiz
     */
    @POST("attempts/start")
    suspend fun startAttempt(
        @Body request: StartAttemptRequest
    ): Response<QuizAttempt>
    
    /**
     * POST /attempts/answer - Submit an answer for a specific question
     */
    @POST("attempts/answer")
    suspend fun submitAnswer(
        @Body request: SubmitAnswerRequest
    ): Response<Map<String, Any>>
    
    /**
     * GET /attempts/{id} - Get details of a specific quiz attempt
     */
    @GET("attempts/{id}")
    suspend fun getAttemptById(
        @Path("id") attemptId: String
    ): Response<QuizAttempt>
    
    /**
     * PUT /attempts/complete - Complete a quiz attempt
     * Note: Uses request body, not path parameter
     */
    @PUT("attempts/complete")
    suspend fun completeAttempt(
        @Body request: CompleteAttemptRequest
    ): Response<QuizAttempt>
    
    /**
     * GET /attempts/{id}/xp - Get detailed XP breakdown for an attempt
     */
    @GET("attempts/{id}/xp")
    suspend fun getAttemptXpDetails(
        @Path("id") attemptId: String
    ): Response<Map<String, Any>>
    
    /**
     * GET /leaderboards/global - Get the top performing students across all quizzes
     */
    @GET("leaderboards/global")
    suspend fun getGlobalLeaderboard(): Response<List<Map<String, Any>>>
    
    /**
     * GET /leaderboards/quiz/{quiz_id} - Get the leaderboard for a specific quiz
     */
    @GET("leaderboards/quiz/{quiz_id}")
    suspend fun getQuizLeaderboard(
        @Path("quiz_id") quizId: String
    ): Response<List<LeaderboardEntry>>
    
    /**
     * GET /leaderboards/quiz/{quiz_id}/my-rank - Get authenticated student's rank
     */
    @GET("leaderboards/quiz/{quiz_id}/my-rank")
    suspend fun getMyRank(
        @Path("quiz_id") quizId: String
    ): Response<Map<String, Any>>
}
