package com.example.quizmaster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.MainActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: QuizHistoryAdapter
    
    // UI Elements
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var buttonBackHome: MaterialButton
    private lateinit var textViewBestScore: TextView
    private lateinit var textViewTotalQuizzes: TextView
    private lateinit var textViewAverageScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use layout ID directly by name lookup
        val layoutId = resources.getIdentifier("activity_history", "layout", packageName)
        setContentView(layoutId)

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            HistoryViewModel.Factory(applicationContext)
        )[HistoryViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initializeViews() {
        recyclerViewHistory = findViewById(resources.getIdentifier("recyclerViewHistory", "id", packageName))
        layoutEmptyState = findViewById(resources.getIdentifier("layoutEmptyState", "id", packageName))
        buttonBackHome = findViewById(resources.getIdentifier("buttonBackHome", "id", packageName))
        textViewBestScore = findViewById(resources.getIdentifier("textViewBestScore", "id", packageName))
        textViewTotalQuizzes = findViewById(resources.getIdentifier("textViewTotalQuizzes", "id", packageName))
        textViewAverageScore = findViewById(resources.getIdentifier("textViewAverageScore", "id", packageName))
    }

    private fun setupRecyclerView() {
        adapter = QuizHistoryAdapter()
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = adapter
    }
    
    private fun setupClickListeners() {
        buttonBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.quizResults.collect { results ->
                if (results.isEmpty()) {
                    layoutEmptyState.visibility = View.VISIBLE
                    recyclerViewHistory.visibility = View.GONE
                } else {
                    layoutEmptyState.visibility = View.GONE
                    recyclerViewHistory.visibility = View.VISIBLE
                    adapter.submitList(results.reversed()) // Show newest first
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.statistics.collect { stats ->
                textViewBestScore.text = "${stats.bestScore}%"
                textViewTotalQuizzes.text = stats.totalQuizzesPlayed.toString()
                textViewAverageScore.text = "${stats.averageScore}%"
            }
        }
    }
}