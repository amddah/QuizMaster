package com.example.quizmaster.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quizmaster.R
import com.example.quizmaster.data.Question
import com.example.quizmaster.data.Quiz
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.data.repository.QuizAttemptRepository
import com.example.quizmaster.utils.ScoreCalculator
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.example.quizmaster.data.model.QuizModel
import com.example.quizmaster.data.model.QuestionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        const val EXTRA_QUIZ_JSON = "extra_quiz_json"
        const val EXTRA_QUIZ_ID = "extra_quiz_id" // For backend quiz ID
        private const val TIMER_DURATION = 15000L // 15 seconds
        private const val FEEDBACK_DELAY = 2000L // 2 seconds
        private const val KEY_TOTAL_SCORE = "key_total_score"
        private const val KEY_ATTEMPT_ID = "key_attempt_id"
        private const val KEY_QUIZ_START_TIME = "key_quiz_start_time"
        private const val TAG = "QuizActivity"
    }
    
    private lateinit var viewModel: QuizViewModel
    private val attemptRepository = QuizAttemptRepository()

    private var countDownTimer: CountDownTimer? = null
    private var currentQuestion: Question? = null
    private var answerButtons: List<MaterialButton> = emptyList()
    private var isAnswerSubmitted = false
    private var questionStartTime = 0L
    private var totalScore = 0
    
    // Backend integration variables
    private var currentAttemptId: String? = null
    private var currentQuizId: String? = null
    private var quizStartTime = 0L
    private val questionAnswers = mutableListOf<Pair<String, Int>>() // questionId to timeToAnswer
    
    // UI Elements
    private lateinit var textViewQuestion: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewProgress: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var textViewScore: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var cardViewQuestion: View
    private lateinit var linearLayoutAnswers: View
    private lateinit var buttonAnswer1: MaterialButton
    private lateinit var buttonAnswer2: MaterialButton
    private lateinit var buttonAnswer3: MaterialButton
    private lateinit var buttonAnswer4: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Restore state if activity recreated
        if (savedInstanceState != null) {
            totalScore = savedInstanceState.getInt(KEY_TOTAL_SCORE, 0)
            currentAttemptId = savedInstanceState.getString(KEY_ATTEMPT_ID)
            quizStartTime = savedInstanceState.getLong(KEY_QUIZ_START_TIME, 0L)
        }

        // Get quiz ID from intent if provided
        currentQuizId = intent.getStringExtra(EXTRA_QUIZ_ID)

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
        textViewScore.text = getString(R.string.quiz_score, totalScore)
        
        // Start backend quiz attempt if we have a quiz ID and no attempt yet
        if (currentQuizId != null && currentAttemptId == null) {
            startBackendAttempt(currentQuizId!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TOTAL_SCORE, totalScore)
        outState.putString(KEY_ATTEMPT_ID, currentAttemptId)
        outState.putLong(KEY_QUIZ_START_TIME, quizStartTime)
    }
    
    private fun initializeViews() {
        textViewQuestion = findViewById(R.id.textViewQuestion)
        textViewCategory = findViewById(R.id.textViewCategory)
        textViewProgress = findViewById(R.id.textViewProgress)
        textViewTimer = findViewById(R.id.textViewTimer)
        textViewScore = findViewById(R.id.textViewScore)
        progressBar = findViewById(R.id.progressBar)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        cardViewQuestion = findViewById(R.id.cardViewQuestion)
        linearLayoutAnswers = findViewById(R.id.linearLayoutAnswers)
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
                if (!isAnswerSubmitted && button.visibility == View.VISIBLE) {
                    submitAnswer(button.text.toString())
                }
            }
        }
    }
    
    private fun setupQuiz() {
        // If a full quiz JSON was passed, deserialize and start with it (Search-by-ID flow)
        val quizJson = intent.getStringExtra(EXTRA_QUIZ_JSON)
        if (!quizJson.isNullOrBlank()) {
            try {
                val gson = Gson()
                val quizModel = gson.fromJson(quizJson, QuizModel::class.java)
                // Convert QuizModel -> local Quiz
                val localQuestions = quizModel.questions.map { qm ->
                    // Ensure options are distinct and the correct answer is present
                    if (qm.type == QuestionType.TRUE_FALSE) {
                        // Normalize true/false answers to canonical "True" / "False"
                        val correctNormalized = if (qm.correctAnswer.equals("true", ignoreCase = true) || qm.correctAnswer.equals("True", ignoreCase = true)) "True" else "False"
                        val incorrectNormalized = if (correctNormalized == "True") "False" else "True"

                        Question(
                            question = qm.questionText,
                            correctAnswer = correctNormalized,
                            incorrectAnswers = listOf(incorrectNormalized),
                            category = quizModel.category.name,
                            difficulty = quizModel.difficulty.name,
                            type = "boolean"
                        )
                    } else {
                        val opts = qm.options.filterNotNull().map { it.trim() }.distinct().toMutableList()
                        if (!opts.contains(qm.correctAnswer)) {
                            opts.add(qm.correctAnswer)
                        }

                        // Build incorrectAnswers by removing correctAnswer from options
                        val incorrect = opts.filter { it != qm.correctAnswer }

                        Question(
                            question = qm.questionText,
                            correctAnswer = qm.correctAnswer,
                            incorrectAnswers = incorrect,
                            category = quizModel.category.name,
                            difficulty = quizModel.difficulty.name,
                            type = "multiple"
                        )
                    }
                 }

                val localQuiz = Quiz(
                    questions = localQuestions,
                    category = quizModel.category,
                    difficulty = quizModel.difficulty
                )

                textViewCategory.text = getString(R.string.category_difficulty_format, localQuiz.category.displayName, localQuiz.difficulty.displayName)
                viewModel.startQuizWithQuiz(localQuiz)
                return
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.error_with_message, e.message ?: getString(R.string.error)), Toast.LENGTH_SHORT).show()
                // fall back to default behavior below
            }
        }

        val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: QuizCategory.GENERAL.name
        val difficultyName = intent.getStringExtra(EXTRA_DIFFICULTY) ?: QuizDifficulty.EASY.name
        
        val category = QuizCategory.valueOf(categoryName)
        val difficulty = QuizDifficulty.valueOf(difficultyName)
        
        textViewCategory.text = getString(R.string.category_difficulty_format, category.displayName, difficulty.displayName)

        viewModel.startQuiz(category, difficulty)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is QuizUiState.Loading -> {
                        // Show loading state
                        loadingProgressBar.visibility = View.VISIBLE
                        cardViewQuestion.visibility = View.GONE
                        linearLayoutAnswers.visibility = View.GONE
                        textViewProgress.visibility = View.GONE
                        textViewTimer.visibility = View.GONE
                        progressBar.visibility = View.GONE
                    }
                    is QuizUiState.QuestionReady -> {
                        // Hide loading, show question
                        loadingProgressBar.visibility = View.GONE
                        cardViewQuestion.visibility = View.VISIBLE
                        linearLayoutAnswers.visibility = View.VISIBLE
                        textViewProgress.visibility = View.VISIBLE
                        textViewTimer.visibility = View.VISIBLE
                        progressBar.visibility = View.VISIBLE
                        displayQuestion(state.question, state.currentQuestionNumber, state.totalQuestions)
                    }
                    is QuizUiState.QuizCompleted -> {
                        // Use the local totalScore (points accumulated) to show in results
                        navigateToResults(totalScore, state.totalQuestions, state.category, state.difficulty)
                    }
                    is QuizUiState.Error -> {
                        // Handle error
                        loadingProgressBar.visibility = View.GONE
                        Toast.makeText(this@QuizActivity, "Error loading quiz: ${state.message}", Toast.LENGTH_SHORT).show()
                        finish() // or handle differently
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
        textViewProgress.text = getString(R.string.quiz_question_counter, questionNumber, totalQuestions)
        textViewScore.text = getString(R.string.quiz_score, totalScore)

        val progress = ((questionNumber - 1) * 100) / totalQuestions
        progressBar.progress = progress

        // Build answer list defensively: for boolean type ensure exactly True/False
        val answers = if (question.type.equals("boolean", ignoreCase = true) || question.type.equals("bool", ignoreCase = true)) {
            listOf("True", "False").shuffled()
        } else {
            (question.incorrectAnswers + question.correctAnswer).filterNotNull().map { it.trim() }.distinct().shuffled()
        }

        // Reset all buttons first and show/hide based on available answers
        answerButtons.forEach { btn ->
            btn.visibility = View.GONE
            btn.isEnabled = false
            btn.text = ""
            resetButtonStyle(btn)
            btn.contentDescription = null
        }

        answers.forEachIndexed { index, ans ->
            if (index < answerButtons.size) {
                val btn = answerButtons[index]
                btn.visibility = View.VISIBLE
                btn.isEnabled = true
                btn.text = ans
                btn.contentDescription = ans
                resetButtonStyle(btn)
            }
        }
        
        startTimer()
    }

    private fun submitAnswer(selectedAnswer: String) {
        if (isAnswerSubmitted) return
        
        isAnswerSubmitted = true
        countDownTimer?.cancel()
        
        val question = currentQuestion ?: return
        val isCorrect = question.isCorrectAnswer(selectedAnswer)
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
        
        // Submit answer to backend if we have a quiz model with question IDs
        // For now, we'll use a placeholder - ideally you'd get the question ID from the question object
        val questionId = "q_${question.question.hashCode()}" // Temporary ID generation
        submitAnswerToBackend(questionId, selectedAnswer, timeToAnswer)
        
        // Highlight correct/incorrect answer and disable visible buttons
        answerButtons.forEach { button ->
            if (button.visibility == View.VISIBLE) {
                button.isEnabled = false
                val text = button.text.toString()
                when {
                    // Use robust comparison for correctness (trim + ignore case)
                    question.isCorrectAnswer(text) -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                    }
                    // If this button matches the user's selection (case-insensitive) and it's wrong
                    text.trim().equals(selectedAnswer.trim(), ignoreCase = true) && !isCorrect -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                    }
                    else -> {
                        // dim other options
                        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                        button.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    }
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
                textViewTimer.text = getString(R.string.quiz_timer, secondsRemaining)

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
                    textViewTimer.text = getString(R.string.quiz_timer, 0)
                    answerButtons.forEach { if (it.visibility == View.VISIBLE) it.isEnabled = false }

                    lifecycleScope.launch {
                        delay(FEEDBACK_DELAY)
                        viewModel.nextQuestion()
                    }
                }
            }
        }.start()
    }
    
    private fun resetButtonStyle(button: MaterialButton) {
        // Reset to neutral appearance
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        button.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }
    
    /**
     * Start a quiz attempt on the backend
     */
    private fun startBackendAttempt(quizId: String) {
        lifecycleScope.launch {
            Log.d(TAG, "Starting backend attempt for quiz: $quizId")
            quizStartTime = System.currentTimeMillis()
            
            val result = attemptRepository.startAttempt(quizId)
            
            result.onSuccess { attempt ->
                currentAttemptId = attempt.id
                Log.d(TAG, "Backend attempt started successfully: ${attempt.id}")
            }
            
            result.onFailure { error ->
                Log.e(TAG, "Failed to start backend attempt: ${error.message}", error)
                // Continue with local quiz even if backend fails
                Toast.makeText(
                    this@QuizActivity,
                    "Note: Quiz will run in offline mode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * Submit an answer to the backend
     */
    private fun submitAnswerToBackend(questionId: String, answer: String, timeToAnswer: Int) {
        val attemptId = currentAttemptId ?: return
        
        lifecycleScope.launch {
            Log.d(TAG, "Submitting answer - Attempt: $attemptId, Question: $questionId, Answer: $answer, Time: $timeToAnswer")
            
            val result = attemptRepository.submitAnswer(
                attemptId = attemptId,
                questionId = questionId,
                answer = answer,
                timeToAnswer = timeToAnswer
            )
            
            result.onSuccess { response ->
                val isCorrect = response["is_correct"] as? Boolean ?: false
                val pointsEarned = response["points_earned"] as? Double ?: 0.0
                Log.d(TAG, "Answer submitted - Correct: $isCorrect, Points: $pointsEarned")
            }
            
            result.onFailure { error ->
                Log.e(TAG, "Failed to submit answer: ${error.message}", error)
                // Continue with local quiz even if backend submission fails
            }
        }
    }
    
    /**
     * Complete the quiz attempt on the backend
     */
    private fun completeBackendAttempt(onComplete: (String?) -> Unit) {
        val attemptId = currentAttemptId
        
        if (attemptId == null) {
            Log.d(TAG, "No attempt ID - skipping backend completion")
            onComplete(null)
            return
        }
        
        lifecycleScope.launch {
            Log.d(TAG, "Completing backend attempt: $attemptId")
            
            val result = attemptRepository.completeAttempt(attemptId)
            
            result.onSuccess { completedAttempt ->
                Log.d(TAG, "Backend attempt completed successfully")
                Log.d(TAG, "Score: ${completedAttempt.totalScore}/${completedAttempt.maxScore}")
                Log.d(TAG, "XP Earned: ${completedAttempt.xpEarned}")
                onComplete(attemptId)
            }
            
            result.onFailure { error ->
                Log.e(TAG, "Failed to complete backend attempt: ${error.message}", error)
                // Still navigate to results with local data
                onComplete(attemptId)
            }
        }
    }
    
    private fun navigateToResults(score: Int, totalQuestions: Int, category: QuizCategory, difficulty: QuizDifficulty) {
        // Complete backend attempt first, then navigate
        completeBackendAttempt { attemptId ->
            val totalTimeTaken = if (quizStartTime > 0) {
                ((System.currentTimeMillis() - quizStartTime) / 1000).toInt()
            } else {
                300 // Default fallback
            }
            
            // Navigate to QuizRewardsActivity with real attempt ID from backend
            val intent = Intent(this, com.example.quizmaster.ui.quiz.QuizRewardsActivity::class.java).apply {
                putExtra("ATTEMPT_ID", attemptId ?: "local_attempt") // Use real ID or fallback
                putExtra("SCORE", score)
                putExtra("MAX_SCORE", totalQuestions * 10) // Assuming 10 points per question
                putExtra("TIME_TAKEN", totalTimeTaken)
                putExtra("CATEGORY", category.name)
                putExtra("DIFFICULTY", difficulty.name)
            }
            startActivity(intent)
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
