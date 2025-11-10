package com.example.quizmaster.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.UserRole
import com.example.quizmaster.ui.auth.LoginActivity
import com.example.quizmaster.data.remote.ApiClient
import kotlinx.coroutines.launch

/**
 * User Profile Activity
 * Displays user information, statistics, badges, and achievements from backend
 */
class ProfileActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var viewModel: ProfileViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userRoleText: TextView
    
    // Student-specific views
    private var studentSection: LinearLayout? = null
    private var levelText: TextView? = null
    private var xpText: TextView? = null
    private var totalQuizzesText: TextView? = null
    private var streakText: TextView? = null
    private var badgesGrid: RecyclerView? = null
    private lateinit var badgesAdapter: SimpleBadgesAdapter
    
    // Professor-specific views
    private var professorSection: LinearLayout? = null
    private var departmentText: TextView? = null
    private var officeHoursText: TextView? = null
    private var bioText: TextView? = null
    private var contactEmailButton: ImageButton? = null
    private var contactMessageButton: ImageButton? = null
    
    // Common views
    private lateinit var myQuizzesRecycler: RecyclerView
    private lateinit var logoutBottomButton: Button
    private lateinit var quizStatusAdapter: QuizStatusAdapter
    private var myQuizzesEmptyText: TextView? = null
    private var loadingProgress: ProgressBar? = null
    private var viewHistoryButton: Button? = null
    private var viewStudentAttemptsButton: Button? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        sessionManager = UserSessionManager.getInstance(this)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        
        setupToolbar()
        initViews()
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
        
        // Load profile data from backend
        viewModel.loadUserProfile()
    }
    
    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        // Only set toolbar as support action bar if the window does not already provide one
        try {
            if (supportActionBar == null) {
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title
            }
        } catch (e: IllegalStateException) {
            // Skip if theme already has ActionBar
        }
    }
    
    private fun initViews() {
        userNameText = findViewById(R.id.userNameText)
        userEmailText = findViewById(R.id.userEmailText)
        userRoleText = findViewById(R.id.userRoleText)
        
        // Student section and views
        studentSection = findViewById(R.id.studentSection)
        levelText = findViewById(R.id.levelText)
        xpText = findViewById(R.id.xpText)
        totalQuizzesText = findViewById(R.id.totalQuizzesText)
        streakText = findViewById(R.id.streakText)
        badgesGrid = findViewById(R.id.badgesGrid)
        
        // Professor section and views
        professorSection = findViewById(R.id.professorSection)
        departmentText = findViewById(R.id.departmentText)
        officeHoursText = findViewById(R.id.officeHoursText)
        bioText = findViewById(R.id.bioText)
        contactEmailButton = findViewById(R.id.contactEmailButton)
        contactMessageButton = findViewById(R.id.contactMessageButton)
        
        // Bottom buttons
        logoutBottomButton = findViewById(R.id.logoutBottomButton)
        viewHistoryButton = findViewById(R.id.viewHistoryButton)
        viewStudentAttemptsButton = findViewById(R.id.viewStudentAttemptsButton)
        
        // Common views
        myQuizzesRecycler = findViewById(R.id.myQuizzesRecycler)
        myQuizzesEmptyText = findViewById(R.id.myQuizzesEmptyText)
        loadingProgress = findViewById(R.id.loadingProgress)
    }
    
    private fun setupRecyclerView() {
        // Student badges
        badgesAdapter = SimpleBadgesAdapter()
        badgesGrid?.apply {
            layoutManager = GridLayoutManager(this@ProfileActivity, 3)
            adapter = badgesAdapter
        }
        
        // Quizzes status list
        quizStatusAdapter = QuizStatusAdapter()
        myQuizzesRecycler.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = quizStatusAdapter
            isNestedScrollingEnabled = false
            visibility = View.VISIBLE
        }
        Log.d("ProfileActivity", "setupRecyclerView: adapter set, layoutManager set, visibility=VISIBLE")
    }
    
    private fun observeViewModel() {
        // Observe user data
        viewModel.user.observe(this) { user ->
            user?.let {
                // Display name with proper formatting
                val fullName = "${it.firstName} ${it.lastName}".trim()
                userNameText.text = if (fullName.isNotEmpty()) fullName else "User"
                
                // Display email
                userEmailText.text = it.email
                
                // Display role with emoji
                userRoleText.text = if (it.role == UserRole.PROFESSOR) "👨‍🏫 Professor" else "👨‍🎓 Student"
                
                // Display student-specific data
                totalQuizzesText?.text = "${it.totalQuizzes ?: 0}"
                it.streak?.let { streak ->
                    streakText?.text = "${streak.currentStreak} days"
                }
                
                // Show/hide sections based on role
                updateUIBasedOnRole(it.role)
            }
        }
        
        // Observe level from achievements (student only)
        viewModel.level.observe(this) { level ->
            levelText?.text = "$level"
        }
        
        // Observe XP from achievements (student only)
        viewModel.xp.observe(this) { xp ->
            val xpToNextLevel = (viewModel.level.value ?: 1) * 100
            val currentXpInLevel = xp % xpToNextLevel
            xpText?.text = "$currentXpInLevel / $xpToNextLevel"
        }
        
        // Observe badges (student only)
        viewModel.badges.observe(this) { badges ->
            badgesAdapter.submitList(badges)
        }

        // Observe created quizzes status
        viewModel.myQuizzesStatus.observe(this) { quizzes ->
            Log.d("ProfileActivity", "myQuizzesStatus observed: size=${quizzes?.size}")
            try {
                val titles = quizzes?.map { it.title } ?: listOf()
                Log.d("ProfileActivity", "myQuizzesStatus titles=${titles}")
            } catch (e: Exception) {
                Log.d("ProfileActivity", "Failed to log quiz titles: ${e.message}")
            }
            quizStatusAdapter.submitList(quizzes) {
                myQuizzesRecycler.post {
                    val count = quizStatusAdapter.itemCount
                    Log.d("ProfileActivity", "quizStatusAdapter.itemCount=$count")
                    val hasItems = count > 0
                    myQuizzesRecycler.visibility = if (hasItems) View.VISIBLE else View.GONE
                    myQuizzesEmptyText?.visibility = if (hasItems) View.GONE else View.VISIBLE
                    myQuizzesRecycler.requestLayout()
                }
            }
        }
        
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgress?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe errors
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        // Quiz history button (for students)
        viewHistoryButton?.setOnClickListener {
            startActivity(Intent(this, QuizHistoryActivity::class.java))
        }
        
        // View student attempts button (for professors)
        viewStudentAttemptsButton?.setOnClickListener {
            // TODO: Navigate to student attempts activity
            Toast.makeText(this, "View Student Attempts - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        // Contact buttons
        contactEmailButton?.setOnClickListener {
            // TODO: Launch email intent
            Toast.makeText(this, "Email contact - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        contactMessageButton?.setOnClickListener {
            // TODO: Launch messaging
            Toast.makeText(this, "Message contact - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        // Wire bottom logout if present
        logoutBottomButton.setOnClickListener {
            performLogout()
        }
    }
    
    private fun updateUIBasedOnRole(role: UserRole) {
        when (role) {
            UserRole.STUDENT -> {
                // Show student-specific UI
                studentSection?.visibility = View.VISIBLE
                professorSection?.visibility = View.GONE
                viewHistoryButton?.visibility = View.VISIBLE
                viewStudentAttemptsButton?.visibility = View.GONE
            }
            UserRole.PROFESSOR -> {
                // Show professor-specific UI
                studentSection?.visibility = View.GONE
                professorSection?.visibility = View.VISIBLE
                viewHistoryButton?.visibility = View.GONE
                viewStudentAttemptsButton?.visibility = View.VISIBLE
            }
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            sessionManager.clearSession()
            ApiClient.setAuthToken(null)
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            finish()
        }
    }
}
