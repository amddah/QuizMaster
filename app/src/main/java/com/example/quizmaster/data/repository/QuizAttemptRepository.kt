package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.LeaderboardEntry
import com.example.quizmaster.data.remote.StartAttemptRequest
import com.example.quizmaster.data.remote.SubmitAnswerRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for quiz attempt operations
 * All endpoints match the backend Swagger specification
 */
class QuizAttemptRepository {
    
    private val quizAttemptApiService = ApiClient.quizAttemptApiService
    
    /**
     * Start a new quiz attempt
     * POST /attempts/start
     */
    suspend fun startAttempt(quizId: String): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val request = StartAttemptRequest(quizId = quizId)
            val response = quizAttemptApiService.startAttempt(request)
            
            if (response.isSuccessful && response.body() != null) {
                // Extract attempt from the wrapper response
                val attempt = response.body()!!.attempt
                Result.success(attempt)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to start attempt: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Submit an answer for a question in an ongoing attempt
     * POST /attempts/answer
     */
    suspend fun submitAnswer(
        attemptId: String,
        questionId: String,
        answer: String,
        timeToAnswer: Int
    ): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val request = SubmitAnswerRequest(
                attemptId = attemptId,
                questionId = questionId,
                answer = answer,
                timeToAnswer = timeToAnswer
            )
            val response = quizAttemptApiService.submitAnswer(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to submit answer: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Complete a quiz attempt and calculate final score
     * PUT /attempts/{id}/complete
     * Returns nested response: {"attempt": {...}, "new_badges": [...], "xp_reward": {...}}
     */
    suspend fun completeAttempt(attemptId: String): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.completeAttempt(attemptId)
            
            if (response.isSuccessful && response.body() != null) {
                // Extract the attempt from the nested response
                Result.success(response.body()!!.attempt)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to complete attempt: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all quiz attempts by the authenticated student
     * GET /attempts
     */
    suspend fun getMyAttempts(): Result<List<QuizAttempt>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getMyAttempts()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get attempts: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get details of a specific quiz attempt
     * GET /attempts/{id}
     */
    suspend fun getAttemptById(attemptId: String): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getAttemptById(attemptId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get attempt: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get detailed XP breakdown for a specific quiz attempt
     * GET /attempts/{id}/xp
     */
    suspend fun getAttemptXpDetails(attemptId: String): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getAttemptXpDetails(attemptId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get XP details: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get the global leaderboard showing top performing students
     * GET /leaderboards/global
     */
    suspend fun getGlobalLeaderboard(): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getGlobalLeaderboard()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get global leaderboard: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get the leaderboard for a specific quiz
     * GET /leaderboards/quiz/{quiz_id}
     */
    suspend fun getQuizLeaderboard(quizId: String): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getQuizLeaderboard(quizId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get leaderboard: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get the authenticated student's rank for a specific quiz
     * GET /leaderboards/quiz/{quiz_id}/my-rank
     */
    suspend fun getMyRankForQuiz(quizId: String): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getMyRank(quizId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get rank: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
