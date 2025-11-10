# Student Attempts Feature - Implementation Complete

## Overview
Successfully implemented the "View Student Attempts" feature for professors, allowing them to:
1. View all their created quizzes with student attempt counts
2. Click on a quiz to see a leaderboard ranking of students who attempted it

## Feature Flow

### Screen 1: Professor Quizzes List
**File**: `activity_professor_quizzes.xml`
- Shows all quizzes created by the professor
- Each quiz displays:
  - Quiz title
  - Category badge (green background)
  - Number of students who attempted the quiz
  - "View Rankings" button (disabled if 0 attempts, enabled otherwise)

### Screen 2: Quiz Leaderboard
**File**: `activity_quiz_leaderboard.xml`
- Shows rankings for a specific quiz
- Header card displays:
  - Quiz title
  - Total student count ("X students attempted")
- Leaderboard shows each student:
  - Rank badge (Gold #1, Silver #2, Bronze #3, Green for others)
  - Student name
  - Score (e.g., "10 / 20")
  - Percentage badge

## Files Created

### Layouts
1. **activity_professor_quizzes.xml** - Main list screen with RecyclerView, toolbar, loading/empty states
2. **item_professor_quiz.xml** - Quiz card showing title, category, attempt count, view button
3. **activity_quiz_leaderboard.xml** - Leaderboard screen with quiz info card and rankings list
4. **item_leaderboard_entry.xml** (already existed) - Individual ranking row

### Activities
1. **ProfessorQuizzesActivity.kt** (`ui/professor/`)
   - Fetches all quizzes via `quizApiService.getAllQuizzes()`
   - For each quiz, calls `leaderboardApiService.getQuizLeaderboard(quizId)` to get attempt count
   - Populates RecyclerView with `ProfessorQuizzesAdapter`
   - Navigates to `QuizLeaderboardActivity` when "View Rankings" clicked

2. **QuizLeaderboardActivity.kt** (`ui/professor/`)
   - Receives `QUIZ_ID` and `QUIZ_TITLE` from intent extras
   - Calls `leaderboardApiService.getQuizLeaderboard(quizId)`
   - Displays quiz info and student rankings
   - Uses `LeaderboardAdapter` for the list

### Adapters
1. **ProfessorQuizzesAdapter.kt** - Binds quiz data, enables/disables button based on attempt count
2. **LeaderboardAdapter.kt** - Displays student rankings with colored rank badges (Gold/Silver/Bronze/Green)

### Data Models (already existed)
1. **LeaderboardEntry.kt** - Contains:
   - `LeaderboardEntry`: rank, studentId, studentName, score, maxScore, percentage, timeTaken, completedAt
   - `QuizLeaderboardResponse`: wrapper with leaderboard array, quizId, totalCount
   - `QuizWithAttempts`: combines QuizApiModel with attemptCount

2. **LeaderboardApiService.kt** - Defines:
   - `getQuizLeaderboard(quizId)`: GET /api/v1/leaderboards/quiz/{quiz_id}

## Integration Points

### ProfileActivity.kt
**Modified**: `viewStudentAttemptsButton` click listener
```kotlin
viewStudentAttemptsButton?.setOnClickListener {
    startActivity(Intent(this, ProfessorQuizzesActivity::class.java))
}
```

### AndroidManifest.xml
**Added**:
```xml
<activity
    android:name=".ui.professor.ProfessorQuizzesActivity"
    android:parentActivityName=".ui.profile.ProfileActivity"
    android:theme="@style/Theme.MyApplication.NoActionBar" />

<activity
    android:name=".ui.professor.QuizLeaderboardActivity"
    android:parentActivityName=".ui.professor.ProfessorQuizzesActivity"
    android:theme="@style/Theme.MyApplication.NoActionBar" />
```

## API Endpoints Used

### 1. Get All Quizzes
- **Endpoint**: `GET /api/v1/quizzes`
- **Service**: `QuizApiService.getAllQuizzes()`
- **Response**: `List<QuizApiModel>`

### 2. Get Quiz Leaderboard
- **Endpoint**: `GET /api/v1/leaderboards/quiz/{quiz_id}`
- **Service**: `LeaderboardApiService.getQuizLeaderboard(quizId)`
- **Response**: `QuizLeaderboardResponse`
  ```json
  {
    "leaderboard": [{
      "rank": 1,
      "student_id": "...",
      "student_name": "...",
      "score": 10,
      "max_score": 20,
      "percentage": 50,
      "time_taken": 6,
      "completed_at": "2025-11-10T12:09:15.869Z"
    }],
    "quiz_id": "...",
    "total_count": 2
  }
  ```

## UI/UX Features

### Visual Design
- **Material Design**: CardView with rounded corners, subtle elevation
- **Color Coding**:
  - Green (#4CAF50) for primary elements (badges, percentages)
  - Gold (#FFD700) for rank #1
  - Silver (#C0C0C0) for rank #2
  - Bronze (#CD7F32) for rank #3
- **Responsive States**: 
  - Loading spinner during API calls
  - Empty state messages when no data
  - Disabled buttons with reduced opacity when no attempts

### User Experience
- **Navigation**: Back button in toolbar for both screens
- **Parent Activities**: Proper navigation hierarchy (Profile → Quizzes → Leaderboard)
- **Error Handling**: Toast messages for API failures
- **Data Display**: Clear, concise information with appropriate formatting

## Testing Notes

### Build Status
✅ **BUILD SUCCESSFUL** - All files compile without errors

### To Test
1. Log in as a **professor** account
2. Navigate to Profile screen
3. Verify "View Student Attempts" button is visible
4. Click button to open quiz list
5. Verify all created quizzes appear with attempt counts
6. Click "View Rankings" on a quiz with attempts
7. Verify leaderboard shows ranked students with correct data
8. Test back navigation works correctly

### Known Requirements
- Backend must be running on localhost:8080 (10.0.2.2 for Android emulator)
- Professor must have created quizzes
- Students must have attempted quizzes for rankings to appear
- Valid authentication token required

## Technical Notes

### Package Structure
```
com.example.quizmaster.ui.professor/
├── ProfessorQuizzesActivity.kt
├── ProfessorQuizzesAdapter.kt
├── QuizLeaderboardActivity.kt
└── LeaderboardAdapter.kt
```

### Dependencies
- Kotlin Coroutines for async operations
- Retrofit 2 for API communication
- RecyclerView with ListAdapter and DiffUtil
- Material Components for UI
- Gson for JSON parsing

### Best Practices Applied
- **Separation of Concerns**: Activities handle UI, Adapters handle list items
- **Null Safety**: Proper null handling for API responses
- **Coroutines**: IO operations on background thread, UI updates on Main thread
- **Error Handling**: Try-catch blocks with user-friendly messages
- **Resource Management**: Proper use of ViewBinding and findViewById
- **Code Reusability**: DiffUtil for efficient list updates

## Future Enhancements (Optional)
- Add filter by category/difficulty in quiz list
- Add search functionality
- Show time taken by each student in leaderboard
- Add export leaderboard to CSV feature
- Add quiz analytics (average score, pass rate, etc.)
- Implement pull-to-refresh for data updates
- Add sorting options for leaderboard (by score, time, date)

---

**Status**: ✅ **COMPLETE AND READY FOR TESTING**
**Build**: ✅ **SUCCESSFUL** (37 tasks up-to-date)
**Integration**: ✅ **WIRED** (ProfileActivity → ProfessorQuizzesActivity → QuizLeaderboardActivity)
