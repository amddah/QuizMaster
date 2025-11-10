package com.example.quizmaster.ui.professor

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.LeaderboardEntry

class LeaderboardAdapter : ListAdapter<LeaderboardEntry, LeaderboardAdapter.LeaderboardViewHolder>(LeaderboardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_entry, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rankText: TextView = itemView.findViewById(R.id.rankText)
    private val nameText: TextView = itemView.findViewById(R.id.nameText)
        private val percentageText: TextView = itemView.findViewById(R.id.percentageText)
        private val scoreText: TextView = itemView.findViewById(R.id.scoreText)

        fun bind(entry: LeaderboardEntry) {
            // Set rank with colored background for top 3
            rankText.text = "#${entry.rank}"
            
            // Set rank background color
            val background = rankText.background as? GradientDrawable
            background?.setColor(getRankColor(entry.rank))
            
            // Set student name
            nameText.text = entry.studentName
            
            // Set percentage
            percentageText.text = "${entry.percentage.toInt()}%"
            
            // Set score with format "score / maxScore pts"
            scoreText.text = "${entry.score} / ${entry.maxScore}"
        }
        
        private fun getRankColor(rank: Int): Int {
            return when (rank) {
                1 -> Color.parseColor("#FFD700") // Gold
                2 -> Color.parseColor("#C0C0C0") // Silver
                3 -> Color.parseColor("#CD7F32") // Bronze
                else -> Color.parseColor("#4CAF50") // Default green
            }
        }
    }

    private class LeaderboardDiffCallback : DiffUtil.ItemCallback<LeaderboardEntry>() {
        override fun areItemsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
            return oldItem.studentId == newItem.studentId
        }

        override fun areContentsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
            return oldItem == newItem
        }
    }
}
