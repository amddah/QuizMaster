package com.example.quizmaster.ui.quiz

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.model.QuizModel
import com.example.quizmaster.data.model.QuestionModel
import com.example.quizmaster.data.model.QuestionType
import com.example.quizmaster.data.model.ApprovalStatus
import com.example.quizmaster.data.model.UserRole
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.QuizApiService
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import kotlinx.coroutines.launch

/**
 * Quiz Creation Activity - Allows students and professors to create quizzes
 */
class QuizCreationActivity : AppCompatActivity() {
    
    private lateinit var quizTitleInput: EditText
    private lateinit var quizDescriptionInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var courseSpinner: Spinner
    private lateinit var questionsRecyclerView: RecyclerView
    private lateinit var addQuestionButton: Button
    private lateinit var submitButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var sessionManager: UserSessionManager
    private lateinit var quizApiService: QuizApiService
    
    private val questions = mutableListOf<QuestionModel>()
    private lateinit var questionAdapter: QuestionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_creation)
        
        sessionManager = UserSessionManager.getInstance(this)
        quizApiService = ApiClient.quizApiService
        
        initViews()
        setupSpinners()
        setupRecyclerView()
        setupClickListeners()
    }
    
    private fun initViews() {
        quizTitleInput = findViewById(R.id.quizTitleInput)
        quizDescriptionInput = findViewById(R.id.quizDescriptionInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        difficultySpinner = findViewById(R.id.difficultySpinner)
        courseSpinner = findViewById(R.id.courseSpinner)
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView)
        addQuestionButton = findViewById(R.id.addQuestionButton)
        submitButton = findViewById(R.id.submitButton)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupSpinners() {
        val categories = QuizCategory.values().map { it.displayName }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        
        val difficulties = QuizDifficulty.values().map { it.displayName }
        val difficultyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficulties)
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = difficultyAdapter
    }
    
    private fun setupRecyclerView() {
        questionAdapter = QuestionAdapter(questions) { position ->
            questions.removeAt(position)
            questionAdapter.notifyItemRemoved(position)
        }
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = questionAdapter
    }
    
    private fun setupClickListeners() {
        addQuestionButton.setOnClickListener {
            showAddQuestionDialog()
        }
        
        submitButton.setOnClickListener {
            saveQuiz()
        }
    }
    
    private fun showAddQuestionDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_question, null)
        
        val questionTextInput = view.findViewById<EditText>(R.id.questionTextInput)
        val questionTypeSpinner = view.findViewById<Spinner>(R.id.questionTypeSpinner)
        val optionsContainer = view.findViewById<LinearLayout>(R.id.optionsContainer)
        val correctAnswerSpinner = view.findViewById<Spinner>(R.id.correctAnswerSpinner)
        
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("True/False", "Multiple Choice")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionTypeSpinner.adapter = typeAdapter
        
        questionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                optionsContainer.removeAllViews()
                if (position == 1) { // Multiple Choice
                    repeat(4) { i ->
                        val optionInput = EditText(this@QuizCreationActivity).apply {
                            hint = "Option ${i + 1}"
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { setMargins(0, 8, 0, 8) }
                        }
                        optionsContainer.addView(optionInput)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        dialog.setView(view)
        dialog.setPositiveButton("Add") { _, _ ->
            val questionText = questionTextInput.text.toString()
            if (questionText.isNotEmpty()) {
                val type = if (questionTypeSpinner.selectedItemPosition == 0) 
                    QuestionType.TRUE_FALSE else QuestionType.MULTIPLE_CHOICE
                
                val options = if (type == QuestionType.MULTIPLE_CHOICE) {
                    (0 until optionsContainer.childCount).map {
                        (optionsContainer.getChildAt(it) as EditText).text.toString()
                    }
                } else {
                    listOf("True", "False")
                }
                
                val question = QuestionModel(
                    id = System.currentTimeMillis().toString(),
                    questionText = questionText,
                    type = type,
                    correctAnswer = "0",
                    options = options,
                    timeLimit = 15,
                    maxScore = 100
                )
                
                questions.add(question)
                questionAdapter.notifyItemInserted(questions.size - 1)
            }
        }
        dialog.setNegativeButton("Cancel", null)
        dialog.show()
    }
    
    private fun saveQuiz() {
        val title = quizTitleInput.text.toString()
        val description = quizDescriptionInput.text.toString()
        val courseName = courseSpinner.selectedItem?.toString() ?: ""

        if (title.isEmpty() || questions.isEmpty() || courseName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and add at least one question", Toast.LENGTH_SHORT).show()
            return
        }
        
        progressBar.visibility = ProgressBar.VISIBLE

        lifecycleScope.launch {
            try {
                val category = QuizCategory.values()[categorySpinner.selectedItemPosition]
                val difficulty = QuizDifficulty.values()[difficultySpinner.selectedItemPosition]
                
                // Get current user from Flow
                var currentUser: com.example.quizmaster.data.model.User? = null
                sessionManager.currentUser.collect { user ->
                    currentUser = user
                    return@collect
                }

                val status = if (currentUser?.role == UserRole.PROFESSOR)
                    ApprovalStatus.APPROVED else ApprovalStatus.PENDING
                
                val quiz = QuizModel(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    description = description,
                    category = category,
                    difficulty = difficulty,
                    questions = questions,
                    creatorId = currentUser?.id ?: "",
                    creatorName = "${currentUser?.firstName} ${currentUser?.lastName}",
                    creatorRole = currentUser?.role ?: UserRole.STUDENT,
                    linkedCourseId = "course-${System.currentTimeMillis()}",
                    linkedCourseName = courseName,
                    approvalStatus = status
                )
                
                val response = quizApiService.createQuiz(quiz)

                progressBar.visibility = ProgressBar.GONE
                
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@QuizCreationActivity,
                        "Quiz created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@QuizCreationActivity,
                        "Failed to create quiz: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@QuizCreationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * Adapter for displaying questions in RecyclerView
 */
class QuestionAdapter(
    private val questions: List<QuestionModel>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    
    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val questionType: TextView = itemView.findViewById(R.id.questionType)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        
        fun bind(question: QuestionModel, position: Int) {
            questionText.text = question.questionText
            questionType.text = question.type.name
            deleteButton.setOnClickListener { onDelete(position) }
        }
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }
    
    override fun getItemCount() = questions.size
}
