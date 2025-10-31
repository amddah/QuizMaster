package com.example.quizmaster.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.Achievement
import com.example.quizmaster.data.model.BadgeType
import com.example.quizmaster.data.model.StudentStats
import com.example.quizmaster.data.model.User
import com.example.quizmaster.data.remote.CategoryStats
import com.example.quizmaster.data.remote.GamificationApiService
import com.example.quizmaster.data.remote.StreakInfo
import com.example.quizmaster.repository.GamificationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for gamification profile
 */
class GamificationProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = UserSessionManager.getInstance(application)
    private val repository: GamificationRepository by lazy {
        // Initialize with API service - you'll need to update this based on your ApiClient setup
        GamificationRepository(
            com.example.quizmaster.data.remote.ApiClient.createGamificationService()
        )
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _stats = MutableLiveData<StudentStats?>()
    val stats: LiveData<StudentStats?> = _stats

    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> = _achievements

    private val _streakInfo = MutableLiveData<StreakInfo?>()
    val streakInfo: LiveData<StreakInfo?> = _streakInfo

    private val _categoryStats = MutableLiveData<List<CategoryStats>>()
    val categoryStats: LiveData<List<CategoryStats>> = _categoryStats

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Load complete user profile with all gamification data
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Load user info from session
                sessionManager.currentUser.first()?.let { user ->
                    _user.value = user
                }

                // Load stats
                loadStats()

                // Load achievements
                loadAchievements()

                // Load streak info
                loadStreakInfo()

                // Load category stats
                loadCategoryStats()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadStats() {
        val result = repository.getUserStats()
        result.onSuccess { stats ->
            _stats.value = stats
        }.onFailure { error ->
            _errorMessage.value = "Failed to load statistics: ${error.message}"
        }
    }

    private suspend fun loadAchievements() {
        val result = repository.getUserAchievements()
        result.onSuccess { achievements ->
            _achievements.value = achievements
        }.onFailure { error ->
            _errorMessage.value = "Failed to load achievements: ${error.message}"
        }
    }

    private suspend fun loadStreakInfo() {
        val result = repository.getStreakInfo()
        result.onSuccess { streak ->
            _streakInfo.value = streak
        }.onFailure { error ->
            // Streak info is optional, don't show error
            _streakInfo.value = StreakInfo(0, 0, null, false, 0)
        }
    }

    private suspend fun loadCategoryStats() {
        val result = repository.getCategoryStats()
        result.onSuccess { categoryStats ->
            _categoryStats.value = categoryStats
        }.onFailure { error ->
            // Category stats are optional
            _categoryStats.value = emptyList()
        }
    }

    /**
     * Get total number of possible badges
     */
    fun getTotalBadgesCount(): Int {
        return BadgeType.values().size
    }

    /**
     * Refresh profile data
     */
    fun refresh() {
        loadUserProfile()
    }
}
