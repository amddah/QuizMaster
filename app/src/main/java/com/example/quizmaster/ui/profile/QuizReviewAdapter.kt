package com.example.quizmaster.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.google.android.material.card.MaterialCardView

/**
 * Adapter for displaying quiz review items (questions with answers)
 */
class QuizReviewAdapter : ListAdapter<QuizReviewItem, QuizReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val card: MaterialCardView = itemView.findViewById(R.id.reviewCard)
        private val questionNumber: TextView = itemView.findViewById(R.id.questionNumber)
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val studentAnswerLabel: TextView = itemView.findViewById(R.id.studentAnswerLabel)
        private val studentAnswer: TextView = itemView.findViewById(R.id.studentAnswer)
        private val correctAnswerLabel: TextView = itemView.findViewById(R.id.correctAnswerLabel)
        private val correctAnswer: TextView = itemView.findViewById(R.id.correctAnswer)
        private val pointsText: TextView = itemView.findViewById(R.id.pointsText)

        fun bind(item: QuizReviewItem) {
            // Question number and text
            questionNumber.text = "Question ${item.questionNumber}"
            questionText.text = item.questionText
            
            // Student answer
            studentAnswer.text = item.studentAnswer
            
            // Correct answer
            correctAnswer.text = item.correctAnswer
            
            // Points
            pointsText.text = "${item.pointsEarned}/${item.questionPoints} pts"
            
            // Color coding based on correctness
            if (item.isCorrect) {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5E9")) // Light green
                studentAnswerLabel.text = "✓ Your Answer (Correct)"
                studentAnswerLabel.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                correctAnswerLabel.visibility = View.GONE
                correctAnswer.visibility = View.GONE
                pointsText.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            } else {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#FFEBEE")) // Light red
                studentAnswerLabel.text = "✗ Your Answer (Incorrect)"
                studentAnswerLabel.setTextColor(android.graphics.Color.parseColor("#F44336"))
                correctAnswerLabel.visibility = View.VISIBLE
                correctAnswer.visibility = View.VISIBLE
                correctAnswerLabel.text = "✓ Correct Answer:"
                correctAnswerLabel.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                pointsText.setTextColor(android.graphics.Color.parseColor("#F44336"))
            }
        }
    }

    private class ReviewDiffCallback : DiffUtil.ItemCallback<QuizReviewItem>() {
        override fun areItemsTheSame(oldItem: QuizReviewItem, newItem: QuizReviewItem): Boolean {
            return oldItem.questionNumber == newItem.questionNumber
        }

        override fun areContentsTheSame(oldItem: QuizReviewItem, newItem: QuizReviewItem): Boolean {
            return oldItem == newItem
        }
    }
}
