package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.QuizLeaderboardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API service for leaderboard operations
 */
interface LeaderboardApiService {
    
    /**
     * Get leaderboard for a specific quiz
     * GET /api/v1/leaderboards/quiz/{quiz_id}
     */
    @GET("leaderboards/quiz/{quiz_id}")
    suspend fun getQuizLeaderboard(
        @Path("quiz_id") quizId: String
    ): Response<QuizLeaderboardResponse>
    
    /**
     * Get global leaderboard (top 50 students)
     * GET /api/v1/leaderboards/global
     */
    @GET("leaderboards/global")
    suspend fun getGlobalLeaderboard(): Response<List<Map<String, Any>>>
}
