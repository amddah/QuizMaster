package com.example.quizmaster.ui.student

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmaster.R
import com.example.quizmaster.ui.auth.LoginActivity
import com.example.quizmaster.data.local.UserSessionManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Student Dashboard Activity
 * 
 * TODO: Implement full dashboard with:
 * - User info section (name, level, XP, badges)
 * - Progress bar to next level
 * - Available quizzes list (filtered by course completion)
 * - Category/difficulty filters
 * - Create quiz button
 * - Leaderboard access
 * - Profile button
 * - Statistics cards
 */
class StudentDashboardActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Create layout/activity_student_dashboard.xml
        setContentView(R.layout.activity_student_dashboard)
        
        sessionManager = UserSessionManager(this)
        
        // Show user info
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    Toast.makeText(
                        this@StudentDashboardActivity,
                        "Welcome, ${it.username}! Level ${it.level}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun logout() {
        lifecycleScope.launch {
            sessionManager.clearSession()
            val intent = Intent(this@StudentDashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
