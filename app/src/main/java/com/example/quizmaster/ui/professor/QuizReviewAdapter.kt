package com.example.quizmaster.ui.professor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R

class QuizReviewAdapter(private val items: List<QuizReviewItem>) :
    RecyclerView.Adapter<QuizReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.questionText)
        val correctAnswerText: TextView = view.findViewById(R.id.correctAnswerText)
        val studentAnswerText: TextView = view.findViewById(R.id.studentAnswerText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_review_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.questionText.text = item.questionText
        val ctx = holder.itemView.context
        holder.correctAnswerText.text = ctx.getString(R.string.lbl_correct, item.correctAnswer)
        holder.studentAnswerText.text = ctx.getString(R.string.lbl_student, item.studentAnswer)
    }

    override fun getItemCount(): Int = items.size
}
