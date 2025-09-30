package com.example.quizmaster.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.QuizResult
import com.example.quizmaster.data.local.QuizDataStore
import com.example.quizmaster.data.local.QuizStatistics
import com.example.quizmaster.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: QuizRepository) : ViewModel() {
    
    private val _quizResults = MutableStateFlow<List<QuizResult>>(emptyList())
    val quizResults: StateFlow<List<QuizResult>> = _quizResults.asStateFlow()
    
    private val _statistics = MutableStateFlow(
        QuizStatistics(
            totalQuizzesPlayed = 0,
            bestScore = 0,
            totalQuestionsAnswered = 0,
            totalCorrectAnswers = 0,
            firstPlayDate = null
        )
    )
    val statistics: StateFlow<QuizStatistics> = _statistics.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            repository.getQuizResults().collect { results ->
                _quizResults.value = results
            }
        }
        
        viewModelScope.launch {
            repository.getQuizStatistics().collect { stats ->
                _statistics.value = stats
            }
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                val dataStore = QuizDataStore(context)
                val repository = QuizRepository(dataStore)
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}