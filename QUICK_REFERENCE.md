# 🚀 QuizMaster - Quick Reference Guide

## 📋 File Structure Quick Reference

### Core Models Location
```
app/src/main/java/com/example/quizmaster/data/model/
├── User.kt                 # User with role, level, XP, badges
├── UserRole.kt             # STUDENT, PROFESSOR
├── AuthModels.kt           # Login/Register requests
├── QuizModel.kt            # Quiz with approval status
├── QuestionModel.kt        # Question with time scoring
├── QuizAttempt.kt          # Student attempt tracking
├── Course.kt               # Course completion models
└── Gamification.kt         # Badges & achievements
```

### API Services Location
```
app/src/main/java/com/example/quizmaster/data/remote/
├── ApiClient.kt            # ⚙️ Configure URLs here!
├── AuthApiService.kt
├── QuizApiService.kt
├── QuizAttemptApiService.kt
└── CourseApiService.kt
```

### Repositories Location
```
app/src/main/java/com/example/quizmaster/data/repository/
├── QuizManagementRepository.kt
└── LeaderboardRepository.kt
```

## ⚡ Key Configuration Points

### 1. API URLs (REQUIRED)
**File:** `ApiClient.kt`
```kotlin
// Line 14-15: Update these!
private const val BASE_URL = "https://your-api-server.com/"
private const val COURSE_API_BASE_URL = "https://your-course-api.com/"
```

### 2. App Entry Point
**File:** `AndroidManifest.xml`
```xml
<!-- Line 21: Current launcher -->
<activity android:name=".ui.auth.LoginActivity" />
```

### 3. Theme Colors
**File:** `res/values/themes.xml`
```xml
Primary: #4CAF50 (Green)
Dark: #388E3C
Accent: #FFD700 (Gold)
```

## 🎯 Scoring Quick Reference

### Time-Based Scoring
```
Response Time    Score    XP Bonus
0-5 seconds     100%     +5 XP
5-10 seconds    70%      +3 XP
10-15 seconds   40%      +0 XP
>15 seconds     0%       +0 XP

Base XP per correct answer: 10 XP
Perfect quiz bonus: +20 XP
```

### Badge Requirements
```
🎓 First Steps      → Complete 1 quiz
⚡ Speed Demon      → 10 questions < 3s each
💯 Perfect Score    → 100% on any quiz
🔥 5-Day Streak     → 5 consecutive days
👑 Quiz Master      → 50 quizzes completed
🌟 Category Expert  → 5 quizzes 90%+ same category
🥇 Level 50         → Reach level 50
```

## 🔐 Access Control Logic

### Course Completion Check
```kotlin
// Before quiz access:
val canAccess = courseApiService.checkCourseCompletion(
    CourseCompletionRequest(studentId, quiz.linkedCourseId)
).completed

if (!canAccess) {
    showMessage("Complete ${quiz.linkedCourseName} to unlock")
}
```

### Role-Based Actions
```kotlin
when (user.role) {
    UserRole.STUDENT -> {
        // Can create quizzes (pending approval)
        // Can take quizzes (if course completed)
        // Can view leaderboards
    }
    UserRole.PROFESSOR -> {
        // Can create quizzes (auto-approved)
        // Can approve/reject student quizzes
        // Can view all analytics
    }
}
```

## 📱 Activity Navigation Map

```
LoginActivity (Launcher)
    ├─→ StudentDashboardActivity (if student)
    │   ├─→ QuizActivity
    │   │   └─→ ResultsActivity
    │   │       └─→ LeaderboardActivity
    │   ├─→ QuizCreationActivity
    │   └─→ ProfileActivity
    │
    └─→ ProfessorDashboardActivity (if professor)
        ├─→ QuizCreationActivity
        ├─→ ApprovalActivity
        └─→ ProfileActivity
```

## 🛠️ Common Tasks

### Add a New Badge
1. Add to `BadgeType` enum in `Gamification.kt`
2. Update `getDisplayName()`, `getEmoji()`, `getDescription()`
3. Add string resource
4. Implement unlock logic in backend

### Add a New Question Type
1. Add to `QuestionType` enum in `QuestionModel.kt`
2. Update `getAllOptions()` method
3. Update UI in QuizActivity
4. Update QuizCreationActivity

### Change Scoring Thresholds
Edit `ScoreCalculator.kt`:
```kotlin
private const val FAST_RESPONSE_THRESHOLD = 5  // Change this
private const val MEDIUM_RESPONSE_THRESHOLD = 10  // Change this
private const val SLOW_RESPONSE_THRESHOLD = 15  // Change this
```

## 📊 ViewModel Pattern Example

```kotlin
class StudentDashboardViewModel(
    private val quizRepository: QuizManagementRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {
    
    private val _quizzes = MutableLiveData<List<QuizModel>>()
    val quizzes: LiveData<List<QuizModel>> = _quizzes
    
    fun loadAvailableQuizzes(studentId: String, token: String) {
        viewModelScope.launch {
            val result = quizRepository.getAvailableQuizzesForStudent(studentId, token)
            result.onSuccess { _quizzes.value = it }
        }
    }
}
```

## 🎨 UI Component Examples

### Progress Bar (Level Progress)
```xml
<ProgressBar
    android:layout_width="match_parent"
    android:layout_height="8dp"
    android:max="100"
    android:progress="65"
    style="?android:attr/progressBarStyleHorizontal"
    android:progressTint="#4CAF50"/>
```

### Badge Display
```xml
<TextView
    android:text="🏆"
    android:textSize="48sp"
    android:background="@drawable/bg_timer"
    android:padding="16dp"/>
```

### Timer Display
```xml
<TextView
    android:id="@+id/timerText"
    android:text="15s"
    android:textSize="32sp"
    android:textStyle="bold"
    android:textColor="#4CAF50"
    android:background="@drawable/bg_timer"/>
```

## 🧪 Testing Checklist

### Before Each PR
- [ ] Code compiles without errors
- [ ] No hardcoded strings (use resources)
- [ ] Null safety checks
- [ ] Coroutine proper scope
- [ ] Error handling implemented
- [ ] Loading states handled
- [ ] Comments added for complex logic

### Testing Scenarios
- [ ] Login with valid/invalid credentials
- [ ] Student accessing locked quiz
- [ ] Professor approving/rejecting quiz
- [ ] Timer countdown and expiry
- [ ] Score calculation
- [ ] XP and level up
- [ ] Badge unlocking
- [ ] Leaderboard display
- [ ] Network error handling
- [ ] Session expiry

## 🐛 Common Issues & Solutions

### Issue: "Cannot resolve symbol 'R'"
**Solution:** Sync Gradle, Clean Project, Rebuild

### Issue: API calls fail
**Solution:** Check `ApiClient.kt` URLs, verify internet permission in manifest

### Issue: DataStore not working
**Solution:** Ensure coroutine context, use `lifecycleScope.launch`

### Issue: Timer not updating UI
**Solution:** Update UI on main thread using `runOnUiThread()` or LiveData

## 📦 Dependencies Quick Reference

```kotlin
// Already added in build.gradle.kts
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
implementation("androidx.datastore:datastore-preferences")
implementation("com.squareup.retrofit2:retrofit")
implementation("com.squareup.retrofit2:converter-gson")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
```

## 🔗 Useful Links

- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Material Design](https://material.io/develop/android)
- [Android DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

## 📞 Need Help?

1. Check `IMPLEMENTATION_GUIDE.md` for detailed instructions
2. Review `README.md` for architecture overview
3. Check `IMPLEMENTATION_SUMMARY.md` for progress tracking
4. Look at code comments in each file

---

**Last Updated:** [Current Date]
**Version:** 1.0
**Status:** Core Complete, UI In Progress
