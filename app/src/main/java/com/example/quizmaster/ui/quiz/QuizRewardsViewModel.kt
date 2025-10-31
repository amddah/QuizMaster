package com.example.quizmaster.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.model.Achievement
import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.remote.XpGainResponse
import com.example.quizmaster.repository.GamificationRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for quiz rewards screen
 */
class QuizRewardsViewModel(application: Application) : AndroidViewModel(application) {

    private val gamificationRepository: GamificationRepository by lazy {
        GamificationRepository(
            com.example.quizmaster.data.remote.ApiClient.createGamificationService()
        )
    }

    private val _attempt = MutableLiveData<QuizAttempt?>()
    val attempt: LiveData<QuizAttempt?> = _attempt

    private val _xpGain = MutableLiveData<XpGainResponse?>()
    val xpGain: LiveData<XpGainResponse?> = _xpGain

    private val _newBadges = MutableLiveData<List<Achievement>>()
    val newBadges: LiveData<List<Achievement>> = _newBadges

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Load rewards for a completed attempt
     */
    fun loadRewards(attemptId: String) {
        viewModelScope.launch {
            try {
                // Load attempt details
                // This would use your QuizAttemptRepository
                // For now, we'll focus on XP and badges

                // Load XP gain
                val xpResult = gamificationRepository.getAttemptXp(attemptId)
                xpResult.onSuccess { xpGain ->
                    _xpGain.value = xpGain

                    // Load new badges if any were unlocked
                    if (xpGain.new_badges.isNotEmpty()) {
                        loadNewBadges()
                    }
                }.onFailure { error ->
                    _errorMessage.value = "Failed to load XP info: ${error.message}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error loading rewards: ${e.message}"
            }
        }
    }

    private suspend fun loadNewBadges() {
        val result = gamificationRepository.getUserAchievements()
        result.onSuccess { achievements ->
            // Filter for new badges
            val newBadges = achievements.filter { it.isNew }
            _newBadges.value = newBadges
        }
    }

    /**
     * Set attempt data (from previous screen)
     */
    fun setAttempt(attempt: QuizAttempt) {
        _attempt.value = attempt
    }
}
