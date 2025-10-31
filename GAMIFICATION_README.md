# 🎮 QuizMaster Gamification System

A complete gamification system for the QuizMaster Android application featuring experience points, badges, achievements, leaderboards, and performance tracking.

## ✨ Features

### 🌟 Experience & Leveling
- **Dynamic XP System**: Earn experience points for every correct answer
- **Speed Bonuses**: Get bonus XP for fast responses
  - ⚡ Fast (< 3s): +5 XP
  - 🏃 Medium (3-5s): +3 XP
- **Perfect Score Bonus**: 💯 +20 XP for 100% completion
- **Progressive Leveling**: 100 XP per level with visual progress tracking

### 🏆 Badges & Achievements
**10 Unique Badges to Collect:**

| Badge | Description | Rarity |
|-------|-------------|--------|
| 🎓 First Steps | Complete your first quiz | Common |
| ⚡ Speed Demon | Answer 10 questions in under 3 seconds | Rare |
| 💯 Perfect Score | Achieve 100% on any quiz | Uncommon |
| 🔥 5-Day Streak | Maintain 5-day quiz streak | Uncommon |
| 🔥🔥 10-Day Streak | Maintain 10-day quiz streak | Rare |
| 👑 Quiz Master | Complete 50 quizzes | Epic |
| 🌟 Category Expert | Get 90%+ on 5 quizzes in same category | Rare |
| 🥉 Level 10 | Reach level 10 | Uncommon |
| 🥈 Level 25 | Reach level 25 | Rare |
| 🥇 Level 50 | Reach level 50 | Legendary |

### 📊 Performance Tiers
Immediate feedback on quiz performance:

- 🏆 **Excellent** (90-100%): "Outstanding performance!"
- ⭐ **Great** (75-89%): "Excellent work!"
- 👍 **Good** (60-74%): "Good job!"
- 😊 **Average** (50-59%): "Not bad!"
- 💪 **Needs Improvement** (0-49%): "Keep practicing!"

### 🏅 Leaderboards
- **Global Leaderboard**: Compete with all students
- **Top 3 Podium**: Special display for top performers (🥇🥈🥉)
- **Personal Rank**: See your position and percentile
- **Comparison Stats**: "Better than X students"
- **Detailed Metrics**: XP, level, badges, and average scores

### 📈 Statistics Dashboard
Track your progress with:
- Total quizzes completed
- Questions answered
- Accuracy percentage
- Average score
- Global rank
- Badges earned
- Current & longest streaks
- Category-specific performance

### 🎁 Rewards Flow
After each quiz:
- Animated XP gain counter
- XP breakdown showing bonuses
- Level up celebration (if applicable)
- New badge unlock animations
- Performance tier feedback
- Share results functionality

### 🔔 Smart Notifications
- Badge unlock notifications
- Level up celebrations
- Streak reminders
- Multiple achievement notifications
- Custom milestone alerts

## 📱 Screenshots

### Profile Screen
- User info with avatar/initials
- XP bar with level progress
- Comprehensive statistics
- Streak tracking
- Badge collection grid
- Category performance breakdown

### Leaderboard
- Personal rank card with statistics
- Top 3 podium display
- Scrollable rankings list
- Filter by global or quiz-specific
- Real-time updates

### Rewards Screen
- Performance tier display
- Animated XP counter
- XP breakdown with bonuses
- Level up notification
- New badges showcase
- Social sharing

## 🛠️ Technical Implementation

### Architecture
```
MVVM Pattern
├── Models (Gamification.kt)
├── API Services (GamificationApiService.kt)
├── Repository (GamificationRepository.kt)
├── ViewModels (ProfileViewModel, LeaderboardViewModel, etc.)
└── Views (Activities, Layouts, Adapters)
```

### Key Components

#### Models
- `BadgeType`: Enum with all badge types
- `PerformanceTier`: Score-based performance categories
- `XpConstants`: XP calculation utilities
- `StudentStats`: User statistics model
- `Achievement`: Badge/achievement model

#### API Services
- `GamificationApiService`: RESTful endpoints
- `XpGainResponse`: XP breakdown
- `GlobalRankResponse`: Rank and percentile data
- `LeaderboardRankEntry`: Leaderboard entries

#### UI Components
- Reusable XP bar component
- Performance tier card
- Streak tracking card
- Statistics card
- Badge grid display
- Leaderboard podium
- Category performance list

#### Utilities
- `XpConstants`: XP calculations
- `GamificationNotificationHelper`: Push notifications

## 🚀 Getting Started

### Prerequisites
```kotlin
// Dependencies required (add to build.gradle.kts)
implementation("com.google.android.material:material:1.10.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### Integration

1. **Add Activities to Manifest**
```xml
<activity android:name=".ui.profile.GamificationProfileActivity" />
<activity android:name=".ui.leaderboard.EnhancedLeaderboardActivity" />
<activity android:name=".ui.quiz.QuizRewardsActivity" />
```

2. **Request Notification Permission** (Android 13+)
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

3. **Navigate from Quiz Completion**
```kotlin
val intent = Intent(this, QuizRewardsActivity::class.java).apply {
    putExtra("ATTEMPT_ID", attemptId)
}
startActivity(intent)
```

4. **Add Profile Menu Item**
```kotlin
profileMenuItem.setOnClickListener {
    startActivity(Intent(this, GamificationProfileActivity::class.java))
}
```

5. **Add Leaderboard Menu Item**
```kotlin
leaderboardMenuItem.setOnClickListener {
    startActivity(Intent(this, EnhancedLeaderboardActivity::class.java))
}
```

## 📡 Backend API Requirements

The system requires these endpoints:

```
GET  /api/v1/users/stats              - User statistics
GET  /api/v1/users/achievements       - User achievements
GET  /api/v1/attempts/{id}/xp         - XP breakdown for attempt
GET  /api/v1/leaderboards/global      - Global leaderboard
GET  /api/v1/users/global-rank        - User's global rank
GET  /api/v1/users/category-stats     - Category performance
GET  /api/v1/users/streak             - Streak information
```

See `GAMIFICATION_GUIDE.md` for detailed API specifications.

## 🎨 Customization

### Modify XP Values
Edit `XpConstants` in `Gamification.kt`:
```kotlin
object XpConstants {
    const val BASE_XP_PER_CORRECT = 10
    const val FAST_RESPONSE_BONUS = 5
    const val MEDIUM_RESPONSE_BONUS = 3
    const val PERFECT_SCORE_BONUS = 20
    const val XP_PER_LEVEL = 100
}
```

### Add New Badges
1. Add to `BadgeType` enum
2. Implement display name, emoji, description, and rarity
3. Update backend to check criteria and award badge

### Customize Performance Tiers
Modify `PerformanceTier` enum thresholds and messages.

### Theme Colors
Update colors in `colors.xml` and component styles.

## 📊 Analytics Integration

Track user engagement:
```kotlin
// Track badge unlock
analytics.logEvent("badge_unlocked", Bundle().apply {
    putString("badge_type", badgeType.name)
})

// Track level up
analytics.logEvent("level_up", Bundle().apply {
    putInt("new_level", level)
})

// Track streak milestone
analytics.logEvent("streak_milestone", Bundle().apply {
    putInt("streak_days", days)
})
```

## 🧪 Testing

Run comprehensive tests:
- XP calculation accuracy
- Badge unlock conditions
- Leaderboard ranking logic
- Notification delivery
- Animation smoothness
- Data persistence
- Error handling

## 📄 Documentation

- **GAMIFICATION_GUIDE.md**: Complete implementation guide
- **API Documentation**: Swagger spec included
- **Code Comments**: Inline documentation throughout

## 🔮 Future Enhancements

Potential additions:
- Daily challenges with bonus XP
- Multiplayer quiz battles
- Social features (friends, sharing)
- Seasonal events
- Custom avatars
- Power-ups and boosters
- Guild/team system
- Achievement showcase
- XP multipliers by difficulty

## 🤝 Contributing

To extend the gamification system:

1. Add new models in `Gamification.kt`
2. Create corresponding API endpoints
3. Update repository methods
4. Build UI components
5. Implement ViewModels
6. Add Activities/Fragments
7. Update documentation

## 📝 License

Part of the QuizMaster application.

## 👏 Credits

Gamification system designed and implemented for QuizMaster.

---

**Version**: 1.0  
**Last Updated**: October 30, 2025  
**Status**: ✅ Production Ready

For detailed implementation guide, see `GAMIFICATION_GUIDE.md`
