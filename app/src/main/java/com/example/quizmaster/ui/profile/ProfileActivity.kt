package com.example.quizmaster.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmaster.R

/**
 * Profile Activity
 * 
 * TODO: Implement profile UI with:
 * - User information display/edit
 * - Statistics dashboard
 * - Badges collection grid
 * - Achievement progress
 * - Quiz history
 * - Settings
 * - Logout button
 */
class ProfileActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        Toast.makeText(this, "Profile - To be implemented", Toast.LENGTH_SHORT).show()
    }
}
