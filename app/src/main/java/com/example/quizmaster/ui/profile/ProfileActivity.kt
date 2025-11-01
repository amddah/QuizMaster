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
    private lateinit var userRoleText: TextView
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var badgesGrid: RecyclerView
    private lateinit var logoutButton: Button
    private var loadingProgress: ProgressBar? = null
    
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
        userRoleText = findViewById(R.id.userRoleText)
        levelText = findViewById(R.id.levelText)
        xpText = findViewById(R.id.xpText)
        badgesGrid = findViewById(R.id.badgesGrid)
        logoutButton = findViewById(R.id.logoutButton)
        
        // Optional views
        loadingProgress = findViewById(R.id.loadingProgress)
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
                userNameText.text = "${it.firstName} ${it.lastName}"
                userRoleText.text = if (it.role == UserRole.PROFESSOR) "Professor" else "Student"
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
