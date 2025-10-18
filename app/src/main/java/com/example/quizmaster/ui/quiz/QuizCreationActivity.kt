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
import kotlinx.coroutines.flow.first

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
    private lateinit var actionButtonsContainer: LinearLayout
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
        actionButtonsContainer = findViewById(R.id.actionButtonsContainer)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupSpinners() {
        // Category Spinner
        val categories = QuizCategory.entries.map { it.displayName }
        // Use custom spinner_item layout for the selected view (ensures padding + ellipsize)
        val categoryAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, categories) {
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        
        // Difficulty Spinner
        val difficulties = QuizDifficulty.entries.map { it.displayName }
        val difficultyAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, difficulties) {
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = difficultyAdapter
        
        // Course Spinner with proper text visibility
        val courses = listOf(
            "PROG101 - Programming 101",
            "DS201 - Data Structures",
            "ALG301 - Algorithms",
            "DB401 - Database Systems",
            "WEB101 - Web Development",
            "MOB201 - Mobile Development",
            "SE301 - Software Engineering",
            "NET401 - Computer Networks"
        )
        val courseAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, courses) {
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = courseAdapter
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
        val builder = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_question, null)
        
        val questionTextInput = view.findViewById<EditText>(R.id.questionTextInput)
        val questionTypeSpinner = view.findViewById<Spinner>(R.id.questionTypeSpinner)
        val optionsContainer = view.findViewById<LinearLayout>(R.id.optionsContainer)
        val correctAnswerSpinner = view.findViewById<Spinner>(R.id.correctAnswerSpinner)
        val dialogCancelButton = view.findViewById<Button>(R.id.cancelButton)
        val dialogAddButton = view.findViewById<Button>(R.id.addButton)

        val typeAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOf("True/False", "Multiple Choice")) {
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionTypeSpinner.adapter = typeAdapter
        
        val optionInputs = mutableListOf<EditText>()
        
        questionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                optionsContainer.removeAllViews()
                optionInputs.clear()

                if (position == 1) { // Multiple Choice
                    repeat(4) { i ->
                        val optionInput = EditText(this@QuizCreationActivity).apply {
                            hint = "Option ${i + 1}"
                            textSize = 16f
                            setPadding(16, 16, 16, 16)
                            setTextColor(getColor(R.color.text_primary))
                            setHintTextColor(getColor(R.color.text_secondary))
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { setMargins(0, 8, 0, 8) }
                        }
                        // Appliquer un fond arrondi blanc uniforme
                        optionInput.setBackgroundResource(R.drawable.input_rounded_background)
                        optionsContainer.addView(optionInput)
                        optionInputs.add(optionInput)
                    }

                    // Update correct answer spinner for multiple choice
                    val answerOptions = listOf("Option 1", "Option 2", "Option 3", "Option 4")
                    val answerAdapter = object : ArrayAdapter<String>(this@QuizCreationActivity, android.R.layout.simple_spinner_item, answerOptions) {
                        override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                    }
                    answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    correctAnswerSpinner.adapter = answerAdapter
                } else { // True/False
                    val answerOptions = listOf("True", "False")
                    val answerAdapter = object : ArrayAdapter<String>(this@QuizCreationActivity, android.R.layout.simple_spinner_item, answerOptions) {
                        override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                    }
                    answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    correctAnswerSpinner.adapter = answerAdapter
                }

                correctAnswerSpinner.isEnabled = true
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Initialize with True/False options
        val initAnswerOptions = listOf("True", "False")
        val initAnswerAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, initAnswerOptions) {
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        initAnswerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        correctAnswerSpinner.adapter = initAnswerAdapter
        correctAnswerSpinner.isEnabled = true
        
        // Hide underlying action buttons so they don't appear duplicated under the dialog
        actionButtonsContainer.visibility = android.view.View.GONE
        addQuestionButton.visibility = android.view.View.GONE

         builder.setView(view)
         val alert = builder.create()
         alert.setOnDismissListener {
             // restore underlying buttons visibility
             actionButtonsContainer.visibility = android.view.View.VISIBLE
             addQuestionButton.visibility = android.view.View.VISIBLE
         }
         alert.show()

        // Hook dialog internal buttons
        dialogCancelButton.setOnClickListener {
            alert.dismiss()
        }

        dialogAddButton.setOnClickListener {
            val questionText = questionTextInput.text.toString()
            if (questionText.isNotEmpty()) {
                val type = if (questionTypeSpinner.selectedItemPosition == 0)
                    QuestionType.TRUE_FALSE else QuestionType.MULTIPLE_CHOICE
                
                val options: List<String>
                val correctAnswerIndex: String
                
                if (type == QuestionType.MULTIPLE_CHOICE) {
                    options = optionInputs.map { it.text.toString() }.filter { it.isNotEmpty() }
                    if (options.isEmpty()) {
                        Toast.makeText(this, "Please provide at least one option", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    correctAnswerIndex = correctAnswerSpinner.selectedItemPosition.toString()
                } else {
                    options = listOf("True", "False")
                    correctAnswerIndex = if (correctAnswerSpinner.selectedItemPosition == 0) "true" else "false"
                }
                
                val question = QuestionModel(
                    id = System.currentTimeMillis().toString(),
                    questionText = questionText,
                    type = type,
                    correctAnswer = correctAnswerIndex,
                    options = options,
                    timeLimit = 60,
                    maxScore = 10
                )
                
                questions.add(question)
                questionAdapter.notifyItemInserted(questions.size - 1)
                alert.dismiss()
            } else {
                Toast.makeText(this, "Please enter question text", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveQuiz() {
        val title = quizTitleInput.text.toString()
        val description = quizDescriptionInput.text.toString()
        val courseSelection = courseSpinner.selectedItem?.toString() ?: ""

        if (title.isEmpty() || questions.isEmpty() || courseSelection.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and add at least one question", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Extract course ID from the selection (format: "PROG101 - Programming 101")
        val courseId = courseSelection.split(" - ").firstOrNull()?.trim() ?: "UNKNOWN"
        val courseName = courseSelection.split(" - ").lastOrNull()?.trim() ?: courseSelection
        
        progressBar.visibility = ProgressBar.VISIBLE

        lifecycleScope.launch {
            try {
                val category = QuizCategory.entries[categorySpinner.selectedItemPosition]
                val difficulty = QuizDifficulty.entries[difficultySpinner.selectedItemPosition]

                // Get current user from Flow (suspend) - first emission
                val currentUser = sessionManager.currentUser.first()

                val status = if (currentUser?.role == UserRole.PROFESSOR)
                    ApprovalStatus.APPROVED else ApprovalStatus.PENDING
                
                // Prepare questions with proper order
                val orderedQuestions = questions.mapIndexed { index, question ->
                    QuestionModel(
                        id = question.id,
                        questionText = question.questionText,
                        type = question.type,
                        correctAnswer = question.correctAnswer,
                        options = question.options,
                        timeLimit = question.timeLimit,
                        maxScore = question.maxScore,
                        order = index + 1,
                        explanation = question.explanation
                    )
                }
                
                val quiz = QuizModel(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    description = description,
                    category = category,
                    difficulty = difficulty,
                    questions = orderedQuestions,
                    creatorId = currentUser?.id ?: "",
                    creatorName = "${currentUser?.firstName} ${currentUser?.lastName}",
                    creatorRole = currentUser?.role ?: UserRole.STUDENT,
                    linkedCourseId = courseId,
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
