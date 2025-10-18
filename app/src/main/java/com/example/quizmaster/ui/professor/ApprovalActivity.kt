package com.example.quizmaster.ui.professor

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
import com.example.quizmaster.data.model.toQuizModel
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.QuizApiService
import kotlinx.coroutines.launch

/**
 * Approval Activity - Professors review and approve/reject student quizzes
 */
class ApprovalActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var quizApiService: QuizApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: android.widget.LinearLayout

    private var pendingQuizzes = mutableListOf<QuizModel>()
    private var authToken: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval)
        
        sessionManager = UserSessionManager.getInstance(this)
        quizApiService = ApiClient.quizApiService
        
        initViews()
        loadPendingQuizzes()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.pendingQuizzesRecyclerView)
        progressBar = findViewById(R.id.loadingProgressBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun loadPendingQuizzes() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                authToken = sessionManager.getAuthToken()

                // Get all quizzes with status=pending
                val response = quizApiService.getAllQuizzes(status = "pending")

                if (response.isSuccessful) {
                    response.body()?.let { apiQuizzes ->
                        pendingQuizzes.clear()
                        val convertedQuizzes = apiQuizzes.map { it.toQuizModel() }
                        pendingQuizzes.addAll(convertedQuizzes)

                        if (pendingQuizzes.isEmpty()) {
                            emptyStateLayout.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyStateLayout.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            recyclerView.adapter = PendingQuizAdapter(
                                pendingQuizzes,
                                { quiz -> approveQuiz(quiz) },
                                { quiz -> rejectQuiz(quiz) }
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        this@ApprovalActivity,
                        "Failed to load pending quizzes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(
                    this@ApprovalActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun approveQuiz(quiz: QuizModel) {
        lifecycleScope.launch {
            try {
                val response = quizApiService.approveQuiz(quiz.id)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ApprovalActivity,
                        "Quiz approved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadPendingQuizzes()
                } else {
                    Toast.makeText(
                        this@ApprovalActivity,
                        "Failed to approve quiz",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ApprovalActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun rejectQuiz(quiz: QuizModel) {
        lifecycleScope.launch {
            try {
                // Use delete instead of reject since rejectQuiz doesn't exist in the API
                val response = quizApiService.deleteQuiz(quiz.id)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ApprovalActivity,
                        "Quiz rejected",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadPendingQuizzes()
                } else {
                    Toast.makeText(
                        this@ApprovalActivity,
                        "Failed to reject quiz",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ApprovalActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

/**
 * Adapter for pending quizzes
 */
class PendingQuizAdapter(
    private val quizzes: List<QuizModel>,
    private val onApprove: (QuizModel) -> Unit,
    private val onReject: (QuizModel) -> Unit
) : RecyclerView.Adapter<PendingQuizAdapter.ViewHolder>() {
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.quizTitle)
        private val creatorText: TextView = itemView.findViewById(R.id.creatorName)
        private val categoryText: TextView = itemView.findViewById(R.id.category)
        private val difficultyText: TextView = itemView.findViewById(R.id.difficulty)
        private val approveButton: Button = itemView.findViewById(R.id.approveButton)
        private val rejectButton: Button = itemView.findViewById(R.id.rejectButton)
        
        fun bind(quiz: QuizModel) {
            titleText.text = quiz.title
            creatorText.text = "By: ${quiz.creatorName}"
            categoryText.text = quiz.category.toString()
            difficultyText.text = quiz.difficulty.toString()
            
            approveButton.setOnClickListener { onApprove(quiz) }
            rejectButton.setOnClickListener { onReject(quiz) }
        }
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_quiz, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(quizzes[position])
    }
    
    override fun getItemCount() = quizzes.size
}
