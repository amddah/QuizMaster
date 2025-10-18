package com.example.quizmaster.data

enum class QuizDifficulty(val displayName: String, val apiValue: String) {
    EASY("Easy", "easy"),
    MEDIUM("Medium", "medium"),
    HARD("Hard", "hard");
    
    companion object {
        fun fromDisplayName(displayName: String): QuizDifficulty? {
            return entries.find { it.displayName == displayName }
        }
        
        fun fromApiValue(apiValue: String): QuizDifficulty? {
            return entries.find { it.apiValue == apiValue }
        }
        
        fun fromString(difficultyString: String): QuizDifficulty? {
            return entries.find {
                it.name.equals(difficultyString, ignoreCase = true) ||
                it.displayName.equals(difficultyString, ignoreCase = true) ||
                it.apiValue.equals(difficultyString, ignoreCase = true)
            }
        }
    }
}