package com.example.quizmaster.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.local.QuizDataStore
import com.example.quizmaster.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: QuizRepository) : ViewModel() {
    
    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()
    
    init {
        loadBestScore()
    }
    
    private fun loadBestScore() {
        viewModelScope.launch {
            repository.getBestScore().collect { score ->
                _bestScore.value = score
            }
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                val dataStore = QuizDataStore(context)
                val repository = QuizRepository(dataStore)
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}