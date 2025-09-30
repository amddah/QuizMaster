package com.example.quizmaster.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.data.QuizResult
import com.example.quizmaster.databinding.ItemQuizResultBinding
import java.text.SimpleDateFormat
import java.util.*

class QuizHistoryAdapter : ListAdapter<QuizResult, QuizHistoryAdapter.ViewHolder>(QuizResultDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuizResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(private val binding: ItemQuizResultBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
        
        fun bind(result: QuizResult) {
            binding.textViewScore.text = "${result.percentage}%"
            binding.textViewCategory.text = result.category.displayName
            binding.textViewDifficulty.text = "${result.difficulty.displayName} • ${result.score}/${result.totalQuestions} questions"
            binding.textViewDate.text = formatDate(result.completedAt)
        }
        
        private fun formatDate(date: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val today = Calendar.getInstance()
            
            return when {
                isSameDay(calendar, today) -> {
                    "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)}"
                }
                isYesterday(calendar, today) -> {
                    "Yesterday, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)}"
                }
                else -> {
                    dateFormat.format(date)
                }
            }
        }
        
        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                   cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
        
        private fun isYesterday(cal1: Calendar, today: Calendar): Boolean {
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_YEAR, -1)
            return isSameDay(cal1, yesterday)
        }
    }
}

class QuizResultDiffCallback : DiffUtil.ItemCallback<QuizResult>() {
    override fun areItemsTheSame(oldItem: QuizResult, newItem: QuizResult): Boolean {
        return oldItem.completedAt == newItem.completedAt
    }
    
    override fun areContentsTheSame(oldItem: QuizResult, newItem: QuizResult): Boolean {
        return oldItem == newItem
    }
}