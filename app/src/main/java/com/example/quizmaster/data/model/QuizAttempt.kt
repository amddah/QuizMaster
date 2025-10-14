package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model representing a single answer in a quiz attempt
 */
data class QuizAnswer(
    @SerializedName("question_id")
    val questionId: String,
    
    @SerializedName("selected_answer")
    val selectedAnswer: String,
    
    @SerializedName("is_correct")
    val isCorrect: Boolean,
    
    @SerializedName("response_time_seconds")
    val responseTimeSeconds: Int,
    
    @SerializedName("score_earned")
    val scoreEarned: Int,
    
    @SerializedName("max_score")
    val maxScore: Int
)

/**
 * Model representing a complete quiz attempt
 */
data class QuizAttempt(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("quiz_id")
    val quizId: String,
    
    @SerializedName("quiz_title")
    val quizTitle: String,
    
    @SerializedName("student_id")
    val studentId: String,
    
    @SerializedName("student_name")
    val studentName: String,
    
    @SerializedName("answers")
    val answers: List<QuizAnswer>,
    
    @SerializedName("total_score")
    val totalScore: Int,
    
    @SerializedName("max_possible_score")
    val maxPossibleScore: Int,
    
    @SerializedName("percentage")
    val percentage: Double,
    
    @SerializedName("total_time_seconds")
    val totalTimeSeconds: Int,
    
    @SerializedName("correct_answers")
    val correctAnswers: Int,
    
    @SerializedName("total_questions")
    val totalQuestions: Int,
    
    @SerializedName("completed_at")
    val completedAt: Long = System.currentTimeMillis(),
    
    @SerializedName("experience_gained")
    val experienceGained: Int = 0
) {
    /**
     * Get performance rating based on percentage
     */
    fun getPerformanceRating(): String {
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
        return if (totalQuestions > 0) {
            totalTimeSeconds.toDouble() / totalQuestions
        } else 0.0
    }
}
