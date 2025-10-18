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
import com.example.quizmaster.data.model.toQuizModel
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.ui.auth.LoginActivity
import com.example.quizmaster.ui.profile.ProfileActivity
import com.example.quizmaster.ui.quiz.QuizCreationActivity
import com.example.quizmaster.ui.QuizActivity
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
    private lateinit var logoutButton: Button
    private lateinit var profileButton: Button

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
        logoutButton = findViewById(R.id.logoutButton)
        profileButton = findViewById(R.id.profileButton)

        // Setup category spinner with proper text colors
        val categories = listOf("All", "Programming", "Math", "Science", "History", "Literature")
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(getColor(R.color.text_primary))
                view.textSize = 14f
                return view
            }
            
            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(getColor(R.color.text_primary))
                view.setPadding(16, 16, 16, 16)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

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
        
        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.clearSession()
                startActivity(Intent(this@StudentDashboardActivity, LoginActivity::class.java))
                finish()
            }
        }
        
        profileButton.setOnClickListener {
            startActivity(Intent(this@StudentDashboardActivity, ProfileActivity::class.java))
        }
    }
    
    private fun loadUserData() {
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    userNameText.text = getString(R.string.full_name, it.firstName, it.lastName)
                    levelText.text = getString(R.string.level_text, it.level)
                    xpText.text = getString(R.string.xp_text, it.xp)

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
                    response.body()?.let { apiQuizzes ->
                        val convertedQuizzes = apiQuizzes.map { it.toQuizModel() }
                        quizzes.addAll(convertedQuizzes)
                    }
                    quizAdapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@StudentDashboardActivity, getString(R.string.failed_load_quizzes), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudentDashboardActivity, getString(R.string.error_with_message, e.message ?: getString(R.string.error)), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startQuiz(quiz: QuizModel) {
        // Start online quiz with the quiz data
        // For now, we'll show a dialog or navigate to a quiz detail page
        // You can implement an OnlineQuizActivity later
        
        Toast.makeText(
            this,
            "Starting online quiz: ${quiz.title}\nQuiz ID: ${quiz.id}",
            Toast.LENGTH_SHORT
        ).show()
        
        // TODO: Implement OnlineQuizActivity for API-based quizzes
        // val intent = Intent(this, OnlineQuizActivity::class.java)
        // intent.putExtra("quiz_id", quiz.id)
        // intent.putExtra("quiz_title", quiz.title)
        // startActivity(intent)
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
            descriptionText.text = quiz.description ?: itemView.context.getString(R.string.no_description)

            startButton.setOnClickListener {
                onQuizClick(quiz)
            }
        }
    }
}
