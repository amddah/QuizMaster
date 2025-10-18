package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.CourseCompletionRequest
import com.example.quizmaster.data.model.CourseCompletionResponse
import com.example.quizmaster.data.model.Course
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API service for external course system integration
 */
interface CourseApiService {
    
    /**
     * Check if a student has completed a specific course
     */
    @POST("courses/check-completion")
    suspend fun checkCourseCompletion(
        @Body request: CourseCompletionRequest
    ): Response<CourseCompletionResponse>
    
    /**
     * Get list of courses completed by a student
     */
    @GET("students/{studentId}/completed-courses")
    suspend fun getCompletedCourses(
        @Path("studentId") studentId: String
    ): Response<List<Course>>
    
    /**
     * Get course details by ID
     */
    @GET("courses/{courseId}")
    suspend fun getCourseById(
        @Path("courseId") courseId: String
    ): Response<Course>
    
    /**
     * Get all available courses
     */
    @GET("courses")
    suspend fun getAllCourses(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<Course>>
}
