package com.example.quizmaster.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.QuizAttempt
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying quiz attempt history
 */
class QuizHistoryAdapter(
    private val onReviewClick: (QuizAttemptWithQuiz) -> Unit
) : ListAdapter<QuizAttemptWithQuiz, QuizHistoryAdapter.AttemptViewHolder>(AttemptDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttemptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_history, parent, false)
        return AttemptViewHolder(view, onReviewClick)
    }

    override fun onBindViewHolder(holder: AttemptViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttemptViewHolder(
        itemView: View,
        private val onReviewClick: (QuizAttemptWithQuiz) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val card: MaterialCardView = itemView.findViewById(R.id.attemptCard)
        private val quizTitle: TextView = itemView.findViewById(R.id.quizTitle)
        private val scoreText: TextView = itemView.findViewById(R.id.scoreText)
        private val percentageText: TextView = itemView.findViewById(R.id.percentageText)
        private val xpEarned: TextView = itemView.findViewById(R.id.xpEarned)
        private val dateText: TextView = itemView.findViewById(R.id.dateText)
        private val timeTaken: TextView = itemView.findViewById(R.id.timeTaken)
        private val accuracyText: TextView = itemView.findViewById(R.id.accuracyText)
        private val reviewButton: View = itemView.findViewById(R.id.reviewButton)

        fun bind(attemptWithQuiz: QuizAttemptWithQuiz) {
            val attempt = attemptWithQuiz.attempt
            val quiz = attemptWithQuiz.quiz
            
            // Set quiz title
            quizTitle.text = quiz?.title ?: "Quiz #${attempt.quizId.takeLast(8)}"
            
            // Calculate percentage
            val percentage = if (attempt.maxScore > 0) {
                (attempt.totalScore / attempt.maxScore * 100).toInt()
            } else 0
            
            // Set score with color based on performance
            scoreText.text = "${attempt.totalScore.toInt()}/${attempt.maxScore.toInt()}"
            percentageText.text = "$percentage%"
            
            // Color code based on performance
            val color = when {
                percentage >= 90 -> android.graphics.Color.parseColor("#4CAF50") // Green
                percentage >= 70 -> android.graphics.Color.parseColor("#FF9800") // Orange
                else -> android.graphics.Color.parseColor("#F44336") // Red
            }
            percentageText.setTextColor(color)
            
            // XP earned
            xpEarned.text = "+${attempt.xpEarned} XP"
            
            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = try {
                // Parse ISO date string
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                isoFormat.parse(attempt.completedAt ?: attempt.startedAt)
            } catch (e: Exception) {
                Date()
            }
            dateText.text = dateFormat.format(date)
            
            // Time taken
            val minutes = attempt.timeTaken / 60
            val seconds = attempt.timeTaken % 60
            timeTaken.text = if (minutes > 0) {
                "${minutes}m ${seconds}s"
            } else {
                "${seconds}s"
            }
            
            // Calculate accuracy
            val correctAnswers = attempt.answers?.count { it.isCorrect } ?: 0
            val totalAnswers = attempt.answers?.size ?: 0
            val accuracy = if (totalAnswers > 0) {
                (correctAnswers.toFloat() / totalAnswers * 100).toInt()
            } else 0
            accuracyText.text = "$correctAnswers/$totalAnswers correct ($accuracy%)"
            
            // Review button click listener
            reviewButton.setOnClickListener {
                onReviewClick(attemptWithQuiz)
            }
        }
    }

    private class AttemptDiffCallback : DiffUtil.ItemCallback<QuizAttemptWithQuiz>() {
        override fun areItemsTheSame(oldItem: QuizAttemptWithQuiz, newItem: QuizAttemptWithQuiz): Boolean {
            return oldItem.attempt.id == newItem.attempt.id
        }

        override fun areContentsTheSame(oldItem: QuizAttemptWithQuiz, newItem: QuizAttemptWithQuiz): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Data class combining attempt with quiz info
 */
data class QuizAttemptWithQuiz(
    val attempt: QuizAttempt,
    val quiz: com.example.quizmaster.data.model.QuizModel? = null
)
