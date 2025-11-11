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
import com.example.quizmaster.data.repository.QuizAttemptRepository
import com.example.quizmaster.ui.profile.LeaderboardAdapter
import android.widget.ImageButton
import android.widget.Toast
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
    private lateinit var leaderboardRecycler: RecyclerView
    private lateinit var leaderboardCard: com.google.android.material.card.MaterialCardView
    private lateinit var firstPlaceText: TextView
    private lateinit var secondPlaceText: TextView
    private lateinit var thirdPlaceText: TextView
    private lateinit var shareIconButton: ImageButton
    private lateinit var continueButton: MaterialButton

    private var attemptId: String? = null
    private val attemptRepository = QuizAttemptRepository()
    private lateinit var leaderboardAdapter: LeaderboardAdapter

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
        
        // Display score from intent immediately
        displayScoreFromIntent()
        
        observeViewModel()

        // Load rewards data from backend
        attemptId?.let { viewModel.loadRewards(it) }

        // Also load attempt details and quiz leaderboard
        attemptId?.let { loadAttemptAndLeaderboard(it) }
    }
    
    private fun displayScoreFromIntent() {
        val score = intent.getIntExtra("SCORE", 0)
        val maxScore = intent.getIntExtra("MAX_SCORE", 100)
        
        val percentage = if (maxScore > 0) {
            (score.toDouble() / maxScore) * 100
        } else 0.0

        // Update performance tier immediately
        val tier = PerformanceTier.fromPercentage(percentage)
        tierEmoji.text = tier.emoji
        performanceMessage.text = tier.message
        scorePercentage.text = "${String.format("%.0f", percentage)}%"
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

        // Leaderboard recycler
        leaderboardAdapter = LeaderboardAdapter()
        leaderboardRecycler = findViewById(R.id.leaderboardRecycler)
        leaderboardRecycler.apply {
            layoutManager = LinearLayoutManager(this@QuizRewardsActivity)
            adapter = leaderboardAdapter
        }
    }

    private fun setupButtons() {
        continueButton.setOnClickListener {
            // Navigate back to main screen
            finish()
        }

        // small icon-based share button inside leaderboard card
        shareIconButton = findViewById(R.id.shareIconButton)
        shareIconButton.setOnClickListener {
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
                animateXpGain(it.xp_details)
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

        // Observe attempt LiveData - if set, we can use quizId to fetch leaderboard
        viewModel.attempt.observe(this) { attempt ->
            attempt?.let {
                // attempt includes quizId
                loadLeaderboardForQuiz(it.quizId)
            }
        }
    }

    private fun loadAttemptAndLeaderboard(attemptId: String) {
        // Fetch attempt details to obtain quizId, then fetch leaderboard
        lifecycleScope.launch {
            val result = attemptRepository.getAttemptById(attemptId)
            result.onSuccess { attempt ->
                // set attempt in viewModel so other observers pick it up
                viewModel.setAttempt(attempt)
                loadLeaderboardForQuiz(attempt.quizId)
            }.onFailure { error ->
                // Could be local attempt - just show toast and skip leaderboard
                Toast.makeText(this@QuizRewardsActivity, "Leaderboard unavailable: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadLeaderboardForQuiz(quizId: String) {
        lifecycleScope.launch {
            val result = attemptRepository.getQuizLeaderboard(quizId)
            result.onSuccess { leaderboardList ->
                if (leaderboardList.isEmpty()) {
                    findViewById<TextView>(R.id.leaderboardEmptyText).visibility = View.VISIBLE
                    findViewById<com.google.android.material.card.MaterialCardView>(R.id.quizLeaderboardCard).visibility = View.GONE
                    return@onSuccess
                }

                // Show card
                leaderboardCard = findViewById(R.id.quizLeaderboardCard)
                leaderboardCard.visibility = View.VISIBLE

                // Update podium placeholders for top 3
                firstPlaceText = findViewById(R.id.firstPlaceText)
                secondPlaceText = findViewById(R.id.secondPlaceText)
                thirdPlaceText = findViewById(R.id.thirdPlaceText)

                if (leaderboardList.size > 0) firstPlaceText.text = "🥇 ${leaderboardList[0].studentName} — ${leaderboardList[0].percentage.toInt()}%"
                if (leaderboardList.size > 1) secondPlaceText.text = "🥈 ${leaderboardList[1].studentName} — ${leaderboardList[1].percentage.toInt()}%"
                if (leaderboardList.size > 2) thirdPlaceText.text = "🥉 ${leaderboardList[2].studentName} — ${leaderboardList[2].percentage.toInt()}%"

                // For 1-3 entries, show podium only; for >3, show podium + list
                val listForAdapter = if (leaderboardList.size > 3) leaderboardList.drop(3) else emptyList()
                leaderboardAdapter.submitList(listForAdapter)
                leaderboardRecycler.visibility = if (listForAdapter.isNotEmpty()) View.VISIBLE else View.GONE

                // Animate appearance
                val anim = AnimationUtils.loadAnimation(this@QuizRewardsActivity, android.R.anim.fade_in)
                leaderboardCard.startAnimation(anim)
                leaderboardRecycler.startAnimation(AnimationUtils.loadAnimation(this@QuizRewardsActivity, android.R.anim.slide_in_left))
            }.onFailure { error ->
                // Show empty state
                findViewById<TextView>(R.id.leaderboardEmptyText).visibility = View.VISIBLE
                Toast.makeText(this@QuizRewardsActivity, "Failed to load leaderboard: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animateXpGain(xpDetails: com.example.quizmaster.data.remote.XpDetails) {
        lifecycleScope.launch {
            val totalXp = xpDetails.total_xp
            
            // Animate XP value counting up
            var currentXp = 0
            val increment = if (totalXp > 100) 5 else 1
            val delayMs = if (totalXp > 100) 10L else 20L

            while (currentXp < totalXp) {
                currentXp = minOf(currentXp + increment, totalXp)
                xpEarnedValue.text = "+$currentXp XP"
                delay(delayMs)
            }

            // Show XP breakdown from backend
            delay(300)
            baseXpValue.text = "+${xpDetails.base_xp} XP"

            // Show speed bonus if earned
            if (xpDetails.speed_bonus > 0) {
                delay(200)
                speedBonusRow.visibility = View.VISIBLE
                speedBonusValue.text = "+${xpDetails.speed_bonus} XP"
            }

            // Show perfect score bonus if applicable
            if (xpDetails.accuracy_bonus > 0) {
                delay(200)
                perfectScoreRow.visibility = View.VISIBLE
                val accuracyBonusValue = findViewById<TextView>(R.id.perfectScoreValue)
                accuracyBonusValue?.text = "+${xpDetails.accuracy_bonus} XP"
            }

            // Show level up animation
            if (xpDetails.leveled_up) {
                delay(500)
                showLevelUp(xpDetails.new_level)
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
        val percentage = if (attempt.maxScore > 0) (attempt.totalScore / attempt.maxScore) * 100 else 0.0

        val xpGain = viewModel.xpGain.value?.xp_earned ?: 0
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
