package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName

/**
 * User model matching the API specification
 */
data class User(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("first_name")
    val firstName: String,
    
    @SerializedName("last_name")
    val lastName: String,
    
    @SerializedName("role")
    val role: UserRole,  // Now properly deserialized from lowercase strings

    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    // Extended fields for gamification (not in base API)
    @SerializedName("level")
    val level: Int = 1,
    
    @SerializedName("xp")
    val xp: Int = 0,
    
    @SerializedName("total_quizzes_completed")
    val totalQuizzesCompleted: Int = 0,
    
    @SerializedName("badges")
       val badges: List<Badge> = emptyList(),
    
    @SerializedName("department")
    val department: String? = null
) {
    /**
     * Badge model matching backend response
     */
    data class Badge(
       @SerializedName("name")
       val name: String,
       @SerializedName("description")
       val description: String? = null,
       @SerializedName("icon")
       val icon: String? = null,
       @SerializedName("type")
       val type: String? = null,
       @SerializedName("earned_at")
       val earnedAt: String? = null
    )
    fun isStudent(): Boolean = role == UserRole.STUDENT
    fun isProfessor(): Boolean = role == UserRole.PROFESSOR
    
    val fullName: String
        get() = "$firstName $lastName"
    
    /**
     * Calculate progress to next level (0-100)
     */
    fun getProgressToNextLevel(): Int {
        val pointsForCurrentLevel = (level - 1) * 100
        val pointsForNextLevel = level * 100
        val pointsInCurrentLevel = xp - pointsForCurrentLevel
        
        return if (pointsForNextLevel > pointsForCurrentLevel) {
            (pointsInCurrentLevel * 100) / (pointsForNextLevel - pointsForCurrentLevel)
        } else {
            0
        }
    }
    
    /**
     * Calculate XP needed for next level
     */
    fun getXpToNextLevel(): Int {
        val pointsForNextLevel = level * 100
        return maxOf(0, pointsForNextLevel - xp)
    }
}
