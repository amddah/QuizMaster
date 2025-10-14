package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * Base user model
 */
data class User(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("role")
    val role: UserRole,
    
    @SerializedName("profile_image")
    val profileImage: String? = null,
    
    @SerializedName("created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    // Student-specific fields
    @SerializedName("level")
    val level: Int = 1,
    
    @SerializedName("experience_points")
    val experiencePoints: Int = 0,
    
    @SerializedName("total_quizzes_completed")
    val totalQuizzesCompleted: Int = 0,
    
    @SerializedName("total_score")
    val totalScore: Int = 0,
    
    @SerializedName("badges")
    val badges: List<String> = emptyList(),
    
    // Professor-specific fields
    @SerializedName("department")
    val department: String? = null,
    
    @SerializedName("quizzes_created")
    val quizzesCreated: Int = 0
) {
    fun isStudent(): Boolean = role == UserRole.STUDENT
    fun isProfessor(): Boolean = role == UserRole.PROFESSOR
    
    /**
     * Calculate progress to next level (0-100)
     */
    fun getProgressToNextLevel(): Int {
        val pointsForNextLevel = level * 100
        val pointsInCurrentLevel = experiencePoints % 100
        return (pointsInCurrentLevel * 100) / pointsForNextLevel
    }
}
