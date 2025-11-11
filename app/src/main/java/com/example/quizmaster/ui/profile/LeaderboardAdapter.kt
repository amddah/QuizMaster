package com.example.quizmaster.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.LeaderboardEntry

class LeaderboardAdapter : ListAdapter<LeaderboardEntry, LeaderboardAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<LeaderboardEntry>() {
            override fun areItemsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
                return oldItem.studentId == newItem.studentId && oldItem.rank == newItem.rank
            }

            override fun areContentsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rankText: TextView = itemView.findViewById(R.id.rankText)
    private val nameText: TextView = itemView.findViewById(R.id.nameText)
    private val percentageText: TextView = itemView.findViewById(R.id.percentageText)
    private val scoreText: TextView = itemView.findViewById(R.id.scoreText)

        fun bind(entry: LeaderboardEntry) {
            rankText.text = entry.rank.toString()
            nameText.text = entry.studentName
            percentageText.text = "${entry.percentage.toInt()}%"
            scoreText.text = "${entry.score.toInt()} pts"

            // simple fade-in animation for gamified feel
            itemView.alpha = 0f
            itemView.animate().alpha(1f).setDuration(300).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
