package com.example.quizmaster.ui.professor

import android.graphics.Typeface
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
        val questionNumber: TextView = view.findViewById(R.id.questionNumber)
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
        holder.questionNumber.text = (position + 1).toString()
        holder.questionText.text = item.questionText

        // Clear previous options
        holder.optionsContainer.removeAllViews()

        // Add each option as a TextView with better styling
        for (option in item.options) {
            val optionView = TextView(holder.itemView.context).apply {
                text = "•  $option"
                textSize = 14f
                setPadding(8, 8, 8, 8)
                // Default text color
                setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
                // Slightly rounded background for each option to improve readability
                setBackgroundResource(R.drawable.input_border)
                // Ensure layout params have margin between options
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 6, 0, 6)
                layoutParams = params
            }

            // Highlight correct answer (case-insensitive, trim whitespace)
            val correct = item.correctAnswer
            if (!correct.isNullOrEmpty() && option.trim().equals(correct.trim(), ignoreCase = true)) {
                // Mark correct option green and bold
                optionView.setTextColor(holder.itemView.context.getColor(R.color.correct_answer))
                optionView.setTypeface(optionView.typeface, Typeface.BOLD)
            }

            holder.optionsContainer.addView(optionView)
        }
    }

    override fun getItemCount(): Int = items.size
}
