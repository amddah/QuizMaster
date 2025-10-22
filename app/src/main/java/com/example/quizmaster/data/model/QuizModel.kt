package com.example.quizmaster.data.model

import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.google.gson.annotations.SerializedName

/**
 * Approval status for quizzes
 */
enum class ApprovalStatus(val apiValue: String) {
    PENDING("pending"),      // Waiting for professor approval
    APPROVED("approved"),     // Approved and available to students
    REJECTED("rejected");     // Rejected by professor

    companion object {
        fun fromString(status: String): ApprovalStatus? {
            return entries.find { it.name.equals(status, ignoreCase = true) }
        }
    }
}

/**
 * Enhanced quiz model with creator info, course linkage, and approval status
 */
data class QuizModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("category")
    val category: QuizCategory,
    
    @SerializedName("difficulty")
    val difficulty: QuizDifficulty,
    
    @SerializedName("questions")
    val questions: List<QuestionModel>,
    
    @SerializedName("creator_id")
    val creatorId: String,
    
    @SerializedName("creator_name")
    val creatorName: String,
    
    @SerializedName("creator_role")
    val creatorRole: UserRole,
    
    @SerializedName("linked_course_id")
    val linkedCourseId: String,  // Course that must be completed to access this quiz
    
    @SerializedName("linked_course_name")
    val linkedCourseName: String,
    
    @SerializedName("approval_status")
    val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,
    
    @SerializedName("approved_by")
    val approvedBy: String? = null,
    
    @SerializedName("created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @SerializedName("times_attempted")
    val timesAttempted: Int = 0,
    
    @SerializedName("average_score")
    val averageScore: Double = 0.0,
    
    @SerializedName("total_questions")
    val totalQuestions: Int = questions.size,
    
    @SerializedName("time_limit_per_question")
    val timeLimitPerQuestion: Int = 15  // seconds
) {
    fun isApproved(): Boolean = approvalStatus == ApprovalStatus.APPROVED
    fun isPending(): Boolean = approvalStatus == ApprovalStatus.PENDING
    fun isRejected(): Boolean = approvalStatus == ApprovalStatus.REJECTED
    fun isCreatedByProfessor(): Boolean = creatorRole == UserRole.PROFESSOR
    fun isCreatedByStudent(): Boolean = creatorRole == UserRole.STUDENT
}
