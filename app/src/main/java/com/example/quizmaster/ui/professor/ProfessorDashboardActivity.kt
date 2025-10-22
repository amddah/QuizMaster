package com.example.quizmaster.ui.professor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.*
import android.widget.ImageButton
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
import com.example.quizmaster.ui.auth.LoginActivity
import com.example.quizmaster.ui.quiz.QuizCreationActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import com.example.quizmaster.data.model.UserRole

/**
 * Professor Dashboard Activity
 * Displays quiz management, pending approvals, and analytics
 */
class ProfessorDashboardActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var quizApiService: QuizApiService
    // Flag to indicate whether the current user is a professor.
    // Default false so UI defaults to hiding professor-only actions for safety.
    private var isProfessor: Boolean = false

    private lateinit var professorNameText: TextView
    private lateinit var pendingCountText: TextView
    private lateinit var quizzesCountText: TextView
    private lateinit var createQuizFab: FloatingActionButton
    private lateinit var pendingCard: View
    private lateinit var pendingCardInner: View
    private lateinit var myQuizzesRecycler: RecyclerView
    private lateinit var logoutButton: Button
    private lateinit var profileButton: Button

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
        createQuizFab = findViewById(R.id.createQuizFab)
        pendingCard = findViewById(R.id.pendingCard)
        pendingCardInner = findViewById(R.id.pendingCardInner)
        // Ensure card is clickable at runtime in case some parent view intercepts
        pendingCard.isClickable = true
        pendingCard.isFocusable = true
        myQuizzesRecycler = findViewById(R.id.myQuizzesRecycler)
        logoutButton = findViewById(R.id.logoutButton)
        profileButton = findViewById(R.id.profileButton)

        myQuizzesRecycler.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupClickListeners() {
        // FAB now used for creating quizzes
        createQuizFab.setOnClickListener {
            startActivity(Intent(this, QuizCreationActivity::class.java))
        }

        // Also allow clicking the Pending card itself to go to approvals
        pendingCard.setOnClickListener {
            // Debug: show a toast and log so we can confirm the click handler runs
            Toast.makeText(this@ProfessorDashboardActivity, "Opening approvals...", Toast.LENGTH_SHORT).show()
            Log.d("ProfessorDashboard", "pendingCard clicked - launching ApprovalActivity")
            startActivity(Intent(this, ApprovalActivity::class.java))
        }

        // Also allow clicking the pending count text to open approvals (helps if child view consumed clicks)
        pendingCountText.setOnClickListener {
            Toast.makeText(this@ProfessorDashboardActivity, "Opening approvals...", Toast.LENGTH_SHORT).show()
            Log.d("ProfessorDashboard", "pendingCountText clicked - launching ApprovalActivity")
            startActivity(Intent(this, ApprovalActivity::class.java))
        }

        // Fallback: inner container click as well
        pendingCardInner.setOnClickListener {
            Toast.makeText(this@ProfessorDashboardActivity, "Opening approvals...", Toast.LENGTH_SHORT).show()
            Log.d("ProfessorDashboard", "pendingCardInner clicked - launching ApprovalActivity")
            startActivity(Intent(this, ApprovalActivity::class.java))
        }

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.clearSession()
                startActivity(Intent(this@ProfessorDashboardActivity, LoginActivity::class.java))
                finish()
            }
        }
        
        profileButton.setOnClickListener {
            startActivity(Intent(this@ProfessorDashboardActivity, com.example.quizmaster.ui.profile.ProfileActivity::class.java))
        }
    }
    
    private fun loadDashboardData() {
        lifecycleScope.launch {
            try {
                sessionManager.currentUser.collect { user ->
                    user?.let {
                        // Determine the role and store it so the UI can adapt (hide professor-only actions for students)
                        isProfessor = (it.role == UserRole.PROFESSOR)
                        // Use a translatable string resource instead of a hardcoded string
                        professorNameText.text = getString(R.string.welcome_professor, it.firstName)
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
                response.body()?.let { apiQuizzes ->
                    myQuizzes = apiQuizzes.map { it.toQuizModel() }.toMutableList()
                } ?: run {
                    myQuizzes = mutableListOf()
                }
                quizzesCountText.text = myQuizzes.size.toString()
                // Pass the current isProfessor flag implicitly by allowing the adapter to read it from the outer class
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
        } catch (_: Exception) {
            // Silent fail for pending count
        }
    }
    
    private inner class QuizAdapter(private val quizzes: List<QuizModel>) :
        RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
        
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): QuizViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quiz_card, parent, false)
            return QuizViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
            val quiz = quizzes[position]
            holder.bind(quiz)
        }
        
        override fun getItemCount() = quizzes.size
        
        private inner class QuizViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            
            fun bind(quiz: QuizModel) {
                itemView.findViewById<TextView>(R.id.quizTitle).text = quiz.title
                itemView.findViewById<TextView>(R.id.quizCategory).text = quiz.category.toString()
                itemView.findViewById<TextView>(R.id.quizDifficulty).text = quiz.difficulty.toString()
                itemView.findViewById<TextView>(R.id.quizDescription).text = quiz.description ?: ""
                
                // Hide the start button since this is just for display
                val startBtn = itemView.findViewById<Button>(R.id.startButton)
                startBtn.visibility = View.GONE

                // Setup review button to open QuizReviewActivity with quiz id
                val reviewBtn = itemView.findViewById<ImageButton>(R.id.reviewButton)
                // Only show the review (eye) button to professors
                reviewBtn.visibility = if (isProfessor) View.VISIBLE else View.GONE
                reviewBtn.setOnClickListener {
                    val ctx = itemView.context
                    val intent = Intent(ctx, QuizReviewActivity::class.java)
                    intent.putExtra("quiz_id", quiz.id)
                    ctx.startActivity(intent)
                }
            }
        }
    }
}
