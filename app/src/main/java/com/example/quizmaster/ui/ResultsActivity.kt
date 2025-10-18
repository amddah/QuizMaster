package com.example.quizmaster.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quizmaster.R
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.QuizAttemptApiService
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

/**
 * Results Activity - Displays quiz results with leaderboard comparison
 */
class ResultsActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        const val EXTRA_QUIZ_ID = "extra_quiz_id"
        const val EXTRA_TIME_TAKEN = "extra_time_taken"
    }
    
    // UI Elements
    private lateinit var textViewScorePercentage: TextView
    private lateinit var textViewScoreFraction: TextView
    private lateinit var textViewMessage: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewTimeSpent: TextView
    private lateinit var buttonPlayAgain: MaterialButton
    private lateinit var buttonViewHistory: MaterialButton
    private lateinit var buttonHome: MaterialButton
    
    private lateinit var sessionManager: UserSessionManager
    private lateinit var quizAttemptApiService: QuizAttemptApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        
        sessionManager = UserSessionManager.getInstance(this)
        quizAttemptApiService = ApiClient.quizAttemptApiService

        initializeViews()
        displayResults()
        setupClickListeners()
        loadLeaderboardData()
    }
    
    private fun initializeViews() {
        textViewScorePercentage = findViewById(R.id.textViewScorePercentage)
        textViewScoreFraction = findViewById(R.id.textViewScoreFraction)
        textViewMessage = findViewById(R.id.textViewMessage)
        textViewCategory = findViewById(R.id.textViewCategory)
        textViewTimeSpent = findViewById(R.id.textViewTimeSpent)
        buttonPlayAgain = findViewById(R.id.buttonPlayAgain)
        buttonViewHistory = findViewById(R.id.buttonViewHistory)
        buttonHome = findViewById(R.id.buttonHome)
    }
    
    private fun displayResults() {
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 1)
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: QuizCategory.GENERAL.name
        val difficultyName = intent.getStringExtra(EXTRA_DIFFICULTY) ?: QuizDifficulty.EASY.name
        val timeTaken = intent.getIntExtra(EXTRA_TIME_TAKEN, 0)
        
        val category = QuizCategory.valueOf(categoryName)
        val difficulty = QuizDifficulty.valueOf(difficultyName)
        
        // Le score passé depuis QuizActivity est la somme des points gagnés par question
        // Chaque question a un score maximal de 100, donc le score maximal total = totalQuestions * 100
        val maxTotalScore = totalQuestions * 100
        val percentage = if (maxTotalScore > 0) {
            // calculer en flottant pour éviter les erreurs d'arrondi entiers
            ((score.toFloat() / maxTotalScore.toFloat()) * 100f).toInt()
        } else {
            0
        }

        textViewScorePercentage.text = "$percentage%"
        textViewScoreFraction.text = "$score / $maxTotalScore"
        textViewCategory.text = "${category.displayName} - ${difficulty.displayName}"
        textViewTimeSpent.text = formatTime(timeTaken)
        
        // Performance tier is displayed in the message
        
        // Display message based on score
        textViewMessage.text = getResultMessage(percentage)
    }
    
    private fun getPerformanceTier(percentage: Int): String {
        return when {
            percentage >= 90 -> "🏆 Excellent"
            percentage >= 75 -> "⭐ Great"
            percentage >= 60 -> "👍 Good"
            percentage >= 50 -> "😊 Average"
            else -> "💪 Needs Improvement"
        }
    }
    
    private fun getResultMessage(percentage: Int): String {
        return when {
            percentage >= 90 -> "Outstanding performance!"
            percentage >= 75 -> "Great job!"
            percentage >= 60 -> "Good effort!"
            percentage >= 50 -> "Keep practicing!"
            else -> "Try again to improve!"
        }
    }
    
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }
    
    private fun loadLeaderboardData() {
        // TODO: Implement leaderboard data loading
        // This would be called when the API is properly connected
    }
    
    private fun setupClickListeners() {
        buttonPlayAgain.setOnClickListener {
            finish()
        }
        
        buttonViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        
        buttonHome.setOnClickListener {
            // Navigate back to the student's dashboard (clearing the activity stack to avoid returning to old pages)
            val intent = Intent(this, com.example.quizmaster.ui.student.StudentDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
