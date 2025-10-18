package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.QuizAttempt
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.LeaderboardEntry
import com.example.quizmaster.data.remote.StartAttemptRequest
import com.example.quizmaster.data.remote.SubmitAnswerRequest
import com.example.quizmaster.utils.ScoreCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for quiz attempt operations
 */
class QuizAttemptRepository {
    
    private val quizAttemptApiService = ApiClient.quizAttemptApiService
    
    /**
     * Start a new quiz attempt
     */
    suspend fun startAttempt(quizId: String): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val request = StartAttemptRequest(quiz_id = quizId)
            val response = quizAttemptApiService.startAttempt(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to start attempt: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Submit an answer for a question
     */
    suspend fun submitAnswer(
        attemptId: String,
        questionId: String,
        answer: String,
        timeToAnswer: Int
    ): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val request = SubmitAnswerRequest(
                attempt_id = attemptId,
                question_id = questionId,
                answer = answer,
                time_taken = timeToAnswer
            )
            val response = quizAttemptApiService.submitAnswer(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to submit answer: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Complete a quiz attempt
     */
    suspend fun completeAttempt(attemptId: String): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.completeAttempt(attemptId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to complete attempt: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get my quiz attempts
     */
    suspend fun getMyAttempts(): Result<List<QuizAttempt>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getMyAttempts()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get attempts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get quiz leaderboard
     */
    suspend fun getQuizLeaderboard(quizId: String): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getQuizLeaderboard(quizId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get leaderboard: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get my rank for a quiz
     */
    suspend fun getMyRankForQuiz(quizId: String): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getMyRank(quizId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get rank: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get attempt by ID
     */
    suspend fun getAttemptById(attemptId: String): Result<QuizAttempt> = withContext(Dispatchers.IO) {
        try {
            val response = quizAttemptApiService.getAttemptById(attemptId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get attempt: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
