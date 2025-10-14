package com.example.quizmaster.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmaster.R

/**
 * TODO: Implement registration UI
 * Features to add:
 * - Email, username, password fields
 * - Role selection (Student/Professor)
 * - Department field for professors
 * - Input validation
 * - API integration
 */
class RegisterActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Create layout/activity_register.xml
        setContentView(R.layout.activity_register)
        
        Toast.makeText(this, "Register Activity - To be implemented", Toast.LENGTH_SHORT).show()
    }
}
