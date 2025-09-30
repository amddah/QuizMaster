package com.example.quizmaster.data

enum class QuizCategory(val displayName: String, val apiValue: String) {
    GENERAL("General Knowledge", "9"),
    SCIENCE("Science & Nature", "17"),
    HISTORY("History", "23"),
    TECHNOLOGY("Science: Computers", "18"),
    SPORTS("Sports", "21"),
    ENTERTAINMENT("Entertainment: Film", "11"),
    GEOGRAPHY("Geography", "22"),
    ANIMALS("Animals", "27");
    
    companion object {
        fun fromDisplayName(displayName: String): QuizCategory? {
            return entries.find { it.displayName == displayName }
        }
        
        fun fromApiValue(apiValue: String): QuizCategory? {
            return entries.find { it.apiValue == apiValue }
        }
    }
}