package com.example.quizmaster

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty
import com.example.quizmaster.ui.QuizActivity

class MainActivity : AppCompatActivity() {
    
    // Define color scheme
    private val primaryGreen = Color.parseColor("#4CAF50")      // Material Green
    private val darkGreen = Color.parseColor("#388E3C")        // Dark Green
    private val lightGreen = Color.parseColor("#E8F5E8")       // Light Green Background
    private val white = Color.WHITE
    private val textDark = Color.parseColor("#2E2E2E")
    private val textLight = Color.parseColor("#666666")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create modern UI with white and green theme
        createModernLayout()
    }

    private fun createModernLayout() {
        // Main scroll container with light green background
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setBackgroundColor(lightGreen)
        mainLayout.setPadding(32, 0, 32, 32)

        // Header section with green background
        val headerCard = createHeaderSection()
        mainLayout.addView(headerCard)

        // Spacing
        mainLayout.addView(createSpacing(24))

        // Stats card (white background)
        val statsCard = createStatsCard()
        mainLayout.addView(statsCard)

        // Spacing
        mainLayout.addView(createSpacing(24))

        // Quiz options section
        val quizOptionsTitle = createSectionTitle("Choose Your Quiz")
        mainLayout.addView(quizOptionsTitle)
        mainLayout.addView(createSpacing(16))

        // Quiz buttons in cards
        mainLayout.addView(createQuizCard("🧠 General Knowledge", "Easy Level", QuizCategory.GENERAL, QuizDifficulty.EASY, true))
        mainLayout.addView(createSpacing(12))

        mainLayout.addView(createQuizCard("🔬 Science & Nature", "Medium Level", QuizCategory.SCIENCE, QuizDifficulty.MEDIUM, false))
        mainLayout.addView(createSpacing(12))

        mainLayout.addView(createQuizCard("📚 History", "Hard Level", QuizCategory.HISTORY, QuizDifficulty.HARD, false))
        mainLayout.addView(createSpacing(12))

        mainLayout.addView(createQuizCard("💻 Technology", "Medium Level", QuizCategory.TECHNOLOGY, QuizDifficulty.MEDIUM, false))
        mainLayout.addView(createSpacing(24))

        // Action buttons
        val actionsCard = createActionsCard()
        mainLayout.addView(actionsCard)

        setContentView(mainLayout)
    }

    private fun createHeaderSection(): CardView {
        val card = CardView(this)
        card.radius = 20f
        card.setCardBackgroundColor(primaryGreen)
        card.cardElevation = 8f
        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.topMargin = 40
        card.layoutParams = cardParams

        val headerLayout = LinearLayout(this)
        headerLayout.orientation = LinearLayout.VERTICAL
        headerLayout.gravity = Gravity.CENTER
        headerLayout.setPadding(32, 40, 32, 40)

        // App title
        val titleText = TextView(this)
        titleText.text = "🧠 QuizMaster"
        titleText.textSize = 32f
        titleText.setTypeface(null, Typeface.BOLD)
        titleText.setTextColor(white)
        titleText.gravity = Gravity.CENTER

        // Subtitle
        val subtitleText = TextView(this)
        subtitleText.text = "Test your knowledge and become a quiz champion!"
        subtitleText.textSize = 16f
        subtitleText.setTextColor(Color.parseColor("#E8F5E8"))
        subtitleText.gravity = Gravity.CENTER
        val subtitleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        subtitleParams.topMargin = 8
        subtitleText.layoutParams = subtitleParams

        headerLayout.addView(titleText)
        headerLayout.addView(subtitleText)
        card.addView(headerLayout)

        return card
    }

    private fun createStatsCard(): CardView {
        val card = CardView(this)
        card.radius = 16f
        card.setCardBackgroundColor(white)
        card.cardElevation = 4f

        val statsLayout = LinearLayout(this)
        statsLayout.orientation = LinearLayout.HORIZONTAL
        statsLayout.setPadding(24, 20, 24, 20)

        // Best Score
        val bestScoreLayout = createStatItem("🏆", "Best Score", "85%")
        val bestScoreParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        bestScoreParams.weight = 1f
        bestScoreLayout.layoutParams = bestScoreParams

        // Quizzes Taken
        val quizzesLayout = createStatItem("📊", "Quizzes", "12")
        val quizzesParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        quizzesParams.weight = 1f
        quizzesLayout.layoutParams = quizzesParams

        // Average Score
        val avgLayout = createStatItem("⭐", "Average", "76%")
        val avgParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        avgParams.weight = 1f
        avgLayout.layoutParams = avgParams

        statsLayout.addView(bestScoreLayout)
        statsLayout.addView(quizzesLayout)
        statsLayout.addView(avgLayout)
        card.addView(statsLayout)

        return card
    }

    private fun createStatItem(icon: String, label: String, value: String): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        val iconText = TextView(this)
        iconText.text = icon
        iconText.textSize = 24f
        iconText.gravity = Gravity.CENTER

        val valueText = TextView(this)
        valueText.text = value
        valueText.textSize = 20f
        valueText.setTypeface(null, Typeface.BOLD)
        valueText.setTextColor(primaryGreen)
        valueText.gravity = Gravity.CENTER

        val labelText = TextView(this)
        labelText.text = label
        labelText.textSize = 12f
        labelText.setTextColor(textLight)
        labelText.gravity = Gravity.CENTER

        layout.addView(iconText)
        layout.addView(valueText)
        layout.addView(labelText)

        return layout
    }

    private fun createSectionTitle(title: String): TextView {
        val titleText = TextView(this)
        titleText.text = title
        titleText.textSize = 20f
        titleText.setTypeface(null, Typeface.BOLD)
        titleText.setTextColor(textDark)
        return titleText
    }

    private fun createQuizCard(title: String, difficulty: String, category: QuizCategory, difficultyLevel: QuizDifficulty, isPrimary: Boolean): CardView {
        val card = CardView(this)
        card.radius = 12f
        card.setCardBackgroundColor(white)
        card.cardElevation = 3f

        val cardLayout = LinearLayout(this)
        cardLayout.orientation = LinearLayout.HORIZONTAL
        cardLayout.setPadding(20, 16, 20, 16)
        cardLayout.gravity = Gravity.CENTER_VERTICAL

        // Left content
        val leftLayout = LinearLayout(this)
        leftLayout.orientation = LinearLayout.VERTICAL
        val leftParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        leftParams.weight = 1f
        leftLayout.layoutParams = leftParams

        val titleText = TextView(this)
        titleText.text = title
        titleText.textSize = 18f
        titleText.setTypeface(null, Typeface.BOLD)
        titleText.setTextColor(textDark)

        val difficultyText = TextView(this)
        difficultyText.text = difficulty
        difficultyText.textSize = 14f
        difficultyText.setTextColor(textLight)

        leftLayout.addView(titleText)
        leftLayout.addView(difficultyText)

        // Right button
        val playButton = Button(this)
        playButton.text = if (isPrimary) "START" else "PLAY"
        playButton.textSize = 14f
        playButton.setTypeface(null, Typeface.BOLD)
        playButton.setTextColor(white)
        playButton.setBackgroundColor(if (isPrimary) primaryGreen else darkGreen)
        playButton.setPadding(24, 12, 24, 12)

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        playButton.layoutParams = buttonParams

        playButton.setOnClickListener {
            startQuiz(category, difficultyLevel)
        }

        cardLayout.addView(leftLayout)
        cardLayout.addView(playButton)
        card.addView(cardLayout)

        // Add click listener to entire card
        card.setOnClickListener {
            startQuiz(category, difficultyLevel)
        }

        return card
    }

    private fun createActionsCard(): CardView {
        val card = CardView(this)
        card.radius = 16f
        card.setCardBackgroundColor(white)
        card.cardElevation = 4f

        val actionsLayout = LinearLayout(this)
        actionsLayout.orientation = LinearLayout.VERTICAL
        actionsLayout.setPadding(24, 20, 24, 20)

        // History button
        val historyButton = Button(this)
        historyButton.text = "📊 VIEW QUIZ HISTORY"
        historyButton.textSize = 16f
        historyButton.setTypeface(null, Typeface.BOLD)
        historyButton.setTextColor(primaryGreen)
        historyButton.setBackgroundColor(Color.TRANSPARENT)
        historyButton.setPadding(0, 16, 0, 16)

        val historyParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        historyButton.layoutParams = historyParams

        historyButton.setOnClickListener {
            try {
                val intent = Intent(this, com.example.quizmaster.ui.HistoryActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                createSimpleMessage("History feature coming soon!")
            }
        }

        actionsLayout.addView(historyButton)
        card.addView(actionsLayout)

        return card
    }

    private fun createSpacing(height: Int): TextView {
        val spacing = TextView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        )
        spacing.layoutParams = params
        return spacing
    }

    private fun startQuiz(category: QuizCategory, difficulty: QuizDifficulty) {
        try {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra(QuizActivity.EXTRA_CATEGORY, category.name)
            intent.putExtra(QuizActivity.EXTRA_DIFFICULTY, difficulty.name)
            startActivity(intent)
        } catch (e: Exception) {
            createSimpleMessage("Quiz feature is being prepared...")
        }
    }
    
    private fun createSimpleMessage(message: String) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(lightGreen)
        layout.setPadding(50, 100, 50, 50)
        layout.gravity = Gravity.CENTER

        val messageText = TextView(this)
        messageText.text = message
        messageText.textSize = 18f
        messageText.setTextColor(textDark)
        messageText.gravity = Gravity.CENTER

        val backButton = Button(this)
        backButton.text = "Back to Main Menu"
        backButton.textSize = 16f
        backButton.setTextColor(white)
        backButton.setBackgroundColor(primaryGreen)
        backButton.setPadding(32, 16, 32, 16)

        val backParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        backParams.topMargin = 32
        backButton.layoutParams = backParams

        backButton.setOnClickListener {
            createModernLayout()
        }

        layout.addView(messageText)
        layout.addView(backButton)
        setContentView(layout)
    }
}