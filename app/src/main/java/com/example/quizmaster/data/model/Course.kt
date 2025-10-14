package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model representing a course in the external system
 */
data class Course(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("description")
    val description: String? = null
)

/**
 * Request to check course completion status
 */
data class CourseCompletionRequest(
    @SerializedName("student_id")
    val studentId: String,
    
    @SerializedName("course_id")
    val courseId: String
)

/**
 * Response from course completion check
 */
data class CourseCompletionResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("completed")
    val completed: Boolean,
    
    @SerializedName("completion_date")
    val completionDate: Long? = null,
    
    @SerializedName("grade")
    val grade: String? = null,
    
    @SerializedName("message")
    val message: String? = null
)
