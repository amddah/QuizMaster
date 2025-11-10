package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Leaderboard entry for a quiz
 */
data class LeaderboardEntry(
    @SerializedName("rank")
    val rank: Int,
    
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
    val timeTaken: Int, // in seconds
    
    @SerializedName("completed_at")
    val completedAt: String
)

/**
 * Response wrapper for quiz leaderboard
 */
data class QuizLeaderboardResponse(
    @SerializedName("leaderboard")
    val leaderboard: List<LeaderboardEntry>,
    
    @SerializedName("quiz_id")
    val quizId: String,
    
    @SerializedName("total_count")
    val totalCount: Int
)

/**
 * Quiz with attempt count for professor view
 */
data class QuizWithAttempts(
    val quiz: QuizApiModel,
    val attemptCount: Int = 0
)
