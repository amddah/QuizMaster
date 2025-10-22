package com.example.quizmaster.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.Question
import com.example.quizmaster.data.Quiz
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.data.QuizResult
import com.example.quizmaster.data.local.QuizDataStore
import com.example.quizmaster.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

sealed class QuizUiState {
    object Loading : QuizUiState()
    data class QuestionReady(
        val question: Question,
        val currentQuestionNumber: Int,
        val totalQuestions: Int
    ) : QuizUiState()
    data class QuizCompleted(
        val score: Int,
        val totalQuestions: Int,
        val category: QuizCategory,
        val difficulty: QuizDifficulty
    ) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    private var currentQuiz: Quiz? = null
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var quizStartTime = 0L
    
    fun startQuiz(category: QuizCategory, difficulty: QuizDifficulty) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            quizStartTime = System.currentTimeMillis()
            
            repository.getQuiz(category, difficulty).fold(
                onSuccess = { quiz ->
                    currentQuiz = quiz
                    currentQuestionIndex = 0
                    correctAnswers = 0
                    showCurrentQuestion()
                },
                onFailure = { error ->
                    _uiState.value = QuizUiState.Error(error.message ?: "Failed to load quiz")
                }
            )
        }
    }

    /**
     * Start a quiz using a preloaded Quiz object (e.g. fetched via Search-by-ID).
     */
    fun startQuizWithQuiz(quiz: Quiz) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            quizStartTime = System.currentTimeMillis()

            currentQuiz = quiz
            currentQuestionIndex = 0
            correctAnswers = 0

            // Small suspend to ensure UI sees Loading state then updates
            showCurrentQuestion()
        }
    }

    fun submitAnswer(selectedAnswer: String) {
        val quiz = currentQuiz ?: return
        val currentQuestion = quiz.questions[currentQuestionIndex]
        
        if (currentQuestion.isCorrectAnswer(selectedAnswer)) {
            correctAnswers++
        }
    }
    
    fun nextQuestion() {
        val quiz = currentQuiz ?: return
        
        currentQuestionIndex++
        
        if (currentQuestionIndex < quiz.questions.size) {
            showCurrentQuestion()
        } else {
            completeQuiz()
        }
    }
    
    private fun showCurrentQuestion() {
        val quiz = currentQuiz ?: return
        val question = quiz.questions[currentQuestionIndex]
        
        _uiState.value = QuizUiState.QuestionReady(
            question = question,
            currentQuestionNumber = currentQuestionIndex + 1,
            totalQuestions = quiz.questions.size
        )
    }
    
    private fun completeQuiz() {
        val quiz = currentQuiz ?: return
        val timeSpent = System.currentTimeMillis() - quizStartTime
        
        val result = QuizResult(
            score = correctAnswers,
            totalQuestions = quiz.questions.size,
            category = quiz.category,
            difficulty = quiz.difficulty,
            completedAt = Date(),
            timeSpent = timeSpent
        )
        
        // Save the result
        viewModelScope.launch {
            repository.saveQuizResult(result)
        }
        
        _uiState.value = QuizUiState.QuizCompleted(
            score = correctAnswers,
            totalQuestions = quiz.questions.size,
            category = quiz.category,
            difficulty = quiz.difficulty
        )
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                val dataStore = QuizDataStore(context)
                val repository = QuizRepository(dataStore)
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}