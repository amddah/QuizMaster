package com.example.quizmaster.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.model.StudentStats
import com.example.quizmaster.data.model.XpConstants
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

/**
 * Activity displaying gamification profile with stats, badges, and achievements
 */
class GamificationProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: GamificationProfileViewModel
    private lateinit var badgesAdapter: BadgesAdapter
    private lateinit var categoryStatsAdapter: CategoryStatsAdapter

    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userInitials: TextView
    private lateinit var loadingProgress: ProgressBar
    
    // XP Bar
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var xpProgressBar: ProgressBar
    private lateinit var nextLevelText: TextView
    
    // Stats
    private lateinit var quizzesCompletedValue: TextView
    private lateinit var totalQuestionsValue: TextView
    private lateinit var accuracyValue: TextView
    private lateinit var averageScoreValue: TextView
    private lateinit var globalRankValue: TextView
    private lateinit var badgesEarnedValue: TextView
    
    // Streak
    private lateinit var streakCount: TextView
    private lateinit var longestStreak: TextView
    private lateinit var streakStatus: TextView
    
    // Badges
    private lateinit var badgesProgressText: TextView
    private lateinit var badgesRecyclerView: RecyclerView
    private lateinit var noBadgesText: TextView
    
    // Category Stats
    private lateinit var categoryStatsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification_profile)

        viewModel = ViewModelProvider(this)[GamificationProfileViewModel::class.java]

        initializeViews()
        setupToolbar()
        setupRecyclerViews()
        observeViewModel()
        
        // Load data
        viewModel.loadUserProfile()
    }

    private fun initializeViews() {
        // Toolbar
        toolbar = findViewById(R.id.toolbar)
        
        // User info
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userInitials = findViewById(R.id.userInitials)
        
        // Loading
        loadingProgress = findViewById(R.id.loadingProgress)
        
        // XP Bar (from included layout)
        val xpBarComponent = findViewById<View>(R.id.xpBarComponent)
        levelText = xpBarComponent.findViewById(R.id.levelText)
        xpText = xpBarComponent.findViewById(R.id.xpText)
        xpProgressBar = xpBarComponent.findViewById(R.id.xpProgressBar)
        nextLevelText = xpBarComponent.findViewById(R.id.nextLevelText)
        
        // Stats (from included layout)
        val statsCardComponent = findViewById<View>(R.id.statsCardComponent)
        quizzesCompletedValue = statsCardComponent.findViewById(R.id.quizzesCompletedValue)
        totalQuestionsValue = statsCardComponent.findViewById(R.id.totalQuestionsValue)
        accuracyValue = statsCardComponent.findViewById(R.id.accuracyValue)
        averageScoreValue = statsCardComponent.findViewById(R.id.averageScoreValue)
        globalRankValue = statsCardComponent.findViewById(R.id.globalRankValue)
        badgesEarnedValue = statsCardComponent.findViewById(R.id.badgesEarnedValue)
        
        // Streak (from included layout)
        val streakCardComponent = findViewById<View>(R.id.streakCardComponent)
        streakCount = streakCardComponent.findViewById(R.id.streakCount)
        longestStreak = streakCardComponent.findViewById(R.id.longestStreak)
        streakStatus = streakCardComponent.findViewById(R.id.streakStatus)
        
        // Badges
        badgesProgressText = findViewById(R.id.badgesProgressText)
        badgesRecyclerView = findViewById(R.id.badgesRecyclerView)
        noBadgesText = findViewById(R.id.noBadgesText)
        
        // Category Stats
        categoryStatsRecyclerView = findViewById(R.id.categoryStatsRecyclerView)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        // Badges RecyclerView
        badgesAdapter = BadgesAdapter()
        badgesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@GamificationProfileActivity, 3)
            adapter = badgesAdapter
        }
        
        // Category Stats RecyclerView
        categoryStatsAdapter = CategoryStatsAdapter()
        categoryStatsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GamificationProfileActivity)
            adapter = categoryStatsAdapter
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe user info
        viewModel.user.observe(this) { user ->
            user?.let {
                userName.text = "${it.firstName} ${it.lastName}"
                userEmail.text = it.email
                userInitials.text = "${it.firstName.first()}${it.lastName.first()}".uppercase()
            }
        }
        
        // Observe stats
        viewModel.stats.observe(this) { stats ->
            stats?.let { updateUI(it) }
        }
        
        // Observe achievements
        viewModel.achievements.observe(this) { achievements ->
            badgesAdapter.submitList(achievements)
            badgesProgressText.text = "${achievements.size} / ${viewModel.getTotalBadgesCount()}"
            noBadgesText.visibility = if (achievements.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // Observe streak info
        viewModel.streakInfo.observe(this) { streak ->
            streak?.let {
                streakCount.text = "${it.current_streak} ${if (it.current_streak == 1) "day" else "days"}"
                longestStreak.text = "${it.longest_streak} ${if (it.longest_streak == 1) "day" else "days"}"
                
                streakStatus.text = when {
                    it.current_streak == 0 -> "Complete a quiz today to start your streak!"
                    it.streak_active -> "Don't break your streak! Complete a quiz today."
                    else -> "Your streak is about to break! Complete a quiz today."
                }
            }
        }
        
        // Observe category stats
        viewModel.categoryStats.observe(this) { categoryStats ->
            categoryStatsAdapter.submitList(categoryStats)
        }
        
        // Observe errors
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(stats: StudentStats) {
        // Update XP Bar
        levelText.text = "Level ${stats.currentLevel}"
        val xpForCurrentLevel = (stats.currentLevel - 1) * XpConstants.XP_PER_LEVEL
        val xpInCurrentLevel = stats.totalExperiencePoints - xpForCurrentLevel
        xpText.text = "$xpInCurrentLevel / ${XpConstants.XP_PER_LEVEL} XP"
        xpProgressBar.progress = stats.getProgressToNextLevel()
        nextLevelText.text = "${stats.getXpNeededForNextLevel()} XP to Level ${stats.currentLevel + 1}"
        
        // Update Stats
        quizzesCompletedValue.text = stats.totalQuizzesCompleted.toString()
        totalQuestionsValue.text = stats.totalQuestionsAnswered.toString()
        accuracyValue.text = "${String.format("%.1f", stats.getAccuracyPercentage())}%"
        averageScoreValue.text = "${String.format("%.1f", stats.averageScorePercentage)}%"
        globalRankValue.text = stats.globalRank?.let { "#$it" } ?: "#-"
        badgesEarnedValue.text = stats.getTotalBadges().toString()
    }
}
