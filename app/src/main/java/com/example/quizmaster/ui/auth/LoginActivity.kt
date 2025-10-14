package com.example.quizmaster.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.LoginRequest
import com.example.quizmaster.data.model.UserRole
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.AuthApiService
import com.example.quizmaster.ui.professor.ProfessorDashboardActivity
import com.example.quizmaster.ui.student.StudentDashboardActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    
    private lateinit var authApiService: AuthApiService
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Initialize services
        authApiService = ApiClient.authApiService
        sessionManager = UserSessionManager(this)
        
        // Check if already logged in
        checkExistingSession()
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.errorText)
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            
            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }
        
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        errorText.visibility = View.GONE
        
        when {
            email.isEmpty() -> {
                showError("Please enter your email")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Please enter a valid email")
                return false
            }
            password.isEmpty() -> {
                showError("Please enter your password")
                return false
            }
            password.length < 6 -> {
                showError("Password must be at least 6 characters")
                return false
            }
        }
        return true
    }
    
    private fun performLogin(email: String, password: String) {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = authApiService.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    if (authResponse.success && authResponse.user != null && authResponse.token != null) {
                        // Save session
                        sessionManager.saveUserSession(authResponse.token, authResponse.user)
                        ApiClient.setAuthToken(authResponse.token)
                        
                        // Navigate based on role
                        navigateToDashboard(authResponse.user.role)
                    } else {
                        showError(authResponse.message)
                    }
                } else {
                    showError("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.localizedMessage}")
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun checkExistingSession() {
        lifecycleScope.launch {
            sessionManager.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    sessionManager.currentUser.collect { user ->
                        user?.let {
                            navigateToDashboard(it.role)
                        }
                    }
                }
            }
        }
    }
    
    private fun navigateToDashboard(role: UserRole) {
        val intent = when (role) {
            UserRole.STUDENT -> Intent(this, StudentDashboardActivity::class.java)
            UserRole.PROFESSOR -> Intent(this, ProfessorDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
        emailInput.isEnabled = !show
        passwordInput.isEnabled = !show
    }
    
    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
    }
}
