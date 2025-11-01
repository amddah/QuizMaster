package com.example.quizmaster.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.repository.QuizAttemptRepository
import com.example.quizmaster.data.repository.QuizManagementRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for quiz history
 */
class QuizHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val attemptRepository = QuizAttemptRepository()
    
    private val quizRepository = QuizManagementRepository(
        ApiClient.quizApiService,
        ApiClient.courseApiService
    )

    private val _quizHistory = MutableLiveData<List<QuizAttemptWithQuiz>>()
    val quizHistory: LiveData<List<QuizAttemptWithQuiz>> = _quizHistory

    private val _statistics = MutableLiveData<QuizStatistics>()
    val statistics: LiveData<QuizStatistics> = _statistics

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Load quiz history from backend
     */
    fun loadQuizHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                android.util.Log.d("QuizHistoryViewModel", "Starting to load quiz history")
                // Get all attempts
                val attemptsResult = attemptRepository.getMyAttempts()
                
                attemptsResult.onSuccess { attempts ->
                    android.util.Log.d("QuizHistoryViewModel", "Loaded ${attempts.size} attempts")
                    
                    // Load quiz info for each attempt
                    val attemptsWithQuiz = attempts.map { attempt ->
                        android.util.Log.d("QuizHistoryViewModel", "Loading quiz for attempt ${attempt.id}, quiz_id: ${attempt.quizId}")
                        val quizResult = quizRepository.getQuizById(attempt.quizId)
                        QuizAttemptWithQuiz(
                            attempt = attempt,
                            quiz = quizResult.getOrNull()
                        )
                    }
                    
                    // Sort by date (most recent first)
                    val sortedAttempts = attemptsWithQuiz.sortedByDescending { 
                        it.attempt.completedAt ?: it.attempt.startedAt
                    }
                    
                    android.util.Log.d("QuizHistoryViewModel", "Setting ${sortedAttempts.size} sorted attempts to LiveData")
                    _quizHistory.value = sortedAttempts
                    
                    // Calculate statistics
                    calculateStatistics(attempts)
                    
                }.onFailure { error ->
                    android.util.Log.e("QuizHistoryViewModel", "Failed to load attempts", error)
                    _errorMessage.value = "Failed to load quiz history: ${error.message}"
                }

            } catch (e: Exception) {
                android.util.Log.e("QuizHistoryViewModel", "Exception loading quiz history", e)
                _errorMessage.value = "Error loading quiz history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateStatistics(attempts: List<com.example.quizmaster.data.model.QuizAttempt>) {
        try {
            val totalQuizzes = attempts.size
            val totalXp = attempts.sumOf { it.xpEarned.toInt() }
            
            val averageScore = if (attempts.isNotEmpty()) {
                val totalPercentage = attempts.sumOf { attempt ->
                    if (attempt.maxScore > 0) {
                        (attempt.totalScore / attempt.maxScore * 100).toInt()
                    } else 0
                }
                totalPercentage / attempts.size
            } else 0

            android.util.Log.d("QuizHistoryViewModel", "Statistics: quizzes=$totalQuizzes, avgScore=$averageScore, totalXp=$totalXp")
            _statistics.value = QuizStatistics(
                totalQuizzes = totalQuizzes,
                averageScore = averageScore,
                totalXp = totalXp
            )
        } catch (e: Exception) {
            android.util.Log.e("QuizHistoryViewModel", "Error calculating statistics", e)
        }
    }
}

/**
 * Statistics summary for quiz history
 */
data class QuizStatistics(
    val totalQuizzes: Int,
    val averageScore: Int,
    val totalXp: Int
)
