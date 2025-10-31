package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Performance tier for quiz results
 */
enum class PerformanceTier(val minPercentage: Int, val maxPercentage: Int, val emoji: String, val message: String) {
    EXCELLENT(90, 100, "🏆", "Outstanding performance!"),
    GREAT(75, 89, "⭐", "Excellent work!"),
    GOOD(60, 74, "👍", "Good job!"),
    AVERAGE(50, 59, "😊", "Not bad!"),
    NEEDS_IMPROVEMENT(0, 49, "💪", "Keep practicing!");
    
    companion object {
        fun fromPercentage(percentage: Double): PerformanceTier {
            return when {
                percentage >= 90 -> EXCELLENT
                percentage >= 75 -> GREAT
                percentage >= 60 -> GOOD
                percentage >= 50 -> AVERAGE
                else -> NEEDS_IMPROVEMENT
            }
        }
    }
    
    fun getColor(): Long {
        return when (this) {
            EXCELLENT -> 0xFFFFD700 // Gold
            GREAT -> 0xFF4CAF50 // Green
            GOOD -> 0xFF2196F3 // Blue
            AVERAGE -> 0xFFFF9800 // Orange
            NEEDS_IMPROVEMENT -> 0xFFF44336 // Red
        }
    }
}

/**
 * XP calculation constants
 */
object XpConstants {
    const val BASE_XP_PER_CORRECT = 10
    const val FAST_RESPONSE_BONUS = 5      // < 3 seconds
    const val MEDIUM_RESPONSE_BONUS = 3    // 3-5 seconds
    const val PERFECT_SCORE_BONUS = 20
    const val XP_PER_LEVEL = 100
    
    /**
     * Calculate XP for an answer
     */
    fun calculateAnswerXp(isCorrect: Boolean, timeToAnswer: Int): Int {
        if (!isCorrect) return 0
        
        var xp = BASE_XP_PER_CORRECT
        
        // Add time bonus
        xp += when {
            timeToAnswer < 3 -> FAST_RESPONSE_BONUS
            timeToAnswer <= 5 -> MEDIUM_RESPONSE_BONUS
            else -> 0
        }
        
        return xp
    }
    
    /**
     * Calculate level from total XP
     */
    fun calculateLevel(totalXp: Int): Int {
        return (totalXp / XP_PER_LEVEL) + 1
    }
    
    /**
     * Calculate XP needed for next level
     */
    fun xpNeededForNextLevel(currentXp: Int): Int {
        val currentLevel = calculateLevel(currentXp)
        val xpForNextLevel = currentLevel * XP_PER_LEVEL
        return xpForNextLevel - currentXp
    }
    
    /**
     * Calculate progress percentage in current level
     */
    fun levelProgress(currentXp: Int): Int {
        val xpInCurrentLevel = currentXp % XP_PER_LEVEL
        return (xpInCurrentLevel * 100) / XP_PER_LEVEL
    }
}

/**
 * Badge types for gamification
 */
enum class BadgeType {
    FIRST_QUIZ,
    SPEED_DEMON,
    PERFECT_SCORE,
    STREAK_5,
    STREAK_10,
    QUIZ_MASTER,
    CATEGORY_EXPERT,
    LEVEL_10,
    LEVEL_25,
    LEVEL_50;
    
    fun getDisplayName(): String {
        return when (this) {
            FIRST_QUIZ -> "First Steps"
            SPEED_DEMON -> "Speed Demon"
            PERFECT_SCORE -> "Perfect Score"
            STREAK_5 -> "5-Day Streak"
            STREAK_10 -> "10-Day Streak"
            QUIZ_MASTER -> "Quiz Master"
            CATEGORY_EXPERT -> "Category Expert"
            LEVEL_10 -> "Level 10"
            LEVEL_25 -> "Level 25"
            LEVEL_50 -> "Level 50"
        }
    }
    
    fun getEmoji(): String {
        return when (this) {
            FIRST_QUIZ -> "🎓"
            SPEED_DEMON -> "⚡"
            PERFECT_SCORE -> "💯"
            STREAK_5 -> "🔥"
            STREAK_10 -> "🔥🔥"
            QUIZ_MASTER -> "👑"
            CATEGORY_EXPERT -> "🌟"
            LEVEL_10 -> "🥉"
            LEVEL_25 -> "🥈"
            LEVEL_50 -> "🥇"
        }
    }
    
    fun getDescription(): String {
        return when (this) {
            FIRST_QUIZ -> "Complete your first quiz"
            SPEED_DEMON -> "Answer 10 questions in under 3 seconds each"
            PERFECT_SCORE -> "Get 100% on any quiz"
            STREAK_5 -> "Complete quizzes for 5 days in a row"
            STREAK_10 -> "Complete quizzes for 10 days in a row"
            QUIZ_MASTER -> "Complete 50 quizzes"
            CATEGORY_EXPERT -> "Get 90%+ on 5 quizzes in same category"
            LEVEL_10 -> "Reach level 10"
            LEVEL_25 -> "Reach level 25"
            LEVEL_50 -> "Reach level 50"
        }
    }
    
    fun getRarity(): BadgeRarity {
        return when (this) {
            FIRST_QUIZ -> BadgeRarity.COMMON
            SPEED_DEMON -> BadgeRarity.RARE
            PERFECT_SCORE -> BadgeRarity.UNCOMMON
            STREAK_5 -> BadgeRarity.UNCOMMON
            STREAK_10 -> BadgeRarity.RARE
            QUIZ_MASTER -> BadgeRarity.EPIC
            CATEGORY_EXPERT -> BadgeRarity.RARE
            LEVEL_10 -> BadgeRarity.UNCOMMON
            LEVEL_25 -> BadgeRarity.RARE
            LEVEL_50 -> BadgeRarity.LEGENDARY
        }
    }
}

/**
 * Badge rarity for visual distinction
 */
enum class BadgeRarity(val color: Long) {
    COMMON(0xFF9E9E9E),      // Gray
    UNCOMMON(0xFF4CAF50),    // Green
    RARE(0xFF2196F3),        // Blue
    EPIC(0xFF9C27B0),        // Purple
    LEGENDARY(0xFFFFD700)    // Gold
}

/**
 * Achievement model
 */
data class Achievement(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("badge_type")
    val badgeType: BadgeType,
    
    @SerializedName("earned_at")
    val earnedAt: Long = System.currentTimeMillis(),
    
    @SerializedName("is_new")
    var isNew: Boolean = true
)

/**
 * Student statistics model
 */
data class StudentStats(
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("total_quizzes_attempted")
    val totalQuizzesAttempted: Int = 0,
    
    @SerializedName("total_quizzes_completed")
    val totalQuizzesCompleted: Int = 0,
    
    @SerializedName("total_questions_answered")
    val totalQuestionsAnswered: Int = 0,
    
    @SerializedName("total_correct_answers")
    val totalCorrectAnswers: Int = 0,
    
    @SerializedName("average_score_percentage")
    val averageScorePercentage: Double = 0.0,
    
    @SerializedName("total_experience_points")
    val totalExperiencePoints: Int = 0,
    
    @SerializedName("current_level")
    val currentLevel: Int = 1,
    
    @SerializedName("current_streak")
    val currentStreak: Int = 0,
    
    @SerializedName("longest_streak")
    val longestStreak: Int = 0,
    
    @SerializedName("badges_earned")
    val badgesEarned: List<BadgeType> = emptyList(),
    
    @SerializedName("favorite_category")
    val favoriteCategory: String? = null,
    
    @SerializedName("global_rank")
    val globalRank: Int? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Get accuracy percentage
     */
    fun getAccuracyPercentage(): Double {
        if (totalQuestionsAnswered == 0) return 0.0
        return (totalCorrectAnswers.toDouble() / totalQuestionsAnswered) * 100
    }
    
    /**
     * Get progress to next level (0-100)
     */
    fun getProgressToNextLevel(): Int {
        val pointsForCurrentLevel = currentLevel * 100
        val pointsInCurrentLevel = totalExperiencePoints % 100
        return (pointsInCurrentLevel * 100) / 100
    }
    
    /**
     * Get experience points needed for next level
     */
    fun getXpNeededForNextLevel(): Int {
        val xpForNextLevel = (currentLevel + 1) * 100
        val totalXpNeeded = xpForNextLevel
        return totalXpNeeded - totalExperiencePoints
    }
    
    /**
     * Get performance tier based on average score
     */
    fun getPerformanceTier(): PerformanceTier {
        return PerformanceTier.fromPercentage(averageScorePercentage)
    }
    
    /**
     * Get completion rate percentage
     */
    fun getCompletionRate(): Double {
        if (totalQuizzesAttempted == 0) return 0.0
        return (totalQuizzesCompleted.toDouble() / totalQuizzesAttempted) * 100
    }
    
    /**
     * Check if user is on a streak
     */
    fun hasActiveStreak(): Boolean {
        return currentStreak > 0
    }
    
    /**
     * Get badges count by rarity
     */
    fun getBadgesByRarity(): Map<BadgeRarity, Int> {
        return badgesEarned.groupBy { it.getRarity() }
            .mapValues { it.value.size }
    }
    
    /**
     * Get total badges earned
     */
    fun getTotalBadges(): Int {
        return badgesEarned.size
    }
    
    /**
     * Get percentage of all possible badges
     */
    fun getBadgeCompletionPercentage(): Double {
        val totalPossibleBadges = BadgeType.values().size
        return (badgesEarned.size.toDouble() / totalPossibleBadges) * 100
    }
}
