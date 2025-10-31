package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model representing a single answer in a quiz attempt
 * Matches the backend Answer model from Swagger spec
 */
data class Answer(
    @SerializedName("question_id")
    val questionId: String,
    
    @SerializedName("student_answer")
    val studentAnswer: Any?, // Can be string or other type
    
    @SerializedName("is_correct")
    val isCorrect: Boolean,
    
    @SerializedName("points_earned")
    val pointsEarned: Double,
    
    @SerializedName("time_to_answer")
    val timeToAnswer: Int, // In seconds
    
    @SerializedName("answered_at")
    val answeredAt: String? = null
)

/**
 * Model representing XP reward breakdown
 * Matches the backend XPReward model from Swagger spec
 */
data class XPReward(
    @SerializedName("base_xp")
    val baseXp: Int,
    
    @SerializedName("speed_bonus")
    val speedBonus: Int,
    
    @SerializedName("accuracy_bonus")
    val accuracyBonus: Int,
    
    @SerializedName("difficulty_bonus")
    val difficultyBonus: Int,
    
    @SerializedName("streak_bonus")
    val streakBonus: Int,
    
    @SerializedName("total_xp")
    val totalXp: Int,
    
    @SerializedName("leveled_up")
    val leveledUp: Boolean,
    
    @SerializedName("new_level")
    val newLevel: Int
)

/**
 * Model representing a complete quiz attempt
 * Matches the backend QuizAttempt model from Swagger spec
 */
data class QuizAttempt(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("quiz_id")
    val quizId: String,
    
    @SerializedName("student_id")
    val studentId: String,
    
    @SerializedName("answers")
    val answers: List<Answer> = emptyList(),
    
    @SerializedName("total_score")
    val totalScore: Double,
    
    @SerializedName("max_score")
    val maxScore: Double,
    
    @SerializedName("time_taken")
    val timeTaken: Int, // In seconds
    
    @SerializedName("started_at")
    val startedAt: String,
    
    @SerializedName("completed_at")
    val completedAt: String? = null,
    
    @SerializedName("xp_earned")
    val xpEarned: Int = 0,
    
    @SerializedName("xp_details")
    val xpDetails: XPReward? = null
) {
    /**
     * Get percentage score
     */
    fun getPercentage(): Double {
        return if (maxScore > 0) {
            (totalScore / maxScore) * 100.0
        } else 0.0
    }
    
    /**
     * Get performance rating based on percentage
     */
    fun getPerformanceRating(): String {
        val percentage = getPercentage()
        return when {
            percentage >= 90 -> "Excellent! 🏆"
            percentage >= 75 -> "Great Job! ⭐"
            percentage >= 60 -> "Good! 👍"
            percentage >= 50 -> "Not Bad! 😊"
            else -> "Keep Practicing! 💪"
        }
    }
    
    /**
     * Get average time per question
     */
    fun getAverageTimePerQuestion(): Double {
        return if (answers.isNotEmpty()) {
            timeTaken.toDouble() / answers.size
        } else 0.0
    }
    
    /**
     * Get number of correct answers
     */
    fun getCorrectAnswersCount(): Int {
        return answers.count { it.isCorrect }
    }
    
    /**
     * Get total number of questions
     */
    fun getTotalQuestions(): Int {
        return answers.size
    }
}
