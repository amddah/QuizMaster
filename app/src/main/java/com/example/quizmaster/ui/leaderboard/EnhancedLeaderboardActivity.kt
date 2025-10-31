package com.example.quizmaster.ui.leaderboard

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.remote.GlobalRankResponse
import com.example.quizmaster.data.remote.LeaderboardRankEntry
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * Enhanced leaderboard activity with global rankings and user position
 */
class EnhancedLeaderboardActivity : AppCompatActivity() {

    private lateinit var viewModel: EnhancedLeaderboardViewModel
    private lateinit var leaderboardAdapter: GlobalLeaderboardAdapter

    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var myRankNumber: TextView
    private lateinit var myXp: TextView
    private lateinit var myPercentile: TextView
    private lateinit var betterThanText: TextView
    private lateinit var leaderboardTabs: TabLayout
    private lateinit var podiumLayout: View
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var refreshFab: FloatingActionButton

    // Podium views
    private lateinit var firstPlaceMedal: TextView
    private lateinit var firstPlaceName: TextView
    private lateinit var firstPlaceXp: TextView
    private lateinit var secondPlaceMedal: TextView
    private lateinit var secondPlaceName: TextView
    private lateinit var secondPlaceXp: TextView
    private lateinit var thirdPlaceMedal: TextView
    private lateinit var thirdPlaceName: TextView
    private lateinit var thirdPlaceXp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enhanced_leaderboard)

        viewModel = ViewModelProvider(this)[EnhancedLeaderboardViewModel::class.java]

        initializeViews()
        setupToolbar()
        setupTabs()
        setupRecyclerView()
        observeViewModel()

        // Load initial data
        viewModel.loadGlobalLeaderboard()

        // Setup refresh
        refreshFab.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        myRankNumber = findViewById(R.id.myRankNumber)
        myXp = findViewById(R.id.myXp)
        myPercentile = findViewById(R.id.myPercentile)
        betterThanText = findViewById(R.id.betterThanText)
        leaderboardTabs = findViewById(R.id.leaderboardTabs)
        podiumLayout = findViewById(R.id.podiumLayout)
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)
        loadingProgress = findViewById(R.id.loadingProgress)
        refreshFab = findViewById(R.id.refreshFab)

        // Podium views
        val firstPlace = findViewById<View>(R.id.firstPlace)
        firstPlaceMedal = firstPlace.findViewById(R.id.podiumMedal)
        firstPlaceName = firstPlace.findViewById(R.id.podiumName)
        firstPlaceXp = firstPlace.findViewById(R.id.podiumXp)

        val secondPlace = findViewById<View>(R.id.secondPlace)
        secondPlaceMedal = secondPlace.findViewById(R.id.podiumMedal)
        secondPlaceName = secondPlace.findViewById(R.id.podiumName)
        secondPlaceXp = secondPlace.findViewById(R.id.podiumXp)

        val thirdPlace = findViewById<View>(R.id.thirdPlace)
        thirdPlaceMedal = thirdPlace.findViewById(R.id.podiumMedal)
        thirdPlaceName = thirdPlace.findViewById(R.id.podiumName)
        thirdPlaceXp = thirdPlace.findViewById(R.id.podiumXp)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupTabs() {
        leaderboardTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadGlobalLeaderboard()
                    1 -> {
                        // TODO: Show quiz selection for quiz-specific leaderboard
                        Toast.makeText(
                            this@EnhancedLeaderboardActivity,
                            "Select a quiz to view its leaderboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = GlobalLeaderboardAdapter()
        leaderboardRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EnhancedLeaderboardActivity)
            adapter = leaderboardAdapter
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe my rank
        viewModel.myRank.observe(this) { rank ->
            rank?.let { updateMyRankCard(it) }
        }

        // Observe leaderboard
        viewModel.leaderboard.observe(this) { leaderboard ->
            if (leaderboard.isEmpty()) {
                emptyStateText.visibility = View.VISIBLE
                leaderboardRecyclerView.visibility = View.GONE
                podiumLayout.visibility = View.GONE
            } else {
                emptyStateText.visibility = View.GONE
                leaderboardRecyclerView.visibility = View.VISIBLE
                podiumLayout.visibility = View.VISIBLE

                // Update podium
                updatePodium(leaderboard)

                // Update list (skip top 3)
                leaderboardAdapter.submitList(leaderboard.drop(3))
            }
        }

        // Observe errors
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateMyRankCard(globalRank: GlobalRankResponse) {
        myRankNumber.text = "#${globalRank.rank}"
        myXp.text = "${globalRank.total_xp} XP"
        myPercentile.text = "Top ${String.format("%.1f", globalRank.percentile)}%"
        betterThanText.text = "Better than ${globalRank.better_than} ${if (globalRank.better_than == 1) "student" else "students"}"
    }

    private fun updatePodium(leaderboard: List<LeaderboardRankEntry>) {
        // First place
        if (leaderboard.isNotEmpty()) {
            val first = leaderboard[0]
            firstPlaceMedal.text = "🥇"
            firstPlaceName.text = first.user_name
            firstPlaceXp.text = "${first.total_xp} XP"
        }

        // Second place
        if (leaderboard.size > 1) {
            val second = leaderboard[1]
            secondPlaceMedal.text = "🥈"
            secondPlaceName.text = second.user_name
            secondPlaceXp.text = "${second.total_xp} XP"
        }

        // Third place
        if (leaderboard.size > 2) {
            val third = leaderboard[2]
            thirdPlaceMedal.text = "🥉"
            thirdPlaceName.text = third.user_name
            thirdPlaceXp.text = "${third.total_xp} XP"
        }
    }
}
