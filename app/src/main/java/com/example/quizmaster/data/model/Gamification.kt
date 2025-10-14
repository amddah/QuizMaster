package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

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
}
