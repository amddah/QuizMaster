package com.example.quizmaster.ui.professor

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
 * Professor Dashboard Activity
 * 
 * TODO: Implement full dashboard with:
 * - Welcome section with professor info
 * - Statistics (quizzes created, approved, pending)
 * - Create Quiz button
 * - Pending approvals list with count badge
 * - My Created Quizzes section
 * - Quiz analytics
 * - Logout button
 */
class ProfessorDashboardActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Create layout/activity_professor_dashboard.xml
        setContentView(R.layout.activity_professor_dashboard)
        
        sessionManager = UserSessionManager(this)
        
        // Show professor info
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    Toast.makeText(
                        this@ProfessorDashboardActivity,
                        "Welcome, Professor ${it.username}!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun logout() {
        lifecycleScope.launch {
            sessionManager.clearSession()
            val intent = Intent(this@ProfessorDashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
