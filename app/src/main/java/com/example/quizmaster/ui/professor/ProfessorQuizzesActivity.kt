package com.example.quizmaster.ui.professor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.QuizApiService
import com.example.quizmaster.data.remote.LeaderboardApiService
import com.example.quizmaster.data.model.QuizApiModel
import com.example.quizmaster.data.model.QuizWithAttempts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfessorQuizzesActivity : AppCompatActivity() {

    private lateinit var quizzesRecycler: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var adapter: ProfessorQuizzesAdapter
    
    private val quizApiService: QuizApiService by lazy { ApiClient.quizApiService }
    private val leaderboardApiService: LeaderboardApiService by lazy { ApiClient.leaderboardApiService }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professor_quizzes)

        setupToolbar()
        initializeViews()
        setupRecyclerView()
        loadProfessorQuizzes()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Student Attempts"
        }
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initializeViews() {
        quizzesRecycler = findViewById(R.id.quizzesRecycler)
        loadingProgress = findViewById(R.id.loadingProgress)
        emptyStateLayout = findViewById(R.id.emptyState)
    }

    private fun setupRecyclerView() {
        adapter = ProfessorQuizzesAdapter { quiz ->
            openLeaderboard(quiz)
        }
        quizzesRecycler.layoutManager = LinearLayoutManager(this)
        quizzesRecycler.adapter = adapter
    }

    private fun loadProfessorQuizzes() {
        showLoading(true)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = quizApiService.getAllQuizzes()
                
                if (response.isSuccessful) {
                    val allQuizzes = response.body() ?: emptyList()
                    
                    // Get attempt counts for each quiz
                    val quizzesWithAttempts = allQuizzes.map { quiz ->
                        val attemptCount = getAttemptCount(quiz.id)
                        QuizWithAttempts(
                            quiz = quiz,
                            attemptCount = attemptCount
                        )
                    }
                    
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        if (quizzesWithAttempts.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)
                            adapter.submitList(quizzesWithAttempts)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(
                            this@ProfessorQuizzesActivity,
                            "Failed to load quizzes",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@ProfessorQuizzesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun getAttemptCount(quizId: String): Int {
        return try {
            val response = leaderboardApiService.getQuizLeaderboard(quizId)
            if (response.isSuccessful) {
                response.body()?.totalCount ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun openLeaderboard(quizWithAttempts: QuizWithAttempts) {
        if (quizWithAttempts.attemptCount > 0) {
            val intent = Intent(this, QuizLeaderboardActivity::class.java).apply {
                putExtra("QUIZ_ID", quizWithAttempts.quiz.id)
                putExtra("QUIZ_TITLE", quizWithAttempts.quiz.title)
            }
            startActivity(intent)
        }
    }

    private fun showLoading(show: Boolean) {
        loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
        quizzesRecycler.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        emptyStateLayout.visibility = if (show) View.VISIBLE else View.GONE
        quizzesRecycler.visibility = if (show) View.GONE else View.VISIBLE
    }
}
