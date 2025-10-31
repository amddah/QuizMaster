package com.example.quizmaster.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.PerformanceTier
import com.example.quizmaster.data.model.XpConstants
import com.example.quizmaster.ui.profile.BadgesAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity showing rewards after completing a quiz
 */
class QuizRewardsActivity : AppCompatActivity() {

    private lateinit var viewModel: QuizRewardsViewModel
    private lateinit var badgesAdapter: BadgesAdapter

    // Views
    private lateinit var tierEmoji: TextView
    private lateinit var performanceMessage: TextView
    private lateinit var scorePercentage: TextView
    private lateinit var xpEarnedValue: TextView
    private lateinit var baseXpValue: TextView
    private lateinit var speedBonusRow: LinearLayout
    private lateinit var speedBonusValue: TextView
    private lateinit var perfectScoreRow: LinearLayout
    private lateinit var levelUpCard: MaterialCardView
    private lateinit var newLevelText: TextView
    private lateinit var newBadgesContainer: LinearLayout
    private lateinit var newBadgesRecyclerView: RecyclerView
    private lateinit var continueButton: MaterialButton
    private lateinit var shareButton: MaterialButton

    private var attemptId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_rewards)

        attemptId = intent.getStringExtra("ATTEMPT_ID")
        if (attemptId == null) {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[QuizRewardsViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // Load rewards data
        attemptId?.let { viewModel.loadRewards(it) }
    }

    private fun initializeViews() {
        // Performance tier (from included layout)
        val performanceTierComponent = findViewById<View>(R.id.performanceTierComponent)
        tierEmoji = performanceTierComponent.findViewById(R.id.tierEmoji)
        performanceMessage = performanceTierComponent.findViewById(R.id.performanceMessage)
        scorePercentage = performanceTierComponent.findViewById(R.id.scorePercentage)

        // XP breakdown
        xpEarnedValue = findViewById(R.id.xpEarnedValue)
        baseXpValue = findViewById(R.id.baseXpValue)
        speedBonusRow = findViewById(R.id.speedBonusRow)
        speedBonusValue = findViewById(R.id.speedBonusValue)
        perfectScoreRow = findViewById(R.id.perfectScoreRow)

        // Level up
        levelUpCard = findViewById(R.id.levelUpCard)
        newLevelText = findViewById(R.id.newLevelText)

        // Badges
        newBadgesContainer = findViewById(R.id.newBadgesContainer)
        newBadgesRecyclerView = findViewById(R.id.newBadgesRecyclerView)

        // Buttons
        continueButton = findViewById(R.id.continueButton)
        shareButton = findViewById(R.id.shareButton)
    }

    private fun setupRecyclerView() {
        badgesAdapter = BadgesAdapter()
        newBadgesRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@QuizRewardsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = badgesAdapter
        }
    }

    private fun setupButtons() {
        continueButton.setOnClickListener {
            // Navigate back to main screen
            finish()
        }

        shareButton.setOnClickListener {
            shareResults()
        }
    }

    private fun observeViewModel() {
        // Observe attempt
        viewModel.attempt.observe(this) { attempt ->
            attempt?.let {
                val percentage = if (it.maxScore > 0) {
                    (it.totalScore / it.maxScore) * 100
                } else 0.0

                // Update performance tier
                val tier = PerformanceTier.fromPercentage(percentage)
                tierEmoji.text = tier.emoji
                performanceMessage.text = tier.message
                scorePercentage.text = "${String.format("%.0f", percentage)}%"
            }
        }

        // Observe XP gain
        viewModel.xpGain.observe(this) { xpGain ->
            xpGain?.let {
                animateXpGain(it.xp_gained, it.level_up, it.level)
            }
        }

        // Observe new badges
        viewModel.newBadges.observe(this) { badges ->
            if (badges.isNotEmpty()) {
                newBadgesContainer.visibility = View.VISIBLE
                badgesAdapter.submitList(badges)
                animateNewBadges()
            }
        }
    }

    private fun animateXpGain(xpGained: Int, leveledUp: Boolean, newLevel: Int) {
        lifecycleScope.launch {
            // Animate XP value counting up
            var currentXp = 0
            val increment = if (xpGained > 100) 5 else 1
            val delayMs = if (xpGained > 100) 10L else 20L

            while (currentXp < xpGained) {
                currentXp = minOf(currentXp + increment, xpGained)
                xpEarnedValue.text = "+$currentXp XP"
                delay(delayMs)
            }

            // Show XP breakdown
            delay(300)
            baseXpValue.text = "+${(xpGained * 0.8).toInt()} XP" // Simplified

            // Show speed bonus if earned
            if (xpGained > 80) { // Simplified check
                delay(200)
                speedBonusRow.visibility = View.VISIBLE
                speedBonusValue.text = "+${(xpGained * 0.15).toInt()} XP"
            }

            // Show perfect score bonus if applicable
            if (scorePercentage.text.toString().startsWith("100")) {
                delay(200)
                perfectScoreRow.visibility = View.VISIBLE
            }

            // Show level up animation
            if (leveledUp) {
                delay(500)
                showLevelUp(newLevel)
            }
        }
    }

    private fun showLevelUp(newLevel: Int) {
        levelUpCard.visibility = View.VISIBLE
        newLevelText.text = "You reached Level $newLevel!"

        val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        levelUpCard.startAnimation(animation)

        // Add confetti or celebration effect here if desired
    }

    private fun animateNewBadges() {
        val animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        newBadgesRecyclerView.startAnimation(animation)
    }

    private fun shareResults() {
        val attempt = viewModel.attempt.value ?: return
        val percentage = if (attempt.maxScore > 0) {
            (attempt.totalScore / attempt.maxScore) * 100
        } else 0.0

        val xpGain = viewModel.xpGain.value?.xp_gained ?: 0
        val tier = PerformanceTier.fromPercentage(percentage)

        val shareText = """
            ${tier.emoji} I just scored ${String.format("%.0f", percentage)}% on a quiz!
            
            Earned +$xpGain XP
            ${if (viewModel.newBadges.value?.isNotEmpty() == true) "🏆 Unlocked ${viewModel.newBadges.value?.size} new badges!" else ""}
            
            Join me on QuizMaster!
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Share your achievement"))
    }

    override fun onBackPressed() {
        // Disable back button to force user to click continue
        // This ensures they see their rewards
    }
}
