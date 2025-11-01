package com.example.quizmaster.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.remote.BadgeData

/**
 * Simple adapter for displaying badges from backend
 */
class SimpleBadgesAdapter : ListAdapter<BadgeData, SimpleBadgesAdapter.BadgeViewHolder>(BadgeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeIcon: TextView = itemView.findViewById(R.id.badgeEmoji)
        private val badgeName: TextView = itemView.findViewById(R.id.badgeName)

        fun bind(badge: BadgeData) {
            badgeIcon.text = badge.icon
            badgeName.text = badge.name
        }
    }

    private class BadgeDiffCallback : DiffUtil.ItemCallback<BadgeData>() {
        override fun areItemsTheSame(oldItem: BadgeData, newItem: BadgeData): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: BadgeData, newItem: BadgeData): Boolean {
            return oldItem == newItem
        }
    }
}
