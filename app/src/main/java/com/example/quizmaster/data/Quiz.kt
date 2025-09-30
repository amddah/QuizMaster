package com.example.quizmaster.data

data class Quiz(
    val questions: List<Question>,
    val category: QuizCategory,
    val difficulty: QuizDifficulty,
    val totalQuestions: Int = questions.size
)