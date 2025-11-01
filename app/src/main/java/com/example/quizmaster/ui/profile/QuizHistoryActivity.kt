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
 * Activity displaying user's quiz attempt history
 */
class QuizHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: QuizHistoryViewModel
    private lateinit var historyAdapter: QuizHistoryAdapter
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var statsCard: View
    private lateinit var totalQuizzesText: TextView
    private lateinit var averageScoreText: TextView
    private lateinit var totalXpText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_history)
        
        viewModel = ViewModelProvider(this)[QuizHistoryViewModel::class.java]
        
        initViews()
        setupRecyclerView()
        setupToolbar()
        observeViewModel()
        
        // Load quiz history
        viewModel.loadQuizHistory()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.historyRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        loadingProgress = findViewById(R.id.loadingProgress)
        statsCard = findViewById(R.id.statsCard)
        totalQuizzesText = findViewById(R.id.totalQuizzesText)
        averageScoreText = findViewById(R.id.averageScoreText)
        totalXpText = findViewById(R.id.totalXpText)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Quiz History"
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = QuizHistoryAdapter { attempt ->
            // Show attempt details
            Toast.makeText(this, "Score: ${attempt.totalScore}/${attempt.maxScore}", Toast.LENGTH_SHORT).show()
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@QuizHistoryActivity)
            adapter = historyAdapter
        }
    }

    private fun observeViewModel() {
        // Observe quiz history
        viewModel.quizHistory.observe(this) { history ->
            if (history.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                historyAdapter.submitList(history)
            } else {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
        
        // Observe statistics
        viewModel.statistics.observe(this) { stats ->
            totalQuizzesText.text = stats.totalQuizzes.toString()
            averageScoreText.text = "${stats.averageScore}%"
            totalXpText.text = stats.totalXp.toString()
        }
        
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe errors
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
