package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.*
import com.example.quizmaster.data.remote.QuizApiService
import com.example.quizmaster.data.remote.CourseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing quiz operations
 */
class QuizManagementRepository(
    private val quizApiService: QuizApiService,
    private val courseApiService: CourseApiService
) {
    
    // Quiz CRUD operations
    
    suspend fun getAllQuizzes(
        category: String? = null,
        difficulty: String? = null,
        status: ApprovalStatus? = null
    ): Result<List<QuizModel>> = withContext(Dispatchers.IO) {
        try {
            // Normalize incoming filters to lowercase to match backend expectations
            val categoryNormalized = category?.lowercase()
            val difficultyNormalized = difficulty?.lowercase()
            val statusString = status?.name?.lowercase()
            val response = quizApiService.getAllQuizzes(categoryNormalized, difficultyNormalized, statusString)
            if (response.isSuccessful && response.body() != null) {
                val convertedQuizzes = response.body()!!.map { it.toQuizModel() }
                Result.success(convertedQuizzes)
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
                val convertedQuiz = response.body()!!.toQuizModel()
                Result.success(convertedQuiz)
            } else {
                Result.failure(Exception("Failed to fetch quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createQuiz(quiz: QuizModel): Result<QuizModel> = 
        withContext(Dispatchers.IO) {
            try {
                // Use API model to match backend contract and convert the API response back to our app model
                val response = quizApiService.createQuiz(quiz.toApiModel())
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.toQuizModel())
                } else {
                    Result.failure(Exception("Failed to create quiz: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun deleteQuiz(quizId: String): Result<Unit> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.deleteQuiz(quizId)
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
    
    suspend fun approveQuiz(quizId: String): Result<QuizModel> = 
        withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.approveQuiz(quizId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.toQuizModel())
                } else {
                    Result.failure(Exception("Failed to approve quiz: ${response.message()}"))
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
    
    /**
     * Get available courses for selection
     */
    suspend fun getAllCourses(): Result<List<Course>> = withContext(Dispatchers.IO) {
        try {
            val response = courseApiService.getAllCourses()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch courses: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get course details by ID
     */
    suspend fun getCourseById(courseId: String): Result<Course> = withContext(Dispatchers.IO) {
        try {
            val response = courseApiService.getCourseById(courseId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch course: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
