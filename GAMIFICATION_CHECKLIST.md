# 🚀 Gamification System - Quick Start Checklist

## ✅ Implementation Complete!

All gamification features have been implemented. Follow this checklist to integrate them into your app.

## 📋 Integration Checklist

### 1. Backend Setup (Required)
- [ ] Implement the following API endpoints:
  ```
  GET  /api/v1/users/stats
  GET  /api/v1/users/achievements
  GET  /api/v1/attempts/{id}/xp
  GET  /api/v1/leaderboards/global
  GET  /api/v1/users/global-rank
  GET  /api/v1/users/category-stats
  GET  /api/v1/users/streak
  ```
- [ ] Calculate XP based on correct answers and time
- [ ] Track and award badges based on achievements
- [ ] Maintain global and quiz-specific leaderboards
- [ ] Track daily quiz completion streaks
- [ ] Store and retrieve user statistics

### 2. AndroidManifest.xml Updates
Add these activities:

```xml
<activity
    android:name=".ui.profile.GamificationProfileActivity"
    android:label="My Profile"
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

Add notification permission (for Android 13+):

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### 3. Navigation Integration

#### A. After Quiz Completion
In your `ResultsActivity` or quiz completion handler:

```kotlin
// After quiz is completed
val intent = Intent(this, QuizRewardsActivity::class.java).apply {
    putExtra("ATTEMPT_ID", attemptId)
}
startActivity(intent)
finish() // Optional: prevent going back to quiz
```

#### B. Profile Menu Item
Add to your main menu or navigation drawer:

```kotlin
profileButton.setOnClickListener {
    startActivity(Intent(this, GamificationProfileActivity::class.java))
}
```

#### C. Leaderboard Menu Item
Add to your main menu or navigation drawer:

```kotlin
leaderboardButton.setOnClickListener {
    startActivity(Intent(this, EnhancedLeaderboardActivity::class.java))
}
```

### 4. Dependencies Check
Verify these are in your `app/build.gradle.kts`:

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
    
    // Coordinator Layout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
}
```

### 5. Request Notification Permission (Android 13+)
In your main activity or application class:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_CODE
        )
    }
}
```

### 6. Optional: Add Menu Icons
Update your menu resources to include gamification icons:

```xml
<!-- res/menu/main_menu.xml -->
<item
    android:id="@+id/menu_profile"
    android:title="Profile"
    android:icon="@drawable/ic_person" />

<item
    android:id="@+id/menu_leaderboard"
    android:title="Leaderboard"
    android:icon="@drawable/ic_leaderboard" />
```

### 7. Optional: Dashboard Widget
Add XP/Level widget to your student dashboard:

```xml
<!-- In your dashboard layout -->
<include
    android:id="@+id/xpBarWidget"
    layout="@layout/component_xp_bar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

Then in your activity:

```kotlin
// Load and display user stats
viewModel.loadUserStats()
viewModel.stats.observe(this) { stats ->
    // Update XP bar component
    updateXpBar(stats)
}
```

## 🧪 Testing Checklist

Once integrated, test these scenarios:

### Profile Screen
- [ ] Profile loads with user information
- [ ] XP bar displays current level and progress
- [ ] Statistics show correct values
- [ ] Badges grid displays earned badges
- [ ] Streak card shows current and longest streaks
- [ ] Category stats display properly
- [ ] Pull to refresh works

### Leaderboard
- [ ] Global leaderboard loads
- [ ] User's rank displays correctly
- [ ] Top 3 podium shows with medals
- [ ] "Better than X students" calculates correctly
- [ ] Percentile displays accurately
- [ ] Leaderboard list scrolls smoothly

### Rewards Screen
- [ ] Shows after quiz completion
- [ ] Performance tier displays correctly
- [ ] XP counter animates from 0 to earned value
- [ ] XP breakdown shows bonuses (if applicable)
- [ ] Level up card appears when leveling up
- [ ] New badges display when unlocked
- [ ] Share button works
- [ ] Continue button navigates properly

### Notifications
- [ ] Badge unlock notifications appear
- [ ] Level up notifications appear
- [ ] Streak reminder notifications work
- [ ] Multiple badges notification groups properly
- [ ] Tapping notification opens profile
- [ ] Notification channels created properly

### Edge Cases
- [ ] Works with no internet connection (cached data)
- [ ] Handles API errors gracefully
- [ ] Loading states display properly
- [ ] Empty states show when no data
- [ ] Works on different screen sizes
- [ ] Animations are smooth

## 🎨 Customization Options

### Change XP Values
Edit `app/src/main/java/com/example/quizmaster/data/model/Gamification.kt`:

```kotlin
object XpConstants {
    const val BASE_XP_PER_CORRECT = 10      // Change this
    const val FAST_RESPONSE_BONUS = 5       // And this
    const val MEDIUM_RESPONSE_BONUS = 3     // And this
    const val PERFECT_SCORE_BONUS = 20      // And this
    const val XP_PER_LEVEL = 100           // And this
}
```

### Change Colors
Edit `app/src/main/res/values/colors.xml`:

```xml
<color name="badge_common">#9E9E9E</color>
<color name="badge_uncommon">#4CAF50</color>
<color name="badge_rare">#2196F3</color>
<color name="badge_epic">#9C27B0</color>
<color name="badge_legendary">#FFD700</color>
```

### Add New Badges
1. Add to `BadgeType` enum in `Gamification.kt`
2. Implement methods: `getDisplayName()`, `getEmoji()`, `getDescription()`, `getRarity()`
3. Update backend to award badge based on criteria

## 📚 Documentation

- **GAMIFICATION_README.md**: Overview and features
- **GAMIFICATION_GUIDE.md**: Detailed implementation guide
- **Swagger API**: Backend API specifications

## 🐛 Troubleshooting

### Common Issues

**Profile not loading**
- Check API endpoint is implemented
- Verify authentication token is set
- Check logs for API errors

**XP not calculating correctly**
- Verify backend implements XP calculation
- Check `XpConstants` values
- Ensure attempt data includes timing

**Badges not appearing**
- Confirm backend tracks achievements
- Check badge unlock criteria in backend
- Verify API returns badge data

**Leaderboard empty**
- Ensure multiple users have completed quizzes
- Check global leaderboard API endpoint
- Verify sorting is correct on backend

**Notifications not showing**
- Request notification permission
- Check notification channels created
- Verify NotificationManager is initialized

## ✨ Next Steps

After integration:

1. **Test thoroughly** with real data
2. **Gather user feedback** on gamification features
3. **Monitor engagement** metrics
4. **Consider adding** future enhancements:
   - Daily challenges
   - Multiplayer competitions
   - Social features
   - Seasonal events
   - Custom avatars

## 🎉 You're All Set!

The gamification system is ready to go. Your users will now enjoy:
- 🎯 Clear goals with XP and levels
- 🏆 Achievements to collect
- 📊 Progress tracking
- 🏅 Competition through leaderboards
- 🎁 Rewarding feedback after quizzes

Happy coding! 🚀

---

For questions or issues, refer to the detailed guides:
- `GAMIFICATION_GUIDE.md` for implementation details
- `GAMIFICATION_README.md` for feature overview
