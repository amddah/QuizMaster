package com.example.quizmaster.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.UserRole
import com.example.quizmaster.data.repository.AuthRepository
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.ui.professor.ProfessorDashboardActivity
import com.example.quizmaster.ui.student.StudentDashboardActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Initialize repository
        sessionManager = UserSessionManager.getInstance(this)
        authRepository = AuthRepository(ApiClient.authApiService, sessionManager)
        
        initViews()
        setupRoleSpinner()
        setupClickListeners()
    }
    
    private fun initViews() {
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        roleSpinner = findViewById(R.id.roleSpinner)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.errorText)
    }
    
    private fun setupRoleSpinner() {
        val roles = arrayOf("Student", "Professor")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter
    }
    
    private fun setupClickListeners() {
        registerButton.setOnClickListener {
            attemptRegistration()
        }
        
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun attemptRegistration() {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        val selectedRole = if (roleSpinner.selectedItemPosition == 0) UserRole.STUDENT else UserRole.PROFESSOR
        
        // Validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            showError("Please fill in all fields")
            return
        }
        
        if (password != confirmPassword) {
            showError("Passwords do not match")
            return
        }
        
        if (password.length < 6) {
            showError("Password must be at least 6 characters")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address")
            return
        }
        
        performRegistration(firstName, lastName, email, password, selectedRole)
    }
    
    private fun performRegistration(firstName: String, lastName: String, email: String, password: String, role: UserRole) {
        setLoading(true)
        
        lifecycleScope.launch {
            authRepository.register(email, firstName, lastName, password, role)
                .onSuccess { user ->
                    setLoading(false)
                    
                    // Navigate to appropriate dashboard
                    val intent = when (user.role) {
                        UserRole.STUDENT -> Intent(this@RegisterActivity, StudentDashboardActivity::class.java)
                        UserRole.PROFESSOR -> Intent(this@RegisterActivity, ProfessorDashboardActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }
                .onFailure { error ->
                    setLoading(false)
                    showError("Registration failed: ${error.message}")
                }
        }
    }
    
    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        registerButton.isEnabled = !isLoading
        
        // Disable all inputs during loading
        firstNameInput.isEnabled = !isLoading
        lastNameInput.isEnabled = !isLoading
        emailInput.isEnabled = !isLoading
        passwordInput.isEnabled = !isLoading
        confirmPasswordInput.isEnabled = !isLoading
        roleSpinner.isEnabled = !isLoading
    }
    
    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
    }
}
