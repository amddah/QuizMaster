package com.example.quizmaster.data.remote

import com.example.quizmaster.data.TriviaApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String = "multiple"
    ): TriviaApiResponse
    
    companion object {
        const val BASE_URL = "https://opentdb.com/"
    }
}