package com.example.quizmaster.ui.professor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.QuizModel
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.QuizApiService
import com.example.quizmaster.ui.auth.LoginActivity
import com.example.quizmaster.ui.quiz.QuizCreationActivity
import kotlinx.coroutines.launch

/**
 * Professor Dashboard Activity
 * Displays quiz management, pending approvals, and analytics
 */
class ProfessorDashboardActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var quizApiService: QuizApiService
    
    private lateinit var professorNameText: TextView
    private lateinit var pendingCountText: TextView
    private lateinit var quizzesCountText: TextView
    private lateinit var createQuizButton: Button
    private lateinit var viewApprovalsButton: Button
    private lateinit var myQuizzesRecycler: RecyclerView

    private var myQuizzes = mutableListOf<QuizModel>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professor_dashboard)
        
        sessionManager = UserSessionManager.getInstance(this)
        quizApiService = ApiClient.quizApiService
        
        initViews()
        setupClickListeners()
        loadDashboardData()
    }
    
    private fun initViews() {
        professorNameText = findViewById(R.id.professorNameText)
        pendingCountText = findViewById(R.id.pendingCountText)
        quizzesCountText = findViewById(R.id.quizzesCountText)
        createQuizButton = findViewById(R.id.createQuizButton)
        viewApprovalsButton = findViewById(R.id.viewApprovalsButton)
        myQuizzesRecycler = findViewById(R.id.myQuizzesRecycler)

        myQuizzesRecycler.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupClickListeners() {
        createQuizButton.setOnClickListener {
            startActivity(Intent(this, QuizCreationActivity::class.java))
        }
        
        viewApprovalsButton.setOnClickListener {
            startActivity(Intent(this, ApprovalActivity::class.java))
        }
    }
    
    private fun loadDashboardData() {
        lifecycleScope.launch {
            try {
                sessionManager.currentUser.collect { user ->
                    user?.let {
                        professorNameText.text = "Welcome, Prof. ${it.firstName}!"
                        loadMyQuizzes()
                        loadPendingCount()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProfessorDashboardActivity,
                    "Error loading dashboard: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private suspend fun loadMyQuizzes() {
        try {
            // Get all quizzes - in a real app, filter by creator
            val response = quizApiService.getAllQuizzes()

            if (response.isSuccessful) {
                myQuizzes = response.body()?.toMutableList() ?: mutableListOf()
                quizzesCountText.text = myQuizzes.size.toString()
                myQuizzesRecycler.adapter = QuizAdapter(myQuizzes)
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@ProfessorDashboardActivity,
                "Error loading quizzes: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private suspend fun loadPendingCount() {
        try {
            // Get quizzes with pending status
            val response = quizApiService.getAllQuizzes(status = "pending")

            if (response.isSuccessful) {
                val pendingCount = response.body()?.size ?: 0
                pendingCountText.text = pendingCount.toString()
            }
        } catch (e: Exception) {
            // Silent fail for pending count
        }
    }
    
    private inner class QuizAdapter(private val quizzes: List<QuizModel>) :
        RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
        
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): QuizViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quiz_result, parent, false)
            return QuizViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
            val quiz = quizzes[position]
            holder.bind(quiz)
        }
        
        override fun getItemCount() = quizzes.size
        
        private inner class QuizViewHolder(itemView: android.view.View) :
            RecyclerView.ViewHolder(itemView) {
            
            fun bind(quiz: QuizModel) {
                itemView.findViewById<TextView>(R.id.quizTitle).text = quiz.title
                itemView.findViewById<TextView>(R.id.quizCategory).text = quiz.category.toString()
                itemView.findViewById<TextView>(R.id.quizDifficulty).text = quiz.difficulty.toString()
            }
        }
    }
}
