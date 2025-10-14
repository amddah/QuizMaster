package com.example.quizmaster.data.model

/**
 * Enum representing user roles in the system
 */
enum class UserRole {
    STUDENT,
    PROFESSOR;
    
    companion object {
        fun fromString(role: String): UserRole? {
            return entries.find { it.name.equals(role, ignoreCase = true) }
        }
    }
}
