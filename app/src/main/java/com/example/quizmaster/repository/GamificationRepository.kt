package com.example.quizmaster.repository

import com.example.quizmaster.data.model.Achievement
import com.example.quizmaster.data.model.StudentStats
import com.example.quizmaster.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for gamification features
 */
class GamificationRepository(private val apiService: GamificationApiService) {
    
    /**
     * Get user statistics
     */
    suspend fun getUserStats(): Result<StudentStats> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user stats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user achievements with full response
     */
    suspend fun getUserAchievements(): Result<UserAchievementsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserAchievements()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch achievements: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get XP breakdown for attempt
     */
    suspend fun getAttemptXp(attemptId: String): Result<XpGainResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAttemptXp(attemptId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch XP info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get global leaderboard
     */
    suspend fun getGlobalLeaderboard(page: Int = 1, limit: Int = 50): Result<List<LeaderboardRankEntry>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGlobalLeaderboard(page, limit)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch leaderboard: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Get user's global rank
     */
    suspend fun getGlobalRank(): Result<GlobalRankResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGlobalRank()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch global rank: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get category statistics
     */
    suspend fun getCategoryStats(): Result<List<CategoryStats>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCategoryStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch category stats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get streak information
     */
    suspend fun getStreakInfo(): Result<StreakInfo> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStreakInfo()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch streak info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
