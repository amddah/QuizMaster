package com.example.quizmaster.ui.quiz

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.QuestionModel

class QuestionAdapter(
    private val questions: MutableList<QuestionModel>,
    private val onDelete: (Int) -> Unit,
    private val onEdit: (Int) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val questionType: TextView = itemView.findViewById(R.id.questionType)
        private val deleteButton: androidx.appcompat.widget.AppCompatImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(question: QuestionModel) {
            questionText.text = question.questionText
            // Show type + extras (options count and points) to give more info in the item UI
            questionType.text = "${question.type.name} • ${question.options.size} options • ${question.maxScore} pts"

            // Accessibility: describe the delete action
            deleteButton.contentDescription = itemView.context.getString(R.string.delete_question)

            deleteButton.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                // Prevent double-clicks
                deleteButton.isEnabled = false

                // Confirmation dialog before deletion
                android.app.AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.confirm_delete_title))
                    .setMessage(itemView.context.getString(R.string.confirm_delete_message))
                    .setPositiveButton(itemView.context.getString(R.string.delete)) { _, _ ->
                        onDelete(pos)
                    }
                    .setNegativeButton(itemView.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        // Re-enable after dialog closes
                        deleteButton.isEnabled = true
                    }
                    .show()
            }

            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onEdit(pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount() = questions.size
}

