package com.example.quizmaster.data

import java.util.Date

data class QuizResult(
    val score: Int,
    val totalQuestions: Int,
    val category: QuizCategory,
    val difficulty: QuizDifficulty,
    val completedAt: Date = Date(),
    val timeSpent: Long = 0L // in milliseconds
) {
    val percentage: Int
        get() = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
    
    val motivationalMessage: String
        get() = when (percentage) {
            100 -> "Perfect! 🎉 You're a quiz master!"
            in 80..99 -> "Excellent! 🌟 Almost perfect!"
            in 60..79 -> "Well done! 👍 Good job!"
            in 40..59 -> "Not bad! 👌 Keep practicing!"
            in 20..39 -> "Keep trying! 💪 You'll get better!"
            else -> "Don't give up! 📚 Practice makes perfect!"
        }
}