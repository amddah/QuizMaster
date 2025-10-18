package com.example.quizmaster.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quizmaster.R
import com.example.quizmaster.data.Question
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.utils.ScoreCalculator
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        private const val TIMER_DURATION = 15000L // 15 seconds
        private const val FEEDBACK_DELAY = 2000L // 2 seconds
        private const val KEY_TOTAL_SCORE = "key_total_score"
    }
    
    private lateinit var viewModel: QuizViewModel

    private var countDownTimer: CountDownTimer? = null
    private var currentQuestion: Question? = null
    private var answerButtons: List<MaterialButton> = emptyList()
    private var isAnswerSubmitted = false
    private var questionStartTime = 0L
    private var totalScore = 0
    
    // UI Elements
    private lateinit var textViewQuestion: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewProgress: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var textViewScore: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonAnswer1: MaterialButton
    private lateinit var buttonAnswer2: MaterialButton
    private lateinit var buttonAnswer3: MaterialButton
    private lateinit var buttonAnswer4: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Restore totalScore if activity recreated
        if (savedInstanceState != null) {
            totalScore = savedInstanceState.getInt(KEY_TOTAL_SCORE, 0)
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            QuizViewModel.Factory(applicationContext)
        )[QuizViewModel::class.java]

        initializeViews()
        setupAnswerButtons()
        setupQuiz()
        observeViewModel()

        // update UI score after views have been initialized
        textViewScore.text = "Score: $totalScore"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TOTAL_SCORE, totalScore)
    }
    
    private fun initializeViews() {
        textViewQuestion = findViewById(R.id.textViewQuestion)
        textViewCategory = findViewById(R.id.textViewCategory)
        textViewProgress = findViewById(R.id.textViewProgress)
        textViewTimer = findViewById(R.id.textViewTimer)
        textViewScore = findViewById(R.id.textViewScore)
        progressBar = findViewById(R.id.progressBar)
        buttonAnswer1 = findViewById(R.id.buttonAnswer1)
        buttonAnswer2 = findViewById(R.id.buttonAnswer2)
        buttonAnswer3 = findViewById(R.id.buttonAnswer3)
        buttonAnswer4 = findViewById(R.id.buttonAnswer4)
    }

    private fun setupAnswerButtons() {
        answerButtons = listOf(
            buttonAnswer1,
            buttonAnswer2,
            buttonAnswer3,
            buttonAnswer4
        )
        
        answerButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (!isAnswerSubmitted) {
                    submitAnswer(button.text.toString(), index)
                }
            }
        }
    }
    
    private fun setupQuiz() {
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: QuizCategory.GENERAL.name
        val difficultyName = intent.getStringExtra(EXTRA_DIFFICULTY) ?: QuizDifficulty.EASY.name
        
        val category = QuizCategory.valueOf(categoryName)
        val difficulty = QuizDifficulty.valueOf(difficultyName)
        
        textViewCategory.text = "${category.displayName} - ${difficulty.displayName}"

        viewModel.startQuiz(category, difficulty)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is QuizUiState.Loading -> {
                        // Show loading state
                    }
                    is QuizUiState.QuestionReady -> {
                        displayQuestion(state.question, state.currentQuestionNumber, state.totalQuestions)
                    }
                    is QuizUiState.QuizCompleted -> {
                        // Use the local totalScore (points accumulated) to show in results
                        navigateToResults(totalScore, state.totalQuestions, state.category, state.difficulty)
                    }
                    is QuizUiState.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }
    
    private fun displayQuestion(question: Question, questionNumber: Int, totalQuestions: Int) {
        currentQuestion = question
        isAnswerSubmitted = false
        questionStartTime = System.currentTimeMillis()
        
        textViewQuestion.text = question.question
        textViewProgress.text = "$questionNumber / $totalQuestions"
        textViewScore.text = "Score: $totalScore"

        val progress = ((questionNumber - 1) * 100) / totalQuestions
        progressBar.progress = progress

        val answers = question.getAllAnswers()
        answerButtons.forEachIndexed { index, button ->
            if (index < answers.size) {
                button.text = answers[index]
                button.isEnabled = true
                resetButtonStyle(button)
            }
        }
        
        startTimer()
    }
    
    private fun submitAnswer(selectedAnswer: String, buttonIndex: Int) {
        if (isAnswerSubmitted) return
        
        isAnswerSubmitted = true
        countDownTimer?.cancel()
        
        val question = currentQuestion ?: return
        val isCorrect = selectedAnswer == question.correctAnswer
        val timeToAnswer = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()
        
        // Inform ViewModel about the submitted answer (to keep repository results consistent)
        viewModel.submitAnswer(selectedAnswer)

        // Calculate score based on time
        val pointsEarned = if (isCorrect) {
            val multiplier = ScoreCalculator.getScoreMultiplier(timeToAnswer)
            (100 * multiplier).toInt() // Assuming max score of 100 per question
        } else {
            0
        }
        
        totalScore += pointsEarned
        
        // Highlight correct/incorrect answer
        answerButtons.forEachIndexed { index, button ->
            button.isEnabled = false
            when {
                button.text == question.correctAnswer -> {
                    button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                }
                button.text == selectedAnswer && !isCorrect -> {
                    button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                }
            }
        }
        
        lifecycleScope.launch {
            delay(FEEDBACK_DELAY)
            viewModel.nextQuestion()
        }
    }
    
    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(TIMER_DURATION, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                textViewTimer.text = "$secondsRemaining s"
                
                // Change color based on time remaining
                when {
                    secondsRemaining > 10 -> textViewTimer.setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.holo_green_dark))
                    secondsRemaining > 5 -> textViewTimer.setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.holo_orange_dark))
                    else -> textViewTimer.setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.holo_red_dark))
                }
            }

            override fun onFinish() {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    textViewTimer.text = "0 s"
                    answerButtons.forEach { it.isEnabled = false }
                    
                    lifecycleScope.launch {
                        delay(FEEDBACK_DELAY)
                        viewModel.nextQuestion()
                    }
                }
            }
        }.start()
    }
    
    private fun resetButtonStyle(button: MaterialButton) {
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
    }
    
    private fun navigateToResults(score: Int, totalQuestions: Int, category: QuizCategory, difficulty: QuizDifficulty) {
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra(ResultsActivity.EXTRA_SCORE, score)
            putExtra(ResultsActivity.EXTRA_TOTAL_QUESTIONS, totalQuestions)
            putExtra(ResultsActivity.EXTRA_CATEGORY, category.name)
            putExtra(ResultsActivity.EXTRA_DIFFICULTY, difficulty.name)
        }
        startActivity(intent)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
