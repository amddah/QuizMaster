package com.example.quizmaster.ui.profile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.Achievement
import com.google.android.material.card.MaterialCardView

/**
 * Adapter for displaying badges in a grid
 */
class BadgesAdapter : ListAdapter<Achievement, BadgesAdapter.BadgeViewHolder>(BadgeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeCard: MaterialCardView = itemView.findViewById(R.id.badgeCard)
        private val badgeEmoji: TextView = itemView.findViewById(R.id.badgeEmoji)
        private val badgeName: TextView = itemView.findViewById(R.id.badgeName)
        private val rarityIndicator: View = itemView.findViewById(R.id.rarityIndicator)
        private val newBadgeIndicator: TextView = itemView.findViewById(R.id.newBadgeIndicator)

        fun bind(achievement: Achievement) {
            badgeEmoji.text = achievement.badgeType.getEmoji()
            badgeName.text = achievement.badgeType.getDisplayName()
            
            // Set rarity color
            val rarity = achievement.badgeType.getRarity()
            rarityIndicator.setBackgroundColor(rarity.color.toInt())
            
            // Show NEW indicator if badge is new
            newBadgeIndicator.visibility = if (achievement.isNew) View.VISIBLE else View.GONE
            
            // Add click listener for details
            badgeCard.setOnClickListener {
                // Show badge details dialog
                showBadgeDetails(achievement)
            }
        }

        private fun showBadgeDetails(achievement: Achievement) {
            // TODO: Show dialog with badge details
            // For now, we'll just show a toast or similar
        }
    }

    private class BadgeDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Adapter for displaying category statistics
 */
class CategoryStatsAdapter : ListAdapter<com.example.quizmaster.data.remote.CategoryStats, CategoryStatsAdapter.CategoryStatsViewHolder>(CategoryStatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryStatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_stat, parent, false)
        return CategoryStatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryStatsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryStatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val quizzesCount: TextView = itemView.findViewById(R.id.quizzesCount)
        private val averageScore: TextView = itemView.findViewById(R.id.averageScore)
        private val expertBadge: TextView = itemView.findViewById(R.id.expertBadge)

        fun bind(categoryStat: com.example.quizmaster.data.remote.CategoryStats) {
            categoryName.text = categoryStat.category.replaceFirstChar { it.uppercase() }
            quizzesCount.text = "${categoryStat.quizzes_completed} quizzes"
            averageScore.text = "${String.format("%.1f", categoryStat.average_score)}%"
            
            // Show expert badge if applicable
            expertBadge.visibility = if (categoryStat.is_expert) View.VISIBLE else View.GONE
            
            // Color code based on performance
            val scoreColor = when {
                categoryStat.average_score >= 90 -> Color.parseColor("#4CAF50") // Green
                categoryStat.average_score >= 75 -> Color.parseColor("#2196F3") // Blue
                categoryStat.average_score >= 60 -> Color.parseColor("#FF9800") // Orange
                else -> Color.parseColor("#F44336") // Red
            }
            averageScore.setTextColor(scoreColor)
        }
    }

    private class CategoryStatsDiffCallback : DiffUtil.ItemCallback<com.example.quizmaster.data.remote.CategoryStats>() {
        override fun areItemsTheSame(oldItem: com.example.quizmaster.data.remote.CategoryStats, newItem: com.example.quizmaster.data.remote.CategoryStats): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: com.example.quizmaster.data.remote.CategoryStats, newItem: com.example.quizmaster.data.remote.CategoryStats): Boolean {
            return oldItem == newItem
        }
    }
}
