package com.example.quizmaster.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.model.User
import com.example.quizmaster.data.model.toQuizModel
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.BadgeData
import com.example.quizmaster.repository.GamificationRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for user profile with backend data
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authService = ApiClient.authApiService
    private val gamificationRepository = GamificationRepository(
        ApiClient.createGamificationService()
    )

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _badges = MutableLiveData<List<BadgeData>>()
    val badges: LiveData<List<BadgeData>> = _badges

    private val _level = MutableLiveData<Int>()
    val level: LiveData<Int> = _level

    private val _xp = MutableLiveData<Int>()
    val xp: LiveData<Int> = _xp

    private val _totalBadges = MutableLiveData<Int>()
    val totalBadges: LiveData<Int> = _totalBadges

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _myQuizzesStatus = MutableLiveData<List<com.example.quizmaster.data.model.QuizModel>>()
    val myQuizzesStatus: LiveData<List<com.example.quizmaster.data.model.QuizModel>> = _myQuizzesStatus

    /**
     * Load user profile from backend
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load user profile
                val profileResponse = authService.getCurrentUser()
                if (profileResponse.isSuccessful && profileResponse.body() != null) {
                    _user.value = profileResponse.body()
                }

                // Load achievements (badges)
                loadAchievements()

                // Load student's created quizzes status
                loadMyQuizzesStatus()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadMyQuizzesStatus() {
        try {
            android.util.Log.d("ProfileViewModel", "Calling getMyQuizzesStatus()...")
            val response = authService.getMyQuizzesStatus()
            android.util.Log.d("ProfileViewModel", "Response code=${response.code()} isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!
                android.util.Log.d("ProfileViewModel", "Raw response body size=${raw.size}")
                raw.forEachIndexed { index, wrapper ->
                    android.util.Log.d("ProfileViewModel", "[$index] quiz.id=${wrapper.quiz.id} quiz.title=${wrapper.quiz.title} status=${wrapper.status}")
                }
                val list = raw.map { wrapper ->
                    // Convert inner quiz then prefer the wrapper.status if provided
                    val baseQuiz = wrapper.quiz.toQuizModel()
                    val statusFromWrapper = com.example.quizmaster.data.model.ApprovalStatus.fromString(wrapper.status)
                    if (statusFromWrapper != null) baseQuiz.copy(approvalStatus = statusFromWrapper) else baseQuiz
                }
                android.util.Log.d("ProfileViewModel", "Mapped quizzes: size=${list.size}")
                list.forEachIndexed { index, quiz ->
                    android.util.Log.d("ProfileViewModel", "[$index] mapped quiz.id=${quiz.id} quiz.title=${quiz.title} approvalStatus=${quiz.approvalStatus}")
                }
                _myQuizzesStatus.value = list
            } else {
                android.util.Log.e("ProfileViewModel", "Response unsuccessful or body is null")
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileViewModel", "Exception loading quizzes status", e)
            _errorMessage.value = "Failed to load quizzes status: ${e.message}"
        }
    }

    /**
     * Load user achievements from backend
     */
    private suspend fun loadAchievements() {
        try {
            val result = gamificationRepository.getUserAchievements()
            result.onSuccess { achievements ->
                // The backend returns: {"badges": [...], "level": 3, "total_badges": 2, "xp": 650}
                // We need to parse this properly
                _badges.value = achievements.badges
                _level.value = achievements.level
                _xp.value = achievements.xp
                _totalBadges.value = achievements.total_badges
            }.onFailure { error ->
                _errorMessage.value = "Failed to load achievements: ${error.message}"
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error loading achievements: ${e.message}"
        }
    }

    /**
     * Refresh profile data
     */
    fun refreshProfile() {
        loadUserProfile()
    }
}
