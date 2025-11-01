package com.example.quizmaster.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.model.QuizModel
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.repository.QuizAttemptRepository
import com.example.quizmaster.data.repository.QuizManagementRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for quiz review
 */
class QuizReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val attemptRepository = QuizAttemptRepository()
    private val quizRepository = QuizManagementRepository(
        ApiClient.quizApiService,
        ApiClient.courseApiService
    )

    private val _reviewItems = MutableLiveData<List<QuizReviewItem>>()
    val reviewItems: LiveData<List<QuizReviewItem>> = _reviewItems

    private val _scoreInfo = MutableLiveData<ScoreInfo>()
    val scoreInfo: LiveData<ScoreInfo> = _scoreInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Load attempt review data
     */
    fun loadAttemptReview(attemptId: String, quizId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Get attempt details
                val attemptResult = attemptRepository.getAttemptById(attemptId)
                val quizResult = quizRepository.getQuizById(quizId)

                if (attemptResult.isSuccess && quizResult.isSuccess) {
                    val attempt = attemptResult.getOrNull()!!
                    val quiz = quizResult.getOrNull()!!
                    
                    // Build review items
                    val items = buildReviewItems(attempt, quiz)
                    _reviewItems.value = items
                    
                    // Calculate score info
                    val correctAnswers = attempt.answers?.count { it.isCorrect } ?: 0
                    val totalQuestions = attempt.answers?.size ?: 0
                    val percentage = if (attempt.maxScore > 0) {
                        ((attempt.totalScore / attempt.maxScore) * 100).toInt()
                    } else 0
                    
                    _scoreInfo.value = ScoreInfo(
                        score = attempt.totalScore.toInt(),
                        maxScore = attempt.maxScore.toInt(),
                        percentage = percentage,
                        correctAnswers = correctAnswers,
                        totalQuestions = totalQuestions
                    )
                } else {
                    _errorMessage.value = "Failed to load review data"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Build review items from attempt and quiz
     */
    private fun buildReviewItems(attempt: QuizAttempt, quiz: QuizModel): List<QuizReviewItem> {
        val items = mutableListOf<QuizReviewItem>()
        
        attempt.answers?.forEachIndexed { index, answer ->
            // Find the corresponding question from quiz
            val question = quiz.questions.find { it.id == answer.questionId }
            
            question?.let { q ->
                items.add(
                    QuizReviewItem(
                        questionNumber = index + 1,
                        questionText = q.questionText,
                        studentAnswer = answer.studentAnswer?.toString() ?: "No answer",
                        correctAnswer = q.correctAnswer,
                        isCorrect = answer.isCorrect,
                        pointsEarned = answer.pointsEarned.toInt(),
                        questionPoints = q.maxScore
                    )
                )
            }
        }
        
        return items
    }
}

/**
 * Data class for review item
 */
data class QuizReviewItem(
    val questionNumber: Int,
    val questionText: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val questionPoints: Int
)

/**
 * Data class for score info
 */
data class ScoreInfo(
    val score: Int,
    val maxScore: Int,
    val percentage: Int,
    val correctAnswers: Int,
    val totalQuestions: Int
)
