package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.remote.LeaderboardEntry
import com.example.quizmaster.data.remote.QuizAttemptApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing quiz attempts and leaderboards
 */
class LeaderboardRepository(
    private val attemptApiService: QuizAttemptApiService
) {
    
    /**
     * Get quiz attempt by ID
     */
    suspend fun getAttemptById(attemptId: String): Result<QuizAttempt> = 
        withContext(Dispatchers.IO) {
            try {
                val response = attemptApiService.getAttemptById(attemptId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch attempt: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Get my attempts
     */
    suspend fun getMyAttempts(): Result<List<QuizAttempt>> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getMyAttempts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch attempts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get leaderboard for a specific quiz
     */
    suspend fun getQuizLeaderboard(
        quizId: String
    ): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getQuizLeaderboard(quizId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch leaderboard: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get student's rank for a specific quiz
     */
    suspend fun getMyRankForQuiz(
        quizId: String
    ): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getMyRank(quizId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch rank: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get global leaderboard
     */
    suspend fun getGlobalLeaderboard(): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getGlobalLeaderboard()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch global leaderboard: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Compare student's performance with others (local calculation)
     */
    fun compareWithLeaderboard(
        studentAttempt: QuizAttempt,
        leaderboard: List<LeaderboardEntry>
    ): ComparisonResult {
        val studentScore = studentAttempt.totalScore
        
        val betterThan = leaderboard.count { it.score < studentScore }
        val rank = leaderboard.indexOfFirst { it.student_id == studentAttempt.studentId } + 1
        val topScore = leaderboard.firstOrNull()?.score?.toInt() ?: 0
        val averageScore = if (leaderboard.isNotEmpty()) {
            leaderboard.map { it.score }.average()
        } else 0.0
        
        return ComparisonResult(
            studentScore = studentScore.toInt(),
            rank = rank,
            totalParticipants = leaderboard.size,
            betterThanCount = betterThan,
            topScore = topScore,
            averageScore = averageScore,
            percentile = if (leaderboard.isNotEmpty()) {
                (betterThan.toDouble() / leaderboard.size) * 100
            } else 0.0
        )
    }
}

/**
 * Data class for comparison results
 */
data class ComparisonResult(
    val studentScore: Int,
    val rank: Int,
    val totalParticipants: Int,
    val betterThanCount: Int,
    val topScore: Int,
    val averageScore: Double,
    val percentile: Double
) {
    fun getPerformanceMessage(): String {
        return when {
            rank == 1 -> "🏆 You're #1! Amazing!"
            rank <= 3 -> "🥇 Top 3! Excellent work!"
            percentile >= 75 -> "⭐ Top 25%! Great job!"
            percentile >= 50 -> "👍 Above average!"
            else -> "💪 Keep practicing!"
        }
    }
    
    fun getBetterThanMessage(): String {
        return "You performed better than $betterThanCount out of $totalParticipants students"
    }
}
