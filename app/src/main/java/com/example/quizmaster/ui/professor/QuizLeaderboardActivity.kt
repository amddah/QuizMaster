package com.example.quizmaster.ui.professor

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.LeaderboardApiService
import com.example.quizmaster.data.model.LeaderboardEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizLeaderboardActivity : AppCompatActivity() {

    private lateinit var quizTitleText: TextView
    private lateinit var totalStudentsText: TextView
    private lateinit var leaderboardRecycler: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var adapter: LeaderboardAdapter
    
    private val leaderboardApiService: LeaderboardApiService by lazy { ApiClient.leaderboardApiService }
    
    private var quizId: String = ""
    private var quizTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_leaderboard)

        // Get data from intent
        quizId = intent.getStringExtra("QUIZ_ID") ?: ""
        quizTitle = intent.getStringExtra("QUIZ_TITLE") ?: "Quiz Leaderboard"

        if (quizId.isEmpty()) {
            Toast.makeText(this, "Invalid quiz ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        initializeViews()
        setupRecyclerView()
        loadLeaderboard()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Rankings"
        }
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initializeViews() {
        quizTitleText = findViewById(R.id.quizTitleText)
        totalStudentsText = findViewById(R.id.totalStudentsText)
        leaderboardRecycler = findViewById(R.id.leaderboardRecycler)
        loadingProgress = findViewById(R.id.loadingProgress)
        emptyStateLayout = findViewById(R.id.emptyState)
        
        quizTitleText.text = quizTitle
    }

    private fun setupRecyclerView() {
        adapter = LeaderboardAdapter()
        leaderboardRecycler.layoutManager = LinearLayoutManager(this)
        leaderboardRecycler.adapter = adapter
    }

    private fun loadLeaderboard() {
        showLoading(true)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = leaderboardApiService.getQuizLeaderboard(quizId)
                
                if (response.isSuccessful) {
                    val leaderboardResponse = response.body()
                    
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        
                        if (leaderboardResponse != null) {
                            val entries = leaderboardResponse.leaderboard
                            val totalCount = leaderboardResponse.totalCount
                            
                            totalStudentsText.text = "$totalCount students attempted"
                            
                            if (entries.isEmpty()) {
                                showEmptyState(true)
                            } else {
                                showEmptyState(false)
                                adapter.submitList(entries)
                            }
                        } else {
                            showEmptyState(true)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(
                            this@QuizLeaderboardActivity,
                            "Failed to load leaderboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@QuizLeaderboardActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
        leaderboardRecycler.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        emptyStateLayout.visibility = if (show) View.VISIBLE else View.GONE
        leaderboardRecycler.visibility = if (show) View.GONE else View.VISIBLE
    }
}
