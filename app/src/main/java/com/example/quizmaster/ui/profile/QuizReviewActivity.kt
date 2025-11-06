package com.example.quizmaster.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.google.android.material.appbar.MaterialToolbar

/**
 * Activity for reviewing a completed quiz attempt
 * Shows all questions with student's answers and correct answers
 */
class QuizReviewActivity : AppCompatActivity() {

    private lateinit var viewModel: QuizReviewViewModel
    private lateinit var reviewAdapter: QuizReviewAdapter
    
    private var toolbar: MaterialToolbar? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var scoreText: TextView
    private lateinit var accuracyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review)
        
        viewModel = ViewModelProvider(this)[QuizReviewViewModel::class.java]
        
        val attemptId = intent.getStringExtra("ATTEMPT_ID") ?: ""
        val quizId = intent.getStringExtra("QUIZ_ID") ?: ""
        val quizTitle = intent.getStringExtra("QUIZ_TITLE") ?: "Quiz Review"
        
        initViews()
        setupToolbar(quizTitle)
        setupRecyclerView()
        observeViewModel()
        
        // Load attempt details
        viewModel.loadAttemptReview(attemptId, quizId)
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.reviewRecycler)
        loadingProgress = findViewById(R.id.loadingProgressBar)
        scoreText = findViewById(R.id.quizIdText)
        accuracyText = findViewById(R.id.quizDescriptionText)
    }

    private fun setupToolbar(title: String) {
        toolbar?.let { tb ->
            try {
                setSupportActionBar(tb)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = title
                supportActionBar?.subtitle = "Review"

                tb.setNavigationOnClickListener {
                    finish()
                }
            } catch (e: IllegalStateException) {
                // Activity already has an ActionBar from theme; skip setting toolbar
            }
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = QuizReviewAdapter()
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@QuizReviewActivity)
            adapter = reviewAdapter
        }
    }

    private fun observeViewModel() {
        // Observe review data
        viewModel.reviewItems.observe(this) { items ->
            reviewAdapter.submitList(items)
        }
        
        // Observe score info
        viewModel.scoreInfo.observe(this) { info ->
            scoreText.text = "Score: ${info.score}/${info.maxScore} (${info.percentage}%)"
            accuracyText.text = "Correct: ${info.correctAnswers}/${info.totalQuestions}"
        }
        
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        // Observe errors
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
