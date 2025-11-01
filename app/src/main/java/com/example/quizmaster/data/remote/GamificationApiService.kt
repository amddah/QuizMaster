package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.Achievement
import com.example.quizmaster.data.model.StudentStats
import retrofit2.Response
import retrofit2.http.*

/**
 * Request/Response models for gamification
 */
data class XpDetails(
    val base_xp: Int,
    val speed_bonus: Int,
    val accuracy_bonus: Int,
    val streak_bonus: Int,
    val difficulty_bonus: Int,
    val total_xp: Int,
    val new_level: Int,
    val leveled_up: Boolean
)

data class XpGainResponse(
    val attempt_id: String,
    val xp_details: XpDetails,
    val xp_earned: Int
)

data class BadgeUnlockResponse(
    val badge_type: String,
    val earned_at: Long,
    val is_new: Boolean = true
)

/**
 * API service for gamification features
 */
interface GamificationApiService {
    
    /**
     * Get user statistics and progress
     */
    @GET("users/stats")
    suspend fun getUserStats(): Response<StudentStats>
    
    /**
     * Get user achievements/badges
     */
    @GET("users/achievements")
    suspend fun getUserAchievements(): Response<List<Achievement>>
    
    /**
     * Get XP breakdown for a specific attempt
     */
    @GET("attempts/{id}/xp")
    suspend fun getAttemptXp(
        @Path("id") attemptId: String
    ): Response<XpGainResponse>
    
    /**
     * Get global rankings with pagination
     */
    @GET("leaderboards/global")
    suspend fun getGlobalLeaderboard(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<LeaderboardRankEntry>>
    
    /**
     * Get user's global rank and percentile
     */
    @GET("users/global-rank")
    suspend fun getGlobalRank(): Response<GlobalRankResponse>
    
    /**
     * Get category-specific statistics
     */
    @GET("users/category-stats")
    suspend fun getCategoryStats(): Response<List<CategoryStats>>
    
    /**
     * Get user's streak information
     */
    @GET("users/streak")
    suspend fun getStreakInfo(): Response<StreakInfo>
}

/**
 * Leaderboard entry with detailed information
 */
data class LeaderboardRankEntry(
    val rank: Int,
    val user_id: String,
    val user_name: String,
    val total_xp: Int,
    val level: Int,
    val total_quizzes: Int,
    val average_score: Double,
    val badges_count: Int,
    val avatar_url: String? = null
)

/**
 * Global rank response
 */
data class GlobalRankResponse(
    val rank: Int,
    val total_users: Int,
    val percentile: Double,
    val better_than: Int,
    val total_xp: Int,
    val level: Int
)

/**
 * Category statistics
 */
data class CategoryStats(
    val category: String,
    val quizzes_completed: Int,
    val average_score: Double,
    val total_xp: Int,
    val best_score: Double,
    val is_expert: Boolean = false
)

/**
 * Streak information
 */
data class StreakInfo(
    val current_streak: Int,
    val longest_streak: Int,
    val last_quiz_date: String?,
    val streak_active: Boolean,
    val days_until_break: Int
)
