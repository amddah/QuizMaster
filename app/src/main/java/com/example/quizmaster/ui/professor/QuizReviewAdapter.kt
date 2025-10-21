package com.example.quizmaster.ui.professor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R

class QuizReviewAdapter(private val items: List<QuizReviewItem>) :
    RecyclerView.Adapter<QuizReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.questionText)
        val optionsContainer: LinearLayout = view.findViewById(R.id.optionsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_review_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.questionText.text = item.questionText

        // Clear previous options
        holder.optionsContainer.removeAllViews()

        // Add each option as a TextView
        for (option in item.options) {
            val optionView = TextView(holder.itemView.context).apply {
                text = "• $option"
                textSize = 14f
                setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
                setPadding(0, 4, 0, 4)
            }
            holder.optionsContainer.addView(optionView)
        }
    }

    override fun getItemCount(): Int = items.size
}
