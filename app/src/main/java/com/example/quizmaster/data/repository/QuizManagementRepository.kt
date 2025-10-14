package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.*
import com.example.quizmaster.data.remote.QuizApiService
import com.example.quizmaster.data.remote.QuizAttemptApiService
import com.example.quizmaster.data.remote.CourseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing quiz operations
 */
class QuizManagementRepository(
    private val quizApiService: QuizApiService,
    private val attemptApiService: QuizAttemptApiService,
    private val courseApiService: CourseApiService
) {
    
    // Quiz CRUD operations
    
    suspend fun getAllQuizzes(
        category: String? = null,
        difficulty: String? = null,
        status: ApprovalStatus? = null
    ): Result<List<QuizModel>> = withContext(Dispatchers.IO) {
        try {
            val response = quizApiService.getAllQuizzes(category, difficulty, status)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch quizzes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizById(quizId: String): Result<QuizModel> = withContext(Dispatchers.IO) {
        try {
            val response = quizApiService.getQuizById(quizId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createQuiz(token: String, quiz: QuizModel): Result<QuizModel> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.createQuiz(token, quiz)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to create quiz: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun updateQuiz(quizId: String, token: String, quiz: QuizModel): Result<QuizModel> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.updateQuiz(quizId, token, quiz)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to update quiz: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun deleteQuiz(quizId: String, token: String): Result<Unit> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.deleteQuiz(quizId, token)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to delete quiz: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    // Professor operations
    
    suspend fun getPendingQuizzes(token: String): Result<List<QuizModel>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.getPendingQuizzes(token)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch pending quizzes: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun approveQuiz(quizId: String, token: String): Result<QuizModel> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.approveQuiz(quizId, token)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to approve quiz: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun rejectQuiz(quizId: String, token: String, reason: String): Result<QuizModel> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.rejectQuiz(quizId, token, mapOf("reason" to reason))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to reject quiz: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    // Student operations
    
    suspend fun getAvailableQuizzesForStudent(
        studentId: String,
        token: String
    ): Result<List<QuizModel>> = withContext(Dispatchers.IO) {
        try {
            val response = quizApiService.getAvailableQuizzesForStudent(studentId, token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch available quizzes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkCourseCompletion(
        studentId: String,
        courseId: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = CourseCompletionRequest(studentId, courseId)
            val response = courseApiService.checkCourseCompletion(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.completed)
            } else {
                Result.failure(Exception("Failed to check course completion: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizzesCreatedByUser(
        userId: String,
        token: String
    ): Result<List<QuizModel>> = withContext(Dispatchers.IO) {
        try {
            val response = quizApiService.getQuizzesCreatedByUser(userId, token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user quizzes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
