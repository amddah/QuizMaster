package com.example.quizmaster.data.local

import com.example.quizmaster.data.Quiz
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty

object OfflineQuestions {
    /**
     * Les questions locales ont été désactivées pour éviter l'utilisation de données stockées localement.
     * Si une partie du code tente encore d'appeler cette méthode, elle lèvera une exception explicite
     * afin que l'appelant bascule sur la récupération via l'API distante.
     */
    fun getQuizForCategory(category: QuizCategory, difficulty: QuizDifficulty): Quiz {
        throw IllegalStateException("OfflineQuestions disabled: local/offline quiz data removed. Use remote API or pass full quiz JSON to QuizActivity.")
    }
}