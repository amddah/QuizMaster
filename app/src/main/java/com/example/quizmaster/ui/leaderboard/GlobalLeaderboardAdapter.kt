package com.example.quizmaster.ui.leaderboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.XpConstants
import com.example.quizmaster.data.remote.LeaderboardRankEntry
import com.google.android.material.card.MaterialCardView

/**
 * Adapter for global leaderboard entries
 */
class GlobalLeaderboardAdapter : ListAdapter<LeaderboardRankEntry, GlobalLeaderboardAdapter.LeaderboardViewHolder>(LeaderboardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_global_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val leaderboardEntryCard: MaterialCardView = itemView.findViewById(R.id.leaderboardEntryCard)
        private val rankBadge: TextView = itemView.findViewById(R.id.rankBadge)
        private val rankMedal: TextView = itemView.findViewById(R.id.rankMedal)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val levelIndicator: TextView = itemView.findViewById(R.id.levelIndicator)
        private val badgesCount: TextView = itemView.findViewById(R.id.badgesCount)
        private val xpValue: TextView = itemView.findViewById(R.id.xpValue)
        private val averageScore: TextView = itemView.findViewById(R.id.averageScore)

        fun bind(entry: LeaderboardRankEntry) {
            // Show medal for top 3, rank badge for others
            if (entry.rank <= 3) {
                rankBadge.visibility = View.GONE
                rankMedal.visibility = View.VISIBLE
                rankMedal.text = when (entry.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> ""
                }
            } else {
                rankBadge.visibility = View.VISIBLE
                rankMedal.visibility = View.GONE
                rankBadge.text = entry.rank.toString()
            }

            userName.text = entry.user_name
            levelIndicator.text = "Lvl ${entry.level}"
            badgesCount.text = "${entry.badges_count} ${if (entry.badges_count == 1) "badge" else "badges"}"
            xpValue.text = "${entry.total_xp} XP"
            averageScore.text = "${String.format("%.1f", entry.average_score)}% avg"

            // Highlight card for top ranks
            if (entry.rank <= 3) {
                leaderboardEntryCard.strokeWidth = 4
                leaderboardEntryCard.strokeColor = when (entry.rank) {
                    1 -> Color.parseColor("#FFD700") // Gold
                    2 -> Color.parseColor("#C0C0C0") // Silver
                    3 -> Color.parseColor("#CD7F32") // Bronze
                    else -> Color.TRANSPARENT
                }
            } else {
                leaderboardEntryCard.strokeWidth = 0
            }
        }
    }

    private class LeaderboardDiffCallback : DiffUtil.ItemCallback<LeaderboardRankEntry>() {
        override fun areItemsTheSame(oldItem: LeaderboardRankEntry, newItem: LeaderboardRankEntry): Boolean {
            return oldItem.user_id == newItem.user_id
        }

        override fun areContentsTheSame(oldItem: LeaderboardRankEntry, newItem: LeaderboardRankEntry): Boolean {
            return oldItem == newItem
        }
    }
}
