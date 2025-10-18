package com.example.quizmaster.ui.quiz

import android.os.Bundle
import android.widget.*
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
        
        // Force EditText colors across this activity to avoid invisible text due to theme overrides
        forceEditTextColors(findViewById<View>(android.R.id.content))

        initViews()
        setupSpinners()
        setupRecyclerView()
        setupClickListeners()
    }

    // Recursively force EditText text color to black and hint color to text_secondary
    private fun forceEditTextColors(root: View) {
        when (root) {
            is ViewGroup -> {
                for (i in 0 until root.childCount) {
                    forceEditTextColors(root.getChildAt(i))
                }
            }
            is EditText -> {
                try {
                    root.setTextColor(android.graphics.Color.BLACK)
                    root.setHintTextColor(resources.getColor(R.color.text_secondary, theme))
                } catch (_: Exception) { /* defensive: ignore if color not found */ }
            }
            else -> { /* noop */ }
        }
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

        // Ensure input text is visible regardless of theme overrides
        quizTitleInput.setTextColor(android.graphics.Color.BLACK)
        quizDescriptionInput.setTextColor(android.graphics.Color.BLACK)
    }
    
    private fun setupSpinners() {
        // Category Spinner
        val categories = QuizCategory.entries.map { it.displayName }
        // Use custom spinner_item layout for the selected view (ensures padding + ellipsize)
        val categoryAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_selected_item, categories) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        // Ensure selected view text is visible
        ensureSpinnerSelectedTextBlack(categorySpinner)
        // Force white popup background and refresh selection so selected view is rendered with our custom layout
        categorySpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        categorySpinner.setSelection(categorySpinner.selectedItemPosition)

        // Difficulty Spinner
        val difficulties = QuizDifficulty.entries.map { it.displayName }
        val difficultyAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_selected_item, difficulties) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = difficultyAdapter
        ensureSpinnerSelectedTextBlack(difficultySpinner)
        difficultySpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        difficultySpinner.setSelection(difficultySpinner.selectedItemPosition)

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
        val courseAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_selected_item, courses) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = courseAdapter
        ensureSpinnerSelectedTextBlack(courseSpinner)
        courseSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        courseSpinner.setSelection(courseSpinner.selectedItemPosition)
    }

    // Force the selected (closed) Spinner text color to black. Use when adapter.getView may not affect the rendered selected view.
    private fun ensureSpinnerSelectedTextBlack(spinner: Spinner) {
        // preserve any existing listener by wrapping? For simplicity, set a light listener that only ensures color; it won't override more complex listeners used ailleurs.
        spinner.post {
            (spinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (spinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupRecyclerView() {
        // Pass a mutable list and provide both delete and edit handlers
        questionAdapter = QuestionAdapter(questions as MutableList<QuestionModel>, onDelete = { position ->
            if (position in questions.indices) {
                questions.removeAt(position)
                questionAdapter.notifyItemRemoved(position)
                // refresh following items so any index-related info is updated
                if (position < questions.size) questionAdapter.notifyItemRangeChanged(position, questions.size - position)
            }
        }, onEdit = { position ->
            if (position in questions.indices) showEditQuestionDialog(position)
        })
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
    
    @Suppress("DEPRECATION")
    private fun showAddQuestionDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_question, null)
        
        val questionTextInput = view.findViewById<EditText>(R.id.questionTextInput)
        // Force text color on dialog input to avoid theme tint making text invisible
        questionTextInput.setTextColor(android.graphics.Color.BLACK)
        questionTextInput.setHintTextColor(getColor(R.color.text_secondary))
         val questionTypeSpinner = view.findViewById<Spinner>(R.id.questionTypeSpinner)
         val optionsContainer = view.findViewById<LinearLayout>(R.id.optionsContainer)
         val correctAnswerSpinner = view.findViewById<Spinner>(R.id.correctAnswerSpinner)
         val dialogCancelButton = view.findViewById<Button>(R.id.cancelButton)
         val dialogAddButton = view.findViewById<Button>(R.id.addButton)

        val typeAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_selected_item, listOf("True/False", "Multiple Choice")) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionTypeSpinner.adapter = typeAdapter
        ensureSpinnerSelectedTextBlack(questionTypeSpinner)
        questionTypeSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        questionTypeSpinner.setSelection(questionTypeSpinner.selectedItemPosition)

        val optionInputs = mutableListOf<EditText>()
        
        questionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Ensure selected text stays black when user picks a type
                (questionTypeSpinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK)
                 optionsContainer.removeAllViews()
                 optionInputs.clear()

                if (position == 1) { // Multiple Choice
                    repeat(4) { i ->
                        val optionInput = EditText(this@QuizCreationActivity).apply {
                            hint = "Option ${i + 1}"
                            textSize = 16f
                            setPadding(16, 16, 16, 16)
                            // Force black text so it's visible on white popup background
                            setTextColor(android.graphics.Color.BLACK)
                            // Keep hint color as secondary gray
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
                    val answerAdapter = object : ArrayAdapter<String>(this@QuizCreationActivity, R.layout.spinner_selected_item, answerOptions) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                    }
                    answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    correctAnswerSpinner.adapter = answerAdapter
                    ensureSpinnerSelectedTextBlack(correctAnswerSpinner)
                    correctAnswerSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
                    correctAnswerSpinner.setSelection(correctAnswerSpinner.selectedItemPosition)
                 } else { // True/False
                    val answerOptions = listOf("True", "False")
                    val answerAdapter = object : ArrayAdapter<String>(this@QuizCreationActivity, R.layout.spinner_selected_item, answerOptions) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                    }
                    answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    correctAnswerSpinner.adapter = answerAdapter
                    ensureSpinnerSelectedTextBlack(correctAnswerSpinner)
                    correctAnswerSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
                    correctAnswerSpinner.setSelection(correctAnswerSpinner.selectedItemPosition)
                 }

                 correctAnswerSpinner.isEnabled = true
             }
             override fun onNothingSelected(parent: AdapterView<*>?) {}
         }

        // Initialize with True/False options
        val initAnswerOptions = listOf("True", "False")
        val initAnswerAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_selected_item, initAnswerOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        initAnswerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        correctAnswerSpinner.adapter = initAnswerAdapter
        ensureSpinnerSelectedTextBlack(correctAnswerSpinner)
        correctAnswerSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        correctAnswerSpinner.setSelection(correctAnswerSpinner.selectedItemPosition)
        correctAnswerSpinner.isEnabled = true
        
        // Hide underlying action buttons so they don't appear duplicated under the dialog
        actionButtonsContainer.visibility = View.GONE
        addQuestionButton.visibility = View.GONE

         builder.setView(view)
         val alert = builder.create()
        // Ensure dialog resizes when keyboard appears so inputs are not obscured
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

         alert.setOnDismissListener {
             // restore underlying buttons visibility
             actionButtonsContainer.visibility = View.VISIBLE
             addQuestionButton.visibility = View.VISIBLE
         }
         alert.show()

        // After the dialog is shown ensure the selected view text color is set (view may not be attached before show)
        questionTypeSpinner.post { (questionTypeSpinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK) }
        correctAnswerSpinner.post { (correctAnswerSpinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK) }

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
    
    // Dialog to edit an existing question in-place
    @Suppress("DEPRECATION")
    private fun showEditQuestionDialog(position: Int) {
        val existing = questions[position]
        val builder = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_question, null)

        val questionTextInput = view.findViewById<EditText>(R.id.questionTextInput)
        questionTextInput.setTextColor(android.graphics.Color.BLACK)
        questionTextInput.setHintTextColor(getColor(R.color.text_secondary))
        val questionTypeSpinner = view.findViewById<Spinner>(R.id.questionTypeSpinner)
        val optionsContainer = view.findViewById<LinearLayout>(R.id.optionsContainer)
        val correctAnswerSpinner = view.findViewById<Spinner>(R.id.correctAnswerSpinner)
        val dialogCancelButton = view.findViewById<Button>(R.id.cancelButton)
        val dialogAddButton = view.findViewById<Button>(R.id.addButton)

        // Setup type spinner (same as add dialog)
        val typeAdapter = object : ArrayAdapter<String>(this, R.layout.spinner_selected_item, listOf("True/False", "Multiple Choice")) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(android.graphics.Color.BLACK)
                view.setPadding(24, 16, 24, 16)
                return view
            }
        }
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionTypeSpinner.adapter = typeAdapter
        ensureSpinnerSelectedTextBlack(questionTypeSpinner)
        questionTypeSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)

        val optionInputs = mutableListOf<EditText>()

        // Helper to build answer spinner for multiple choice
        fun setupAnswerSpinnerForOptions(optionCount: Int) {
            val answerOptions = (1..optionCount).map { "Option $it" }
            val answerAdapter = object : ArrayAdapter<String>(this@QuizCreationActivity, R.layout.spinner_selected_item, answerOptions) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(android.graphics.Color.BLACK)
                    view.setPadding(24, 16, 24, 16)
                    return view
                }
                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent) as TextView
                    view.setTextColor(android.graphics.Color.BLACK)
                    view.setPadding(24, 16, 24, 16)
                    return view
                }
            }
            answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            correctAnswerSpinner.adapter = answerAdapter
            ensureSpinnerSelectedTextBlack(correctAnswerSpinner)
            correctAnswerSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
        }

        questionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                (questionTypeSpinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK)
                optionsContainer.removeAllViews()
                optionInputs.clear()

                if (pos == 1) { // Multiple Choice
                    // Create four option inputs (or reuse count from existing question)
                    val initialOptions = if (existing.options.isNotEmpty()) existing.options else List(4) { "" }
                    val count = maxOf(4, initialOptions.size)
                    repeat(count) { i ->
                        val optionInput = EditText(this@QuizCreationActivity).apply {
                            hint = "Option ${i + 1}"
                            textSize = 16f
                            setPadding(16, 16, 16, 16)
                            setTextColor(android.graphics.Color.BLACK)
                            setHintTextColor(getColor(R.color.text_secondary))
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { setMargins(0, 8, 0, 8) }
                        }
                        optionInput.setBackgroundResource(R.drawable.input_rounded_background)
                        optionInput.setText(if (i < initialOptions.size) initialOptions[i] else "")
                        optionsContainer.addView(optionInput)
                        optionInputs.add(optionInput)
                    }
                    setupAnswerSpinnerForOptions(optionInputs.size)
                } else { // True/False
                    val answerOptions = listOf("True", "False")
                    val answerAdapter = object : ArrayAdapter<String>(this@QuizCreationActivity, R.layout.spinner_selected_item, answerOptions) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(android.graphics.Color.BLACK)
                            view.setPadding(24, 16, 24, 16)
                            return view
                        }
                    }
                    answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    correctAnswerSpinner.adapter = answerAdapter
                    ensureSpinnerSelectedTextBlack(correctAnswerSpinner)
                    correctAnswerSpinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background)
                }

                correctAnswerSpinner.isEnabled = true
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Prefill current values
        questionTextInput.setText(existing.questionText)
        questionTypeSpinner.post { questionTypeSpinner.setSelection(if (existing.type == QuestionType.MULTIPLE_CHOICE) 1 else 0) }

        // Force initialization of answer spinner by triggering selection handler
        questionTypeSpinner.post {
            // After type spinner sets up options, preselect correct answer
            if (existing.type == QuestionType.MULTIPLE_CHOICE) {
                // find correct index if stored as numeric string
                val correctIdx = existing.correctAnswer.toIntOrNull() ?: 0
                correctAnswerSpinner.post { if (correctIdx in 0 until correctAnswerSpinner.count) correctAnswerSpinner.setSelection(correctIdx) }
            } else {
                val sel = if (existing.correctAnswer.equals("true", ignoreCase = true)) 0 else 1
                correctAnswerSpinner.post { correctAnswerSpinner.setSelection(sel) }
            }
        }

        // Hide underlying action buttons so they don't appear duplicated under the dialog
        actionButtonsContainer.visibility = View.GONE
        addQuestionButton.visibility = View.GONE

        builder.setView(view)
        val alert = builder.create()
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        alert.setOnDismissListener {
            actionButtonsContainer.visibility = View.VISIBLE
            addQuestionButton.visibility = View.VISIBLE
        }

        alert.show()

        // Ensure text colors for spinners inside dialog
        questionTypeSpinner.post { (questionTypeSpinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK) }
        correctAnswerSpinner.post { (correctAnswerSpinner.selectedView as? TextView)?.setTextColor(android.graphics.Color.BLACK) }

        dialogCancelButton.setOnClickListener { alert.dismiss() }

        // Update button text to indicate edit
        dialogAddButton.text = getString(R.string.update)
        dialogAddButton.setOnClickListener {
            val questionText = questionTextInput.text.toString()
            if (questionText.isNotEmpty()) {
                val type = if (questionTypeSpinner.selectedItemPosition == 0) QuestionType.TRUE_FALSE else QuestionType.MULTIPLE_CHOICE

                val options: List<String>
                val correctAnswerIndex: String

                if (type == QuestionType.MULTIPLE_CHOICE) {
                    val collected = optionInputs.map { it.text.toString() }.filter { it.isNotEmpty() }
                    if (collected.isEmpty()) {
                        Toast.makeText(this, "Please provide at least one option", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    options = collected
                    correctAnswerIndex = correctAnswerSpinner.selectedItemPosition.toString()
                } else {
                    options = listOf("True", "False")
                    correctAnswerIndex = if (correctAnswerSpinner.selectedItemPosition == 0) "true" else "false"
                }

                val updated = existing.copy(
                    questionText = questionText,
                    type = type,
                    correctAnswer = correctAnswerIndex,
                    options = options
                )

                questions[position] = updated
                questionAdapter.notifyItemChanged(position)
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
    private val questions: MutableList<QuestionModel>,
    private val onDelete: (Int) -> Unit,
    private val onEdit: (Int) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val questionType: TextView = itemView.findViewById(R.id.questionType)
        private val deleteButton: androidx.appcompat.widget.AppCompatImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(question: QuestionModel) {
            questionText.text = question.questionText
            // Show type + extras (options count and points) to give more info in the item UI
            questionType.text = "${question.type.name} • ${question.options.size} options • ${question.maxScore} pts"

            // Accessibility: describe the delete action
            deleteButton.contentDescription = itemView.context.getString(R.string.delete_question)

            deleteButton.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                // Prevent double-clicks
                deleteButton.isEnabled = false

                // Confirmation dialog before deletion
                android.app.AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.confirm_delete_title))
                    .setMessage(itemView.context.getString(R.string.confirm_delete_message))
                    .setPositiveButton(itemView.context.getString(R.string.delete)) { _, _ ->
                        onDelete(pos)
                    }
                    .setNegativeButton(itemView.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        // Re-enable after dialog closes
                        deleteButton.isEnabled = true
                    }
                    .show()
            }

            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onEdit(pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount() = questions.size
}
