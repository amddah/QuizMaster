package com.example.quizmaster.data

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("question")
    val question: String,
    @SerializedName("correct_answer")
    val correctAnswer: String,
    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String>,
    @SerializedName("category")
    val category: String,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("type")
    val type: String = "multiple"
) {
    fun getAllAnswers(): List<String> {
        return (incorrectAnswers + correctAnswer).shuffled()
    }
    
    fun isCorrectAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }
}