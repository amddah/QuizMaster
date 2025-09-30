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
import com.example.quizmaster.data.Question
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        private const val TIMER_DURATION = 15000L // 15 seconds
        private const val FEEDBACK_DELAY = 2000L // 2 seconds
    }
    
    private lateinit var viewModel: QuizViewModel

    private var countDownTimer: CountDownTimer? = null
    private var currentQuestion: Question? = null
    private var answerButtons: List<MaterialButton> = emptyList()
    private var isAnswerSubmitted = false
    
    // UI Elements
    private lateinit var textViewQuestion: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewProgress: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonAnswer1: MaterialButton
    private lateinit var buttonAnswer2: MaterialButton
    private lateinit var buttonAnswer3: MaterialButton
    private lateinit var buttonAnswer4: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use layout ID directly by name lookup
        val layoutId = resources.getIdentifier("activity_quiz", "layout", packageName)
        setContentView(layoutId)

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            QuizViewModel.Factory(applicationContext)
        )[QuizViewModel::class.java]

        initializeViews()
        setupAnswerButtons()
        setupQuiz()
        observeViewModel()
    }
    
    private fun initializeViews() {
        textViewQuestion = findViewById(resources.getIdentifier("textViewQuestion", "id", packageName))
        textViewCategory = findViewById(resources.getIdentifier("textViewCategory", "id", packageName))
        textViewProgress = findViewById(resources.getIdentifier("textViewProgress", "id", packageName))
        textViewTimer = findViewById(resources.getIdentifier("textViewTimer", "id", packageName))
        progressBar = findViewById(resources.getIdentifier("progressBar", "id", packageName))
        buttonAnswer1 = findViewById(resources.getIdentifier("buttonAnswer1", "id", packageName))
        buttonAnswer2 = findViewById(resources.getIdentifier("buttonAnswer2", "id", packageName))
        buttonAnswer3 = findViewById(resources.getIdentifier("buttonAnswer3", "id", packageName))
        buttonAnswer4 = findViewById(resources.getIdentifier("buttonAnswer4", "id", packageName))
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
                        navigateToResults(state.score, state.totalQuestions, state.category, state.difficulty)
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
        
        textViewQuestion.text = question.question
        textViewProgress.text = "$questionNumber / $totalQuestions"

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
        val isCorrect = question.isCorrectAnswer(selectedAnswer)
        
        // Show visual feedback
        answerButtons.forEachIndexed { index, button ->
            button.isEnabled = false
            when {
                index == buttonIndex && isCorrect -> {
                    // Selected answer is correct - use color lookup
                    val colorId = resources.getIdentifier("correct_answer", "color", packageName)
                    button.backgroundTintList = ContextCompat.getColorStateList(this, colorId)
                }
                index == buttonIndex && !isCorrect -> {
                    // Selected answer is incorrect - use color lookup
                    val colorId = resources.getIdentifier("incorrect_answer", "color", packageName)
                    button.backgroundTintList = ContextCompat.getColorStateList(this, colorId)
                }
                button.text == question.correctAnswer -> {
                    // Highlight correct answer - use color lookup
                    val colorId = resources.getIdentifier("correct_answer", "color", packageName)
                    button.backgroundTintList = ContextCompat.getColorStateList(this, colorId)
                }
            }
        }
        
        viewModel.submitAnswer(selectedAnswer)
        
        // Move to next question after delay
        lifecycleScope.launch {
            delay(FEEDBACK_DELAY)
            viewModel.nextQuestion()
        }
    }
    
    private fun startTimer() {
        textViewTimer.text = "⏱️ 15"

        countDownTimer = object : CountDownTimer(TIMER_DURATION, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                textViewTimer.text = "⏱️ $secondsRemaining"
            }
            
            override fun onFinish() {
                if (!isAnswerSubmitted) {
                    // Time's up, submit empty answer
                    submitAnswer("", -1)
                }
            }
        }
        countDownTimer?.start()
    }
    
    private fun resetButtonStyle(button: MaterialButton) {
        val colorId = resources.getIdentifier("surface_color", "color", packageName)
        button.backgroundTintList = ContextCompat.getColorStateList(this, colorId)
    }
    
    private fun navigateToResults(score: Int, totalQuestions: Int, category: QuizCategory, difficulty: QuizDifficulty) {
        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra(ResultsActivity.EXTRA_SCORE, score)
        intent.putExtra(ResultsActivity.EXTRA_TOTAL_QUESTIONS, totalQuestions)
        intent.putExtra(ResultsActivity.EXTRA_CATEGORY, category.name)
        intent.putExtra(ResultsActivity.EXTRA_DIFFICULTY, difficulty.name)
        startActivity(intent)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}