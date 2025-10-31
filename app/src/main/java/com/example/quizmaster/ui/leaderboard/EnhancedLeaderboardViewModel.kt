package com.example.quizmaster.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.remote.GlobalRankResponse
import com.example.quizmaster.data.remote.LeaderboardRankEntry
import com.example.quizmaster.repository.GamificationRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for enhanced leaderboard
 */
class EnhancedLeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GamificationRepository by lazy {
        GamificationRepository(
            com.example.quizmaster.data.remote.ApiClient.createGamificationService()
        )
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _myRank = MutableLiveData<GlobalRankResponse?>()
    val myRank: LiveData<GlobalRankResponse?> = _myRank

    private val _leaderboard = MutableLiveData<List<LeaderboardRankEntry>>()
    val leaderboard: LiveData<List<LeaderboardRankEntry>> = _leaderboard

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Load global leaderboard and user's rank
     */
    fun loadGlobalLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Load user's rank
                loadMyRank()

                // Load leaderboard
                val result = repository.getGlobalLeaderboard()
                result.onSuccess { leaderboard ->
                    _leaderboard.value = leaderboard
                }.onFailure { error ->
                    _errorMessage.value = "Failed to load leaderboard: ${error.message}"
                    _leaderboard.value = emptyList()
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error loading leaderboard: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadMyRank() {
        val result = repository.getGlobalRank()
        result.onSuccess { rank ->
            _myRank.value = rank
        }.onFailure { error ->
            // Rank is optional, don't show error
            _myRank.value = null
        }
    }

    /**
     * Refresh leaderboard data
     */
    fun refresh() {
        loadGlobalLeaderboard()
    }
}
