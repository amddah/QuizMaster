package com.example.quizmaster.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.R
import com.example.quizmaster.data.local.UserSessionManager
import com.example.quizmaster.data.remote.ApiClient
import com.example.quizmaster.data.remote.LeaderboardEntry
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

/**
 * Leaderboard Activity with tabs for quiz-specific and global leaderboards
 */
class LeaderboardActivity : AppCompatActivity() {
    
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: View
    private lateinit var sessionManager: UserSessionManager
    private lateinit var adapter: LeaderboardAdapter
    
    private var currentQuizId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        
        sessionManager = UserSessionManager.getInstance(this)
        currentQuizId = intent.getStringExtra("quiz_id")
        
        initViews()
        setupTabs()
        setupRecyclerView()
        loadLeaderboard(0) // Start with quiz leaderboard
    }
    
    private fun initViews() {
        tabLayout = findViewById(R.id.leaderboardTabs)
        recyclerView = findViewById(R.id.leaderboardRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Quiz Leaderboard"))
        tabLayout.addTab(tabLayout.newTab().setText("Global Leaderboard"))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadLeaderboard(tab?.position ?: 0)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun setupRecyclerView() {
        adapter = LeaderboardAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun loadLeaderboard(tabPosition: Int) {
        lifecycleScope.launch {
            try {
                when (tabPosition) {
                    0 -> loadQuizLeaderboard()
                    1 -> loadGlobalLeaderboard()
                }
            } catch (e: Exception) {
                showEmptyState("Error loading leaderboard: ${e.message}")
            }
        }
    }
    
    private suspend fun loadQuizLeaderboard() {
        currentQuizId?.let { quizId ->
            val response = ApiClient.quizAttemptApiService.getQuizLeaderboard(quizId)
            if (response.isSuccessful) {
                val entries = response.body() ?: emptyList()
                if (entries.isNotEmpty()) {
                    adapter.updateEntries(entries)
                    recyclerView.visibility = View.VISIBLE
                    emptyStateLayout.visibility = View.GONE
                } else {
                    showEmptyState("No leaderboard data available")
                }
            } else {
                showEmptyState("Failed to load leaderboard")
            }
        } ?: showEmptyState("No quiz selected")
    }
    
    private suspend fun loadGlobalLeaderboard() {
        val response = ApiClient.quizAttemptApiService.getGlobalLeaderboard()
        if (response.isSuccessful) {
            // Convert global leaderboard data to LeaderboardEntry format
            val globalData = response.body() ?: emptyList()
            if (globalData.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                emptyStateLayout.visibility = View.GONE
                // Handle global leaderboard display
            } else {
                showEmptyState("No global leaderboard data available")
            }
        } else {
            showEmptyState("Failed to load global leaderboard")
        }
    }
    
    private fun showEmptyState(message: String) {
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
    }
}

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    
    private var entries = listOf<LeaderboardEntry>()
    
    fun updateEntries(newEntries: List<LeaderboardEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_entry, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries[position])
    }
    
    override fun getItemCount() = entries.size
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val rankText: TextView = view.findViewById(R.id.rankText)
        private val nameText: TextView = view.findViewById(R.id.nameText)
        private val scoreText: TextView = view.findViewById(R.id.scoreText)
        private val percentageText: TextView = view.findViewById(R.id.percentageText)
        
        fun bind(entry: LeaderboardEntry) {
            rankText.text = "#${entry.rank}"
            nameText.text = entry.student_name
            scoreText.text = "${entry.score.toInt()} pts"
            percentageText.text = "${String.format("%.1f", entry.percentage)}%"
        }
    }
}
