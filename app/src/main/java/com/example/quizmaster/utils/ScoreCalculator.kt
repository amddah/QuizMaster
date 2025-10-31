package com.example.quizmaster.utils

import com.example.quizmaster.data.model.QuestionModel
import com.example.quizmaster.data.model.Answer

/**
 * Utility class for calculating quiz scores based on response time
 */
object ScoreCalculator {
    
    // Time thresholds in seconds
    private const val FAST_RESPONSE_THRESHOLD = 5
    private const val MEDIUM_RESPONSE_THRESHOLD = 10
    private const val SLOW_RESPONSE_THRESHOLD = 15
    
    // Score multipliers
    private const val FAST_MULTIPLIER = 1.0     // 100% of max score
    private const val MEDIUM_MULTIPLIER = 0.7   // 70% of max score
    private const val SLOW_MULTIPLIER = 0.4     // 40% of max score
    
    // Experience points per question
    private const val BASE_XP = 10
    private const val FAST_BONUS_XP = 5
    private const val MEDIUM_BONUS_XP = 3
    
    /**
     * Calculate score for a single question based on correctness and response time
     */
    fun calculateQuestionScore(
        question: QuestionModel,
        isCorrect: Boolean,
        responseTimeSeconds: Int
    ): Int {
        if (!isCorrect) return 0
        
        val multiplier = getScoreMultiplier(responseTimeSeconds)
        return (question.maxScore * multiplier).toInt()
    }
    
    /**
     * Get score multiplier based on response time
     */
    fun getScoreMultiplier(responseTimeSeconds: Int): Double {
        return when {
            responseTimeSeconds <= FAST_RESPONSE_THRESHOLD -> FAST_MULTIPLIER
            responseTimeSeconds <= MEDIUM_RESPONSE_THRESHOLD -> MEDIUM_MULTIPLIER
            responseTimeSeconds <= SLOW_RESPONSE_THRESHOLD -> SLOW_MULTIPLIER
            else -> 0.0
        }
    }
    
    /**
     * Get response speed category
     */
    fun getResponseSpeed(responseTimeSeconds: Int): ResponseSpeed {
        return when {
            responseTimeSeconds <= FAST_RESPONSE_THRESHOLD -> ResponseSpeed.FAST
            responseTimeSeconds <= MEDIUM_RESPONSE_THRESHOLD -> ResponseSpeed.MEDIUM
            responseTimeSeconds <= SLOW_RESPONSE_THRESHOLD -> ResponseSpeed.SLOW
            else -> ResponseSpeed.TOO_SLOW
        }
    }
    
    /**
     * Calculate total score for a list of answers
     */
    fun calculateTotalScore(answers: List<Answer>): Int {
        return answers.sumOf { it.pointsEarned.toInt() }
    }
    
    /**
     * Calculate percentage score
     */
    fun calculatePercentage(totalScore: Int, maxPossibleScore: Int): Double {
        if (maxPossibleScore == 0) return 0.0
        return (totalScore.toDouble() / maxPossibleScore) * 100
    }
    
    /**
     * Calculate experience points earned
     */
    fun calculateExperiencePoints(
        correctAnswers: Int,
        totalQuestions: Int,
        averageResponseTime: Double
    ): Int {
        val baseXP = correctAnswers * BASE_XP
        
        val bonusXP = when {
            averageResponseTime <= FAST_RESPONSE_THRESHOLD -> correctAnswers * FAST_BONUS_XP
            averageResponseTime <= MEDIUM_RESPONSE_THRESHOLD -> correctAnswers * MEDIUM_BONUS_XP
            else -> 0
        }
        
        // Perfect score bonus
        val perfectBonus = if (correctAnswers == totalQuestions) 20 else 0
        
        return baseXP + bonusXP + perfectBonus
    }
    
    /**
     * Get performance tier based on score percentage
     */
    fun getPerformanceTier(percentage: Double): PerformanceTier {
        return when {
            percentage >= 90 -> PerformanceTier.EXCELLENT
            percentage >= 75 -> PerformanceTier.GREAT
            percentage >= 60 -> PerformanceTier.GOOD
            percentage >= 50 -> PerformanceTier.AVERAGE
            else -> PerformanceTier.NEEDS_IMPROVEMENT
        }
    }
    
    /**
     * Get badge for performance
     */
    fun getBadgeForPerformance(percentage: Double, responseSpeed: ResponseSpeed): String? {
        return when {
            percentage == 100.0 && responseSpeed == ResponseSpeed.FAST -> "🏆 Perfect Speed Master"
            percentage == 100.0 -> "⭐ Perfect Score"
            percentage >= 90 && responseSpeed == ResponseSpeed.FAST -> "🚀 Speed Champion"
            percentage >= 90 -> "🥇 Gold Medal"
            percentage >= 75 -> "🥈 Silver Medal"
            percentage >= 60 -> "🥉 Bronze Medal"
            else -> null
        }
    }
}

/**
 * Enum representing response speed categories
 */
enum class ResponseSpeed(val displayName: String, val emoji: String) {
    FAST("Lightning Fast", "⚡"),
    MEDIUM("Quick", "🏃"),
    SLOW("Careful", "🚶"),
    TOO_SLOW("Too Slow", "🐌");
    
    fun getDescription(): String {
        return when (this) {
            FAST -> "Answered in 5 seconds or less"
            MEDIUM -> "Answered within 10 seconds"
            SLOW -> "Answered within 15 seconds"
            TOO_SLOW -> "Took too long to answer"
        }
    }
}

/**
 * Enum representing performance tiers
 */
enum class PerformanceTier(val displayName: String, val emoji: String, val color: String) {
    EXCELLENT("Excellent", "🏆", "#FFD700"),
    GREAT("Great", "⭐", "#4CAF50"),
    GOOD("Good", "👍", "#2196F3"),
    AVERAGE("Average", "😊", "#FF9800"),
    NEEDS_IMPROVEMENT("Keep Practicing", "💪", "#F44336");
    
    fun getMessage(): String {
        return when (this) {
            EXCELLENT -> "Outstanding performance! You're a quiz master!"
            GREAT -> "Excellent work! Keep it up!"
            GOOD -> "Good job! You're on the right track!"
            AVERAGE -> "Not bad! Keep practicing to improve!"
            NEEDS_IMPROVEMENT -> "Don't give up! Practice makes perfect!"
        }
    }
}
