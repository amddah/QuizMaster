package com.example.quizmaster.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.QuizModel

class QuizStatusAdapter : ListAdapter<QuizModel, QuizStatusAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<QuizModel>() {
            override fun areItemsTheSame(oldItem: QuizModel, newItem: QuizModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: QuizModel, newItem: QuizModel): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.quizTitle)
        private val status: TextView = view.findViewById(R.id.quizStatus)
        private val subtitle: TextView? = view.findViewById(R.id.quizSubtitle)
        private val meta: TextView? = view.findViewById(R.id.quizMeta)

        fun bind(item: QuizModel) {
            android.util.Log.d("QuizStatusAdapter", "bind quiz=${item.id} title=${item.title}")
            title.text = item.title
            subtitle?.text = "${item.linkedCourseName} • ${item.difficulty.displayName}"
            try {
                val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                meta?.text = sdf.format(java.util.Date(item.createdAt))
            } catch (e: Exception) {
                // ignore formatting errors
                meta?.text = ""
            }
            when (item.approvalStatus) {
                com.example.quizmaster.data.model.ApprovalStatus.APPROVED -> {
                    status.text = "Approved"
                    status.setBackgroundResource(R.drawable.status_chip_approved)
                }
                com.example.quizmaster.data.model.ApprovalStatus.REJECTED -> {
                    status.text = "Rejected"
                    status.setBackgroundResource(R.drawable.status_chip_rejected)
                }
                else -> {
                    status.text = "Pending"
                    status.setBackgroundResource(R.drawable.status_chip_pending)
                }
            }
            // Open quiz review when clicking an item
            itemView.setOnClickListener {
                try {
                    val ctx = itemView.context
                    val intent = com.example.quizmaster.ui.professor.QuizReviewActivity.createIntent(
                        ctx,
                        item.id,
                        item.description
                    )
                    ctx.startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.d("QuizStatusAdapter", "Failed to start QuizReviewActivity: ${e.message}")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz_status, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
