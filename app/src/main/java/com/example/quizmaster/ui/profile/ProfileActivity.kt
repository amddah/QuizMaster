package com.example.quizmaster.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.UserRole
import com.example.quizmaster.ui.auth.LoginActivity
import kotlinx.coroutines.launch

/**
 * User Profile Activity
 * Displays user information, statistics, badges, and achievements
 */
class ProfileActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var userNameText: TextView
    private lateinit var userRoleText: TextView
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var badgesGrid: RecyclerView
    private lateinit var logoutButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        sessionManager = UserSessionManager.getInstance(this)
        initViews()
        loadUserProfile()
        setupClickListeners()
    }
    
    private fun initViews() {
        userNameText = findViewById(R.id.userNameText)
        userRoleText = findViewById(R.id.userRoleText)
        levelText = findViewById(R.id.levelText)
        xpText = findViewById(R.id.xpText)
        badgesGrid = findViewById(R.id.badgesGrid)
        logoutButton = findViewById(R.id.logoutButton)
        
        badgesGrid.layoutManager = GridLayoutManager(this, 3)
    }
    
    private fun loadUserProfile() {
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    userNameText.text = "${it.firstName} ${it.lastName}"
                    userRoleText.text = if (it.role == UserRole.PROFESSOR) "Professor" else "Student"
                    levelText.text = "Level ${it.level}"
                    
                    val xpToNextLevel = 100
                    val currentXpInLevel = it.xp % xpToNextLevel
                    xpText.text = "$currentXpInLevel / $xpToNextLevel XP"
                }
            }
        }
    }

    private fun setupClickListeners() {
        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.clearSession()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
