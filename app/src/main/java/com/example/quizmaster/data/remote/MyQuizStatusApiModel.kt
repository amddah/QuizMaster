package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.QuizApiModel
import com.google.gson.annotations.SerializedName

/**
 * Wrapper for API response of /users/quizzes-status
 */
data class MyQuizStatusApiModel(
    @SerializedName("quiz")
    val quiz: QuizApiModel,

    @SerializedName("status")
    val status: String
)
