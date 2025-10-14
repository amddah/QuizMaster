package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.model.StudentStats
import com.example.quizmaster.data.remote.LeaderboardEntry
import com.example.quizmaster.data.remote.LeaderboardResponse
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
     * Submit a quiz attempt
     */
    suspend fun submitQuizAttempt(
        token: String,
        attempt: QuizAttempt
    ): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.submitQuizAttempt(token, attempt)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to submit attempt: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
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
     * Get all attempts by a student
     */
    suspend fun getStudentAttempts(
        studentId: String,
        token: String
    ): Result<List<QuizAttempt>> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getStudentAttempts(studentId, token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch student attempts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get leaderboard for a specific quiz
     */
    suspend fun getQuizLeaderboard(
        quizId: String,
        limit: Int = 50
    ): Result<LeaderboardResponse> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getQuizLeaderboard(quizId, limit)
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
    suspend fun getStudentRankForQuiz(
        studentId: String,
        quizId: String,
        token: String
    ): Result<LeaderboardEntry> = withContext(Dispatchers.IO) {
        try {
            val response = attemptApiService.getStudentRankForQuiz(studentId, quizId, token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch student rank: ${response.message()}"))
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
        leaderboard: LeaderboardResponse
    ): ComparisonResult {
        val studentScore = studentAttempt.totalScore
        val entries = leaderboard.entries
        
        val betterThan = entries.count { it.score < studentScore }
        val rank = entries.indexOfFirst { it.studentId == studentAttempt.studentId } + 1
        val topScore = entries.firstOrNull()?.score ?: 0
        val averageScore = if (entries.isNotEmpty()) {
            entries.map { it.score }.average()
        } else 0.0
        
        return ComparisonResult(
            studentScore = studentScore,
            rank = rank,
            totalParticipants = entries.size,
            betterThanCount = betterThan,
            topScore = topScore,
            averageScore = averageScore,
            percentile = if (entries.isNotEmpty()) {
                (betterThan.toDouble() / entries.size) * 100
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
