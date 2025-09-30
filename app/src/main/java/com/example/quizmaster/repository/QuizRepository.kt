package com.example.quizmaster.repository

import com.example.quizmaster.data.Quiz
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.data.QuizResult
import com.example.quizmaster.data.local.OfflineQuestions
import com.example.quizmaster.data.local.QuizDataStore
import com.example.quizmaster.data.local.QuizStatistics
import com.example.quizmaster.data.remote.TriviaApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException

class QuizRepository(private val dataStore: QuizDataStore) {
    
    private val apiService: TriviaApiService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        Retrofit.Builder()
            .baseUrl(TriviaApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApiService::class.java)
    }
    
    suspend fun getQuiz(category: QuizCategory, difficulty: QuizDifficulty): Result<Quiz> {
        return try {
            // Try to get questions from API first
            val response = apiService.getQuestions(
                amount = 10,
                category = category.apiValue,
                difficulty = difficulty.apiValue
            )
            
            if (response.responseCode == 0 && response.results.isNotEmpty()) {
                val quiz = Quiz(
                    questions = response.results,
                    category = category,
                    difficulty = difficulty
                )
                Result.success(quiz)
            } else {
                // Fallback to offline questions
                val offlineQuiz = OfflineQuestions.getQuizForCategory(category, difficulty)
                Result.success(offlineQuiz)
            }
        } catch (e: UnknownHostException) {
            // No internet connection, use offline questions
            val offlineQuiz = OfflineQuestions.getQuizForCategory(category, difficulty)
            Result.success(offlineQuiz)
        } catch (e: Exception) {
            // Any other error, try offline questions as fallback
            try {
                val offlineQuiz = OfflineQuestions.getQuizForCategory(category, difficulty)
                Result.success(offlineQuiz)
            } catch (offlineError: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun saveQuizResult(result: QuizResult) {
        dataStore.saveQuizResult(result)
    }
    
    fun getQuizResults(): Flow<List<QuizResult>> {
        return dataStore.getQuizResults()
    }
    
    fun getBestScore(): Flow<Int> {
        return dataStore.getBestScore()
    }
    
    fun getQuizStatistics(): Flow<QuizStatistics> {
        return dataStore.getQuizStatistics()
    }
    
    suspend fun clearAllData() {
        dataStore.clearAllData()
    }
}