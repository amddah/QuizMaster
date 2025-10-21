package com.example.quizmaster.ui.professor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R

/**
 * Quiz Review activity that receives a quiz_id via intent extras and optionally a
 * Parcelable ArrayList<QuizReviewItem> under the key "review_items".
 */
class QuizReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review)

        val quizId = intent.getStringExtra("quiz_id") ?: ""
        val quizIdText = findViewById<TextView>(R.id.quizIdText)
        quizIdText.text = getString(R.string.quiz_review_title_format, quizId)

        val quizDescriptionText = findViewById<TextView>(R.id.quizDescriptionText)
        val description = intent.getStringExtra("quiz_description")
        if (!description.isNullOrBlank()) {
            quizDescriptionText.text = description
            quizDescriptionText.visibility = View.VISIBLE
        } else {
            quizDescriptionText.visibility = View.GONE
        }

        val recycler = findViewById<RecyclerView>(R.id.reviewRecycler)
        val noDataText = findViewById<TextView>(R.id.noDataText)

        recycler.layoutManager = LinearLayoutManager(this)

        // Attempt to read passed review items (Parcelable ArrayList)
        val items = mutableListOf<QuizReviewItem>()
        val passed: ArrayList<QuizReviewItem>? = intent.getParcelableArrayListExtra("review_items")
        if (passed != null && passed.isNotEmpty()) {
            items.addAll(passed)
        }

        // If nothing was passed, use fallback sample data
        if (items.isEmpty()) {
            items.add(
                QuizReviewItem(
                    questionText = "What is the capital of France?",
                    options = listOf("Paris", "London", "Berlin", "Madrid")
                )
            )
            items.add(
                QuizReviewItem(
                    questionText = "2 + 2 = ?",
                    options = listOf("3", "4", "5", "6")
                )
            )
            items.add(
                QuizReviewItem(
                    questionText = "Is Kotlin interoperable with Java?",
                    options = listOf("Yes", "No")
                )
            )
        }

        if (items.isEmpty()) {
            recycler.visibility = View.GONE
            noDataText.visibility = View.VISIBLE
        } else {
            noDataText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            val adapter = QuizReviewAdapter(items)
            recycler.adapter = adapter
        }
    }

    companion object {
        fun createIntent(
            context: Context,
            quizId: String,
            description: String? = null,
            reviewItems: ArrayList<QuizReviewItem>? = null
        ): Intent {
            val intent = Intent(context, QuizReviewActivity::class.java)
            intent.putExtra("quiz_id", quizId)
            if (!description.isNullOrBlank()) intent.putExtra("quiz_description", description)
            if (reviewItems != null && reviewItems.isNotEmpty()) {
                intent.putParcelableArrayListExtra("review_items", reviewItems)
            }
            return intent
        }
    }
}
