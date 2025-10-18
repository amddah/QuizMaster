package com.example.quizmaster.ui.student

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
import com.example.quizmaster.ui.auth.LoginActivity
import com.example.quizmaster.ui.leaderboard.LeaderboardActivity
import com.example.quizmaster.ui.profile.ProfileActivity
import com.example.quizmaster.ui.quiz.QuizCreationActivity
import kotlinx.coroutines.launch

/**
 * Student Dashboard Activity
 * Displays available quizzes, user stats, and navigation options
 */
class StudentDashboardActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var quizzesRecyclerView: RecyclerView
    private lateinit var userNameText: TextView
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var xpProgressBar: ProgressBar
    private lateinit var categorySpinner: Spinner
    private lateinit var createQuizFab: com.google.android.material.floatingactionbutton.FloatingActionButton

    private var quizzes = mutableListOf<QuizModel>()
    private var quizAdapter: QuizAdapter? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)
        
        sessionManager = UserSessionManager.getInstance(this)
        initViews()
        setupClickListeners()
        loadUserData()
        loadQuizzes()
    }
    
    private fun initViews() {
        userNameText = findViewById(R.id.userNameText)
        levelText = findViewById(R.id.levelText)
        xpText = findViewById(R.id.xpText)
        xpProgressBar = findViewById(R.id.xpProgressBar)
        categorySpinner = findViewById(R.id.categorySpinner)
        quizzesRecyclerView = findViewById(R.id.quizzesRecyclerView)
        createQuizFab = findViewById(R.id.createQuizFab)

        quizzesRecyclerView.layoutManager = LinearLayoutManager(this)
        quizAdapter = QuizAdapter(quizzes) { quiz ->
            startQuiz(quiz)
        }
        quizzesRecyclerView.adapter = quizAdapter
    }
    
    private fun setupClickListeners() {
        createQuizFab.setOnClickListener {
            startActivity(Intent(this, QuizCreationActivity::class.java))
        }
        
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadQuizzes()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun loadUserData() {
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    userNameText.text = "${it.firstName} ${it.lastName}"
                    levelText.text = "Level ${it.level}"
                    xpText.text = "${it.xp} XP"

                    val xpForNextLevel = 100
                    val currentLevelXp = it.xp % xpForNextLevel
                    xpProgressBar.progress = (currentLevelXp * 100) / xpForNextLevel
                }
            }
        }
    }
    
    private fun loadQuizzes() {
        lifecycleScope.launch {
            try {
                val category = categorySpinner.selectedItem?.toString()

                val response = ApiClient.quizApiService.getAllQuizzes(
                    category = category
                )
                
                if (response.isSuccessful) {
                    quizzes.clear()
                    response.body()?.let { quizzes.addAll(it) }
                    quizAdapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@StudentDashboardActivity, "Failed to load quizzes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudentDashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startQuiz(quiz: QuizModel) {
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("quiz_id", quiz.id)
        startActivity(intent)
    }
}

/**
 * Adapter for quiz list
 */
class QuizAdapter(
    private val quizzes: List<QuizModel>,
    private val onQuizClick: (QuizModel) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): QuizViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_card, parent, false)
        return QuizViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizzes[position], onQuizClick)
    }
    
    override fun getItemCount() = quizzes.size
    
    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.quizTitle)
        private val categoryText: TextView = itemView.findViewById(R.id.quizCategory)
        private val difficultyText: TextView = itemView.findViewById(R.id.quizDifficulty)
        private val descriptionText: TextView = itemView.findViewById(R.id.quizDescription)
        private val startButton: Button = itemView.findViewById(R.id.startButton)
        
        fun bind(quiz: QuizModel, onQuizClick: (QuizModel) -> Unit) {
            titleText.text = quiz.title
            categoryText.text = quiz.category.toString()
            difficultyText.text = quiz.difficulty.toString()
            descriptionText.text = quiz.description ?: "No description"
            
            startButton.setOnClickListener {
                onQuizClick(quiz)
            }
        }
    }
}

// Placeholder QuizActivity reference
class QuizActivity : AppCompatActivity()
