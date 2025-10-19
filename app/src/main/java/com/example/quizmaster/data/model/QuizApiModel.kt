package com.example.quizmaster.data.model

import com.google.gson.annotations.SerializedName
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.data.model.QuestionModel
import com.example.quizmaster.data.model.QuizModel

/**
 * API response model that matches the actual quiz API response structure
 */
data class QuizApiModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("difficulty_level")
    val difficultyLevel: String,
    
    @SerializedName("course_id")
    val courseId: String,
    
    @SerializedName("creator_id")
    val creatorId: String,
    
    @SerializedName("creator_role")
    val creatorRole: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("questions")
    val questions: List<QuestionApiModel>,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("approved_by")
    val approvedBy: String? = null
)

/**
 * API response model for questions
 */
data class QuestionApiModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("question_text")
    val questionText: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("options")
    val options: List<String>? = null,
    
    @SerializedName("correct_answer")
    val correctAnswer: String,
    
    @SerializedName("time_limit")
    val timeLimit: Int,
    
    @SerializedName("points")
    val points: Int,
    
    @SerializedName("order")
    val order: Int
)

/**
 * Extension functions to convert API models to app models
 */
fun QuizApiModel.toQuizModel(): QuizModel {
    return QuizModel(
        id = this.id,
        title = this.title,
        description = this.description,
        category = com.example.quizmaster.data.QuizCategory.fromString(this.category)
            ?: com.example.quizmaster.data.QuizCategory.PROGRAMMING,
        difficulty = com.example.quizmaster.data.QuizDifficulty.fromString(this.difficultyLevel)
            ?: com.example.quizmaster.data.QuizDifficulty.EASY,
        questions = this.questions.map { it.toQuestionModel() },
        creatorId = this.creatorId,
        creatorName = "Unknown", // API doesn't provide creator name
        creatorRole = if (this.creatorRole.lowercase() == "professor") UserRole.PROFESSOR else UserRole.STUDENT,
        linkedCourseId = this.courseId,
        linkedCourseName = this.courseId, // Use course ID as name for now
        approvalStatus = ApprovalStatus.fromString(this.status) ?: ApprovalStatus.PENDING,
        approvedBy = this.approvedBy,
        createdAt = System.currentTimeMillis(), // API returns string, we need long
        updatedAt = System.currentTimeMillis()
    )
}

fun QuestionApiModel.toQuestionModel(): QuestionModel {
    return QuestionModel(
        id = this.id,
        questionText = this.questionText,
        type = when (this.type.lowercase()) {
            "multiple_choice" -> QuestionType.MULTIPLE_CHOICE
            "true_false" -> QuestionType.TRUE_FALSE
            else -> QuestionType.MULTIPLE_CHOICE
        },
        correctAnswer = this.correctAnswer,
        options = this.options ?: emptyList(),
        timeLimit = this.timeLimit,
        maxScore = this.points,
        order = this.order
    )
}

// --- Conversion dans l'autre sens : de nos modèles d'application vers le modèle attendu par l'API ---
fun QuestionModel.toQuestionApiModel(): QuestionApiModel {
    return QuestionApiModel(
        id = this.id,
        questionText = this.questionText,
        type = when (this.type) {
            QuestionType.MULTIPLE_CHOICE -> "multiple_choice"
            QuestionType.TRUE_FALSE -> "true_false"
        },
        options = if (this.options.isEmpty()) null else this.options,
        correctAnswer = this.correctAnswer,
        timeLimit = this.timeLimit,
        points = this.maxScore,
        order = this.order
    )
}

fun QuizModel.toApiModel(): QuizApiModel {
    return QuizApiModel(
        id = this.id,
        title = this.title,
        description = this.description,
        // Use API-specific values (apiValue) when available so the backend receives expected tokens
        category = this.category.apiValue,
        difficultyLevel = this.difficulty.apiValue,
        courseId = this.linkedCourseId,
        creatorId = this.creatorId,
        creatorRole = this.creatorRole.name.lowercase(),
        status = this.approvalStatus.name.lowercase(),
        questions = this.questions.map { it.toQuestionApiModel() },
        createdAt = System.currentTimeMillis().toString(),
        updatedAt = System.currentTimeMillis().toString(),
        approvedBy = this.approvedBy
    )
}
