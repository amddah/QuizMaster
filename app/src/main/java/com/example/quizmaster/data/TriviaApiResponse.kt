package com.example.quizmaster.data

import com.google.gson.annotations.SerializedName

data class TriviaApiResponse(
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("results")
    val results: List<Question>
)