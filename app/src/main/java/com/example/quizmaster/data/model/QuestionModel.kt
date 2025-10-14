package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Question type enum
 */
enum class QuestionType {
    TRUE_FALSE,
    MULTIPLE_CHOICE;
    
    companion object {
        fun fromString(type: String): QuestionType? {
            return entries.find { it.name.equals(type, ignoreCase = true) }
        }
    }
}

/**
 * Enhanced question model with timing and scoring support
 */
data class QuestionModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("question_text")
    val questionText: String,
    
    @SerializedName("type")
    val type: QuestionType,
    
    @SerializedName("correct_answer")
    val correctAnswer: String,
    
    @SerializedName("options")
    val options: List<String>,  // For multiple choice, includes correct answer
    
    @SerializedName("time_limit")
    val timeLimit: Int = 15,  // seconds
    
    @SerializedName("max_score")
    val maxScore: Int = 100,  // Maximum points for this question
    
    @SerializedName("explanation")
    val explanation: String? = null  // Optional explanation for the answer
) {
    /**
     * Get all answer options (shuffled for multiple choice)
     */
    fun getAllOptions(): List<String> {
        return when (type) {
            QuestionType.TRUE_FALSE -> listOf("True", "False")
            QuestionType.MULTIPLE_CHOICE -> options.shuffled()
        }
    }
    
    /**
     * Check if the given answer is correct
     */
    fun isCorrectAnswer(answer: String): Boolean {
        return answer.equals(correctAnswer, ignoreCase = true)
    }
    
    /**
     * Calculate score based on response time
     * - 0-5 seconds: 100% of max score
     * - 5-10 seconds: 70% of max score
     * - 10-15 seconds: 40% of max score
     * - >15 seconds: 0 points
     */
    fun calculateScore(responseTimeSeconds: Int, isCorrect: Boolean): Int {
        if (!isCorrect) return 0
        
        return when {
            responseTimeSeconds <= 5 -> maxScore
            responseTimeSeconds <= 10 -> (maxScore * 0.7).toInt()
            responseTimeSeconds <= 15 -> (maxScore * 0.4).toInt()
            else -> 0
        }
    }
    
    /**
     * Get score multiplier based on response time
     */
    fun getScoreMultiplier(responseTimeSeconds: Int): Double {
        return when {
            responseTimeSeconds <= 5 -> 1.0
            responseTimeSeconds <= 10 -> 0.7
            responseTimeSeconds <= 15 -> 0.4
            else -> 0.0
        }
    }
}
