package com.example.quizmaster.ui.professor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.QuizWithAttempts

class ProfessorQuizzesAdapter(
    private val onQuizClick: (QuizWithAttempts) -> Unit
) : ListAdapter<QuizWithAttempts, ProfessorQuizzesAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_professor_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position), onQuizClick)
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quizTitleText: TextView = itemView.findViewById(R.id.quizTitleText)
        private val quizCategoryText: TextView = itemView.findViewById(R.id.quizCategoryText)
        private val attemptCountText: TextView = itemView.findViewById(R.id.attemptCountText)
        private val viewLeaderboardButton: Button = itemView.findViewById(R.id.viewLeaderboardButton)

        fun bind(quizWithAttempts: QuizWithAttempts, onQuizClick: (QuizWithAttempts) -> Unit) {
            val quiz = quizWithAttempts.quiz
            
            quizTitleText.text = quiz.title
            quizCategoryText.text = quiz.category
            attemptCountText.text = quizWithAttempts.attemptCount.toString()
            
            // Enable button only if there are attempts
            val hasAttempts = quizWithAttempts.attemptCount > 0
            viewLeaderboardButton.isEnabled = hasAttempts
            viewLeaderboardButton.alpha = if (hasAttempts) 1.0f else 0.5f
            
            // Set click listener
            if (hasAttempts) {
                viewLeaderboardButton.setOnClickListener {
                    onQuizClick(quizWithAttempts)
                }
            } else {
                viewLeaderboardButton.setOnClickListener(null)
            }
        }
    }

    private class QuizDiffCallback : DiffUtil.ItemCallback<QuizWithAttempts>() {
        override fun areItemsTheSame(oldItem: QuizWithAttempts, newItem: QuizWithAttempts): Boolean {
            return oldItem.quiz.id == newItem.quiz.id
        }

        override fun areContentsTheSame(oldItem: QuizWithAttempts, newItem: QuizWithAttempts): Boolean {
            return oldItem == newItem
        }
    }
}
