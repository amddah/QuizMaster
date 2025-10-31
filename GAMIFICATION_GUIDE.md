# QuizMaster Gamification System - Implementation Guide

## Overview
This document provides a comprehensive guide for the gamification system implemented in the QuizMaster Android application.

## Features Implemented

### 1. Experience & Leveling System
- **Base XP**: 10 XP per correct answer
- **Speed Bonuses**:
  - Fast response (< 3 seconds): +5 XP
  - Medium response (3-5 seconds): +3 XP
- **Perfect Score Bonus**: +20 XP for 100% completion
- **Level Progression**: 100 XP per level

### 2. Badges & Achievements
The following badges are implemented:

| Badge | Emoji | Requirement | Rarity |
|-------|-------|-------------|--------|
| First Steps | 🎓 | Complete your first quiz | Common |
| Speed Demon | ⚡ | Answer 10 questions in under 3 seconds each | Rare |
| Perfect Score | 💯 | Get 100% on any quiz | Uncommon |
| 5-Day Streak | 🔥 | Complete quizzes for 5 days in a row | Uncommon |
| 10-Day Streak | 🔥🔥 | Complete quizzes for 10 days in a row | Rare |
| Quiz Master | 👑 | Complete 50 quizzes | Epic |
| Category Expert | 🌟 | Get 90%+ on 5 quizzes in same category | Rare |
| Level 10 | 🥉 | Reach level 10 | Uncommon |
| Level 25 | 🥈 | Reach level 25 | Rare |
| Level 50 | 🥇 | Reach level 50 | Legendary |

### 3. Performance Tiers
Results are categorized into performance tiers:

| Tier | Range | Emoji | Message |
|------|-------|-------|---------|
| Excellent | 90-100% | 🏆 | "Outstanding performance!" |
| Great | 75-89% | ⭐ | "Excellent work!" |
| Good | 60-74% | 👍 | "Good job!" |
| Average | 50-59% | 😊 | "Not bad!" |
| Needs Improvement | 0-49% | 💪 | "Keep practicing!" |

### 4. Leaderboard & Competition
- **Global Leaderboard**: Top 50 performers across all quizzes
- **Quiz-Specific Leaderboards**: Rankings for individual quizzes
- **Real-time Rankings**: Live updates of user positions
- **Statistics**:
  - Your rank
  - Percentile
  - "Better than X students" comparison
  - Top 3 podium display

## File Structure

```
app/src/main/java/com/example/quizmaster/
├── data/
│   ├── model/
│   │   └── Gamification.kt              # Models, enums, XP calculations
│   └── remote/
│       └── GamificationApiService.kt    # API endpoints
├── repository/
│   └── GamificationRepository.kt        # Data layer
├── ui/
│   ├── profile/
│   │   ├── GamificationProfileActivity.kt
│   │   ├── GamificationProfileViewModel.kt
│   │   └── GamificationAdapters.kt
│   ├── leaderboard/
│   │   ├── EnhancedLeaderboardActivity.kt
│   │   ├── EnhancedLeaderboardViewModel.kt
│   │   └── GlobalLeaderboardAdapter.kt
│   └── quiz/
│       ├── QuizRewardsActivity.kt
│       └── QuizRewardsViewModel.kt
└── utils/
    └── GamificationNotificationHelper.kt

app/src/main/res/layout/
├── activity_gamification_profile.xml
├── activity_enhanced_leaderboard.xml
├── activity_quiz_rewards.xml
├── component_xp_bar.xml
├── component_performance_tier.xml
├── component_streak_card.xml
├── component_stats_card.xml
├── component_podium_item.xml
├── item_badge.xml
├── item_global_leaderboard.xml
└── item_category_stat.xml
```

## Integration Steps

### 1. API Integration

The backend needs to implement these endpoints:

```
GET    /api/v1/users/stats              # Get user statistics
GET    /api/v1/users/achievements       # Get user achievements
GET    /api/v1/attempts/{id}/xp         # Get XP breakdown
GET    /api/v1/leaderboards/global      # Global leaderboard
GET    /api/v1/users/global-rank        # User's rank
GET    /api/v1/users/category-stats     # Category performance
GET    /api/v1/users/streak             # Streak information
```

### 2. AndroidManifest.xml

Add the new activities:

```xml
<activity
    android:name=".ui.profile.GamificationProfileActivity"
    android:label="Profile"
    android:theme="@style/Theme.QuizMaster" />

<activity
    android:name=".ui.leaderboard.EnhancedLeaderboardActivity"
    android:label="Leaderboard"
    android:theme="@style/Theme.QuizMaster" />

<activity
    android:name=".ui.quiz.QuizRewardsActivity"
    android:label="Quiz Rewards"
    android:theme="@style/Theme.QuizMaster" />
```

Add notification permission (Android 13+):

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### 3. Navigation Integration

After quiz completion, navigate to rewards:

```kotlin
// In ResultsActivity or similar
val intent = Intent(this, QuizRewardsActivity::class.java).apply {
    putExtra("ATTEMPT_ID", attemptId)
}
startActivity(intent)
```

From main menu, navigate to profile:

```kotlin
val intent = Intent(this, GamificationProfileActivity::class.java)
startActivity(intent)
```

From main menu, navigate to leaderboard:

```kotlin
val intent = Intent(this, EnhancedLeaderboardActivity::class.java)
startActivity(intent)
```

### 4. Dependencies

Ensure these dependencies are in your `build.gradle.kts`:

```kotlin
dependencies {
    // Material Design
    implementation("com.google.android.material:material:1.10.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Coordinator Layout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
}
```

## Usage Examples

### Calculate XP for an Answer

```kotlin
val isCorrect = true
val timeToAnswer = 2 // seconds

val xp = XpConstants.calculateAnswerXp(isCorrect, timeToAnswer)
// Returns: 15 XP (10 base + 5 fast bonus)
```

### Determine Performance Tier

```kotlin
val scorePercentage = 87.5
val tier = PerformanceTier.fromPercentage(scorePercentage)
// Returns: PerformanceTier.GREAT

println("${tier.emoji} ${tier.message}")
// Output: "⭐ Excellent work!"
```

### Show Achievement Notification

```kotlin
val notificationHelper = GamificationNotificationHelper(context)

// Single badge
notificationHelper.showBadgeUnlockedNotification(achievement)

// Level up
notificationHelper.showLevelUpNotification(newLevel = 15, xpGained = 125)

// Streak reminder
notificationHelper.showStreakReminderNotification(currentStreak = 7)

// Multiple badges
notificationHelper.showMultipleBadgesNotification(
    badgeCount = 3,
    achievements = listOf(achievement1, achievement2, achievement3)
)
```

## UI/UX Features

### Animations
- XP counter animates from 0 to earned value
- Level up card fades in with celebration
- Badges slide in when unlocked
- Smooth transitions between screens

### Visual Hierarchy
- Top 3 leaderboard positions displayed as podium
- Badge rarity indicated by colored bars
- Performance tiers use emoji and color coding
- Progress bars for XP and level advancement

### User Feedback
- Real-time XP breakdown showing bonuses
- "Better than X students" comparison
- Streak status with motivational messages
- Category performance with expert badges

## Testing Checklist

- [ ] User can view profile with all statistics
- [ ] XP bar displays correctly with progress
- [ ] Badges are displayed in grid layout
- [ ] Leaderboard shows top performers
- [ ] User's rank is displayed accurately
- [ ] Rewards screen shows after quiz completion
- [ ] XP animation plays smoothly
- [ ] Level up notification appears
- [ ] New badges are highlighted
- [ ] Notifications are received for achievements
- [ ] Streak tracking works correctly
- [ ] Category stats display properly
- [ ] Performance tiers show correct emoji/message
- [ ] Share functionality works

## Backend Requirements

The backend should:

1. **Calculate XP** based on:
   - Correct answers
   - Response time
   - Perfect score bonus

2. **Track Achievements** by:
   - Monitoring quiz completions
   - Checking badge criteria
   - Storing earned badges with timestamps

3. **Maintain Leaderboards** with:
   - Global rankings by total XP
   - Quiz-specific rankings by score
   - Real-time updates

4. **Track Streaks** by:
   - Recording daily quiz completions
   - Calculating consecutive days
   - Identifying streak breaks

5. **Provide Statistics** including:
   - Total quizzes completed
   - Questions answered
   - Accuracy percentage
   - Average scores
   - Category performance

## Future Enhancements

Consider adding:
- Daily challenges with bonus XP
- Multiplayer quiz competitions
- Social features (follow friends, compare stats)
- Seasonal events and limited-time badges
- Custom avatar system unlocked by levels
- Power-ups earned through achievements
- Guild/team system for collaborative learning
- Achievement showcase on profile
- XP multipliers for difficulty levels
- Weekly/monthly leaderboards with prizes

## Support

For issues or questions about the gamification system, refer to:
- `Gamification.kt` for model definitions
- `GamificationRepository.kt` for data operations
- `GamificationNotificationHelper.kt` for notification logic
- Individual Activity/ViewModel files for screen-specific behavior

---

**Last Updated**: October 30, 2025
**Version**: 1.0
