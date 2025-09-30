package com.example.quizmaster.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmaster.MainActivity
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.google.android.material.button.MaterialButton

class ResultsActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use layout ID directly by name lookup
        val layoutId = resources.getIdentifier("activity_results", "layout", packageName)
        setContentView(layoutId)

        initializeViews()
        displayResults()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        // Use findViewById with resource name lookup
        textViewScorePercentage = findViewById(resources.getIdentifier("textViewScorePercentage", "id", packageName))
        textViewScoreFraction = findViewById(resources.getIdentifier("textViewScoreFraction", "id", packageName))
        textViewMessage = findViewById(resources.getIdentifier("textViewMessage", "id", packageName))
        textViewCategory = findViewById(resources.getIdentifier("textViewCategory", "id", packageName))
        textViewTimeSpent = findViewById(resources.getIdentifier("textViewTimeSpent", "id", packageName))
        buttonPlayAgain = findViewById(resources.getIdentifier("buttonPlayAgain", "id", packageName))
        buttonViewHistory = findViewById(resources.getIdentifier("buttonViewHistory", "id", packageName))
        buttonHome = findViewById(resources.getIdentifier("buttonHome", "id", packageName))
    }

    private fun displayResults() {
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 10)
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: QuizCategory.GENERAL.name
        val difficultyName = intent.getStringExtra(EXTRA_DIFFICULTY) ?: QuizDifficulty.EASY.name
        
        val category = QuizCategory.valueOf(categoryName)
        val difficulty = QuizDifficulty.valueOf(difficultyName)
        
        val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        
        // Display score
        textViewScorePercentage.text = "$percentage%"
        textViewScoreFraction.text = "$score/$totalQuestions"

        // Display motivational message
        textViewMessage.text = getMotivationalMessage(percentage)

        // Display quiz details
        textViewCategory.text = "📚 ${category.displayName} - ${difficulty.displayName}"

        // Note: In a real implementation, you would get the actual time spent from the ViewModel
        textViewTimeSpent.text = "⏱️ Time: --"

        // Animate the score (optional enhancement)
        animateScore(percentage)
    }
    
    private fun getMotivationalMessage(percentage: Int): String {
        return when (percentage) {
            100 -> "Perfect! 🎉 You're a quiz master!"
            in 80..99 -> "Excellent! 🌟 Almost perfect!"
            in 60..79 -> "Well done! 👍 Good job!"
            in 40..59 -> "Not bad! 👌 Keep practicing!"
            in 20..39 -> "Keep trying! 💪 You'll get better!"
            else -> "Don't give up! 📚 Practice makes perfect!"
        }
    }
    
    private fun animateScore(targetPercentage: Int) {
        // Simple animation that counts up to the target percentage
        val duration = 2000L // 2 seconds
        val startTime = System.currentTimeMillis()
        
        val handler = android.os.Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / duration).coerceAtMost(1f)
                val currentPercentage = (targetPercentage * progress).toInt()
                
                textViewScorePercentage.text = "$currentPercentage%"

                if (progress < 1f) {
                    handler.postDelayed(this, 16) // ~60 FPS
                }
            }
        }
        handler.post(runnable)
    }
    
    private fun setupClickListeners() {
        buttonPlayAgain.setOnClickListener {
            // Restart the same quiz
            val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: QuizCategory.GENERAL.name
            val difficultyName = intent.getStringExtra(EXTRA_DIFFICULTY) ?: QuizDifficulty.EASY.name
            
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra(QuizActivity.EXTRA_CATEGORY, categoryName)
            intent.putExtra(QuizActivity.EXTRA_DIFFICULTY, difficultyName)
            startActivity(intent)
            finish()
        }
        
        buttonViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}