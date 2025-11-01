package com.example.quizmaster.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var badgesAdapter: SimpleBadgesAdapter
    
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userRoleText: TextView
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var badgesGrid: RecyclerView
    private lateinit var logoutButton: Button
    private var loadingProgress: ProgressBar? = null
    private var totalQuizzesText: TextView? = null
    private var streakText: TextView? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        sessionManager = UserSessionManager.getInstance(this)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        
        initViews()
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
        
        // Load profile data from backend
        viewModel.loadUserProfile()
    }
    
    private fun initViews() {
        userNameText = findViewById(R.id.userNameText)
        userEmailText = findViewById(R.id.userEmailText)
        userRoleText = findViewById(R.id.userRoleText)
        levelText = findViewById(R.id.levelText)
        xpText = findViewById(R.id.xpText)
        badgesGrid = findViewById(R.id.badgesGrid)
        logoutButton = findViewById(R.id.logoutButton)
        
        // Optional views
        loadingProgress = findViewById(R.id.loadingProgress)
        totalQuizzesText = findViewById(R.id.totalQuizzesText)
        streakText = findViewById(R.id.streakText)
    }
    
    private fun setupRecyclerView() {
        badgesAdapter = SimpleBadgesAdapter()
        badgesGrid.apply {
            layoutManager = GridLayoutManager(this@ProfileActivity, 3)
            adapter = badgesAdapter
        }
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
                
                // Display total quizzes
                totalQuizzesText?.text = "${it.totalQuizzes ?: 0}"
                
                // Display streak
                it.streak?.let { streak ->
                    streakText?.text = "${streak.currentStreak} days"
                }
            }
        }
        
        // Observe level from achievements
        viewModel.level.observe(this) { level ->
            levelText.text = "Level $level"
        }
        
        // Observe XP from achievements
        viewModel.xp.observe(this) { xp ->
            val xpToNextLevel = (viewModel.level.value ?: 1) * 100
            val currentXpInLevel = xp % xpToNextLevel
            xpText.text = "$currentXpInLevel / $xpToNextLevel XP"
        }
        
        // Observe badges
        viewModel.badges.observe(this) { badges ->
            badgesAdapter.submitList(badges)
        }
        
        // Observe total badges count  
        viewModel.totalBadges.observe(this) { count ->
            // Update title or other view if needed
            supportActionBar?.subtitle = "$count Badges Earned"
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
        // Quiz history button
        findViewById<View>(R.id.viewHistoryButton)?.setOnClickListener {
            startActivity(Intent(this, QuizHistoryActivity::class.java))
        }
        
        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.clearSession()
                // Remove token from ApiClient
                ApiClient.setAuthToken(null)
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
