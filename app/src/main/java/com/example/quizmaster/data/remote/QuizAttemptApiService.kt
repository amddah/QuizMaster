package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.LeaderboardEntry
import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.model.QuizLeaderboardResponse
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
 * Response wrapper for start attempt endpoint
 * Backend returns: {"attempt": {...}, "quiz": {...}}
 */
data class StartAttemptResponse(
    @SerializedName("attempt")
    val attempt: QuizAttempt,
    
    @SerializedName("quiz")
    val quiz: Any? = null // We don't need quiz data here
)

/**
 * Response wrapper for complete attempt endpoint
 * Backend returns: {"attempt": {...}, "new_badges": [...], "xp_reward": {...}}
 */
data class CompleteAttemptResponse(
    @SerializedName("attempt")
    val attempt: QuizAttempt,
    
    @SerializedName("new_badges")
    val newBadges: List<Any>? = null,
    
    @SerializedName("xp_reward")
    val xpReward: Any? = null
)

/* Leaderboard models are defined in data.model.QuizLeaderboardResponse and LeaderboardEntry */

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
    ): Response<StartAttemptResponse>
    
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
     * PUT /attempts/{id}/complete - Complete a quiz attempt
     * Note: Uses path parameter, returns nested response
     */
    @PUT("attempts/{id}/complete")
    suspend fun completeAttempt(
        @Path("id") attemptId: String
    ): Response<CompleteAttemptResponse>
    
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
    ): Response<QuizLeaderboardResponse>
    
    /**
     * GET /leaderboards/quiz/{quiz_id}/my-rank - Get authenticated student's rank
     */
    @GET("leaderboards/quiz/{quiz_id}/my-rank")
    suspend fun getMyRank(
        @Path("quiz_id") quizId: String
    ): Response<Map<String, Any>>
}
