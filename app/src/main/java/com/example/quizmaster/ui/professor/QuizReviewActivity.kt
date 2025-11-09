package com.example.quizmaster.ui.professor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.UserRole
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * Quiz Review activity that receives a quiz_id via intent extras and optionally a
 * Parcelable ArrayList<QuizReviewItem> under the key "review_items".
 */
class QuizReviewActivity : AppCompatActivity() {
    private lateinit var sessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review)

        sessionManager = UserSessionManager.getInstance(this)

        val quizId = intent.getStringExtra("quiz_id") ?: ""
        val quizIdText = findViewById<TextView>(R.id.quizIdText)
        // show a neutral title while loading; will be replaced by API title when available
        quizIdText.text = getString(R.string.reviewing_quiz)

        val quizDescriptionText = findViewById<TextView>(R.id.quizDescriptionText)
        val intentDescription = intent.getStringExtra("quiz_description")
        if (!intentDescription.isNullOrBlank()) {
            quizDescriptionText.text = intentDescription
            quizDescriptionText.visibility = View.VISIBLE
        } else {
            quizDescriptionText.visibility = View.GONE
        }

        val recycler = findViewById<RecyclerView>(R.id.reviewRecycler)
        val noDataText = findViewById<TextView>(R.id.noDataText)
        val loadingBar = findViewById<ProgressBar>(R.id.loadingProgressBar)
        val errorContainer = findViewById<View>(R.id.errorContainer)
        val errorText = findViewById<TextView>(R.id.errorText)
        val retryButton = findViewById<Button>(R.id.retryButton)

        recycler.layoutManager = LinearLayoutManager(this)

        fun showLoading(loading: Boolean) {
            loadingBar.visibility = if (loading) View.VISIBLE else View.GONE
            if (loading) {
                errorContainer.visibility = View.GONE
                noDataText.visibility = View.GONE
                recycler.visibility = View.GONE
            }
        }

        fun showError(message: String) {
            errorText.text = message
            errorContainer.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            noDataText.visibility = View.GONE
        }

        fun showList(items: List<QuizReviewItem>) {
            if (items.isEmpty()) {
                recycler.visibility = View.GONE
                noDataText.visibility = View.VISIBLE
            } else {
                noDataText.visibility = View.GONE
                recycler.visibility = View.VISIBLE
                val adapter = QuizReviewAdapter(items)
                recycler.adapter = adapter
            }
            errorContainer.visibility = View.GONE
        }

        if (quizId.isBlank()) {
            showError(getString(R.string.error_missing_quiz_id))
            return
        }

        // Load quiz from API by id - declared before role check so listeners can call it
        fun loadQuiz() {
            lifecycleScope.launch {
                try {
                    showLoading(true)
                    val response = ApiClient.quizApiService.getQuizById(quizId)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            // Check authorization: allow PROFESSOR or the quiz creator (student)
                            try {
                                val currentUser = sessionManager.currentUser.first()
                                if (currentUser == null) {
                                    showError(getString(R.string.error_unauthorized))
                                    retryButton.visibility = View.GONE
                                    return@launch
                                }
                                val isProfessor = currentUser.role == UserRole.PROFESSOR
                                val isCreator = currentUser.id == body.creatorId
                                if (!isProfessor && !isCreator) {
                                    showError(getString(R.string.error_unauthorized))
                                    retryButton.visibility = View.GONE
                                    return@launch
                                }
                            } catch (e: Exception) {
                                showError(getString(R.string.error_network_generic_short))
                                retryButton.visibility = View.GONE
                                return@launch
                            }

                            // Update header with quiz title (preferred) and description from API
                            if (body.title.isNotBlank()) {
                                quizIdText.text = body.title
                            } else {
                                quizIdText.text = getString(R.string.quiz_review_title_format, quizId)
                            }
                            if (body.description != null && body.description.isNotBlank()) {
                                quizDescriptionText.text = body.description
                                quizDescriptionText.visibility = View.VISIBLE
                            }

                            // Map API questions to QuizReviewItem (question + options + correctAnswer)
                            val items = body.questions.map { q ->
                                QuizReviewItem(
                                    questionText = q.questionText,
                                    options = q.options ?: emptyList(),
                                    correctAnswer = q.correctAnswer ?: ""
                                )
                            }
                            showList(items)
                        } else {
                            showError(getString(R.string.error_empty_response))
                        }
                    } else {
                        showError(getString(R.string.error_network_generic, response.code().toString()))
                    }
                } catch (e: Exception) {
                    showError(e.message ?: getString(R.string.error_network_generic_short))
                } finally {
                    showLoading(false)
                }
            }
        }

        // Allow professors or the quiz creator to review the quiz.
        // We'll attempt to load the quiz and then check authorization (so we can compare creator id).
        retryButton.setOnClickListener { loadQuiz() }
        // Initial load
        loadQuiz()
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
