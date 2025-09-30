package com.example.quizmaster.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quizmaster.data.QuizResult
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quiz_preferences")

class QuizDataStore(private val context: Context) {
    
    private val gson = Gson()
    
    private object Keys {
        val QUIZ_RESULTS = stringPreferencesKey("quiz_results")
        val BEST_SCORE = intPreferencesKey("best_score")
        val TOTAL_QUIZZES_PLAYED = intPreferencesKey("total_quizzes_played")
        val TOTAL_QUESTIONS_ANSWERED = intPreferencesKey("total_questions_answered")
        val TOTAL_CORRECT_ANSWERS = intPreferencesKey("total_correct_answers")
        val FIRST_PLAY_DATE = longPreferencesKey("first_play_date")
    }
    
    suspend fun saveQuizResult(result: QuizResult) {
        context.dataStore.edit { preferences ->
            // Get existing results
            val existingResults = getQuizResultsList(preferences)
            val updatedResults = existingResults + result
            
            // Keep only the last 50 results to avoid storage bloat
            val trimmedResults = if (updatedResults.size > 50) {
                updatedResults.takeLast(50)
            } else {
                updatedResults
            }
            
            // Save updated results
            preferences[Keys.QUIZ_RESULTS] = gson.toJson(trimmedResults)
            
            // Update statistics
            val currentBestScore = preferences[Keys.BEST_SCORE] ?: 0
            if (result.percentage > currentBestScore) {
                preferences[Keys.BEST_SCORE] = result.percentage
            }
            
            val totalQuizzes = (preferences[Keys.TOTAL_QUIZZES_PLAYED] ?: 0) + 1
            preferences[Keys.TOTAL_QUIZZES_PLAYED] = totalQuizzes
            
            val totalQuestions = (preferences[Keys.TOTAL_QUESTIONS_ANSWERED] ?: 0) + result.totalQuestions
            preferences[Keys.TOTAL_QUESTIONS_ANSWERED] = totalQuestions
            
            val totalCorrect = (preferences[Keys.TOTAL_CORRECT_ANSWERS] ?: 0) + result.score
            preferences[Keys.TOTAL_CORRECT_ANSWERS] = totalCorrect
            
            // Set first play date if not set
            if (preferences[Keys.FIRST_PLAY_DATE] == null) {
                preferences[Keys.FIRST_PLAY_DATE] = System.currentTimeMillis()
            }
        }
    }
    
    fun getQuizResults(): Flow<List<QuizResult>> {
        return context.dataStore.data.map { preferences ->
            getQuizResultsList(preferences)
        }
    }
    
    private fun getQuizResultsList(preferences: Preferences): List<QuizResult> {
        val resultsJson = preferences[Keys.QUIZ_RESULTS] ?: return emptyList()
        val type = object : TypeToken<List<QuizResult>>() {}.type
        return try {
            gson.fromJson(resultsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getBestScore(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.BEST_SCORE] ?: 0
        }
    }
    
    fun getQuizStatistics(): Flow<QuizStatistics> {
        return context.dataStore.data.map { preferences ->
            QuizStatistics(
                totalQuizzesPlayed = preferences[Keys.TOTAL_QUIZZES_PLAYED] ?: 0,
                bestScore = preferences[Keys.BEST_SCORE] ?: 0,
                totalQuestionsAnswered = preferences[Keys.TOTAL_QUESTIONS_ANSWERED] ?: 0,
                totalCorrectAnswers = preferences[Keys.TOTAL_CORRECT_ANSWERS] ?: 0,
                firstPlayDate = preferences[Keys.FIRST_PLAY_DATE]?.let { Date(it) }
            )
        }
    }
    
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class QuizStatistics(
    val totalQuizzesPlayed: Int,
    val bestScore: Int,
    val totalQuestionsAnswered: Int,
    val totalCorrectAnswers: Int,
    val firstPlayDate: Date?
) {
    val averageScore: Int
        get() = if (totalQuestionsAnswered > 0) {
            (totalCorrectAnswers * 100) / totalQuestionsAnswered
        } else 0
}