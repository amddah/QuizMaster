package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.CourseCompletionRequest
import com.example.quizmaster.data.model.CourseCompletionResponse
import com.example.quizmaster.data.model.Course
import com.example.quizmaster.data.remote.CourseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for course-related operations
 */
class CourseRepository(private val courseApiService: CourseApiService) {
    
    /**
     * Check if a student has completed a specific course
     */
    suspend fun checkCourseCompletion(
        studentId: String,
        courseId: String
    ): Result<CourseCompletionResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CourseCompletionRequest(studentId, courseId)
            val response = courseApiService.checkCourseCompletion(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Course completion check failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all courses completed by a student
     */
    suspend fun getCompletedCourses(studentId: String): Result<List<Course>> = withContext(Dispatchers.IO) {
        try {
            val response = courseApiService.getCompletedCourses(studentId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch completed courses: ${response.code()}"))
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
                Result.failure(Exception("Course not found: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all available courses
     */
    suspend fun getAllCourses(page: Int = 1, limit: Int = 20): Result<List<Course>> = withContext(Dispatchers.IO) {
        try {
            val response = courseApiService.getAllCourses(page, limit)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch courses: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
