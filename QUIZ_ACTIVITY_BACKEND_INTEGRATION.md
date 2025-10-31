# QuizActivity Backend Integration - Summary

## ✅ What Was Implemented

### Backend Integration Added to `QuizActivity.kt`

The `QuizActivity` has been updated to properly integrate with the backend API for quiz attempts. Previously, it was using a fake `"temp_attempt_id"` which caused the 400 error you saw.

### Key Changes:

#### 1. **New Imports & Variables**
```kotlin
import com.example.quizmaster.data.repository.QuizAttemptRepository

private val attemptRepository = QuizAttemptRepository()
private var currentAttemptId: String? = null
private var currentQuizId: String? = null
private var quizStartTime = 0L
```

#### 2. **New Intent Extra**
```kotlin
const val EXTRA_QUIZ_ID = "extra_quiz_id" // Pass quiz ID from backend
```

#### 3. **Three New Backend Methods**

##### a) `startBackendAttempt(quizId: String)`
- Called when quiz starts (if `EXTRA_QUIZ_ID` is provided)
- Calls `POST /api/v1/attempts/start`
- Stores the returned `attempt.id` in `currentAttemptId`
- Falls back to local mode if backend fails

##### b) `submitAnswerToBackend(questionId, answer, timeToAnswer)`
- Called after each answer is submitted
- Calls `POST /api/v1/attempts/answer`
- Sends attempt ID, question ID, answer, and time taken
- Continues with local quiz even if submission fails

##### c) `completeBackendAttempt(onComplete)`
- Called when quiz finishes
- Calls `PUT /api/v1/attempts/complete`
- Returns the real attempt ID to pass to QuizRewardsActivity
- Falls back gracefully if completion fails

#### 4. **Updated Flow**

**Before:**
```kotlin
putExtra("ATTEMPT_ID", "temp_attempt_id") // ❌ Fake ID
```

**After:**
```kotlin
// Complete backend attempt first
completeBackendAttempt { attemptId ->
    putExtra("ATTEMPT_ID", attemptId ?: "local_attempt") // ✅ Real ID from backend
}
```

### Usage

#### Starting a Quiz with Backend Integration

**Option 1: With Backend Quiz ID (Recommended)**
```kotlin
val intent = Intent(context, QuizActivity::class.java).apply {
    putExtra(QuizActivity.EXTRA_QUIZ_ID, "672417b95677e3d6864240f5") // Real quiz ID
}
startActivity(intent)
```

**Option 2: Local Quiz (Legacy Mode)**
```kotlin
val intent = Intent(context, QuizActivity::class.java).apply {
    putExtra(QuizActivity.EXTRA_CATEGORY, "SCIENCE")
    putExtra(QuizActivity.EXTRA_DIFFICULTY, "MEDIUM")
}
startActivity(intent)
```

**Option 3: With Quiz JSON**
```kotlin
val intent = Intent(context, QuizActivity::class.java).apply {
    putExtra(QuizActivity.EXTRA_QUIZ_JSON, quizJsonString)
}
startActivity(intent)
```

### Flow Diagram

```
┌─────────────────────────────────────────────┐
│         QuizActivity Lifecycle              │
└─────────────────────────────────────────────┘

1. onCreate()
   ├─ Check if EXTRA_QUIZ_ID provided
   └─ If yes → startBackendAttempt(quizId)
      ├─ POST /api/v1/attempts/start
      └─ Store attempt.id in currentAttemptId
      
2. User answers each question
   └─ submitAnswer(answer)
      ├─ Calculate local score
      ├─ Update UI
      └─ submitAnswerToBackend(questionId, answer, time)
         └─ POST /api/v1/attempts/answer
         
3. Quiz completes
   └─ navigateToResults()
      └─ completeBackendAttempt { attemptId ->
         ├─ PUT /api/v1/attempts/complete
         └─ Navigate with REAL attempt ID
            └─ QuizRewardsActivity receives valid ID
               └─ Can fetch XP details ✅
```

### Error Handling

All backend methods include graceful fallback:

```kotlin
result.onSuccess { /* Use backend data */ }
result.onFailure { error ->
    Log.e(TAG, "Backend error: ${error.message}")
    // Continue with local quiz - no interruption
}
```

### Important Notes

#### 1. **Question ID Generation**
Currently using a temporary solution:
```kotlin
val questionId = "q_${question.question.hashCode()}"
```

**To properly integrate**, you need to:
- Store question IDs when fetching quiz from backend
- Pass question IDs through the Question model
- Use real question IDs when submitting answers

#### 2. **Backward Compatibility**
- If no `EXTRA_QUIZ_ID` is provided, quiz runs in local mode
- All backend operations fail gracefully
- User experience is uninterrupted

#### 3. **State Preservation**
```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(KEY_ATTEMPT_ID, currentAttemptId)
    outState.putLong(KEY_QUIZ_START_TIME, quizStartTime)
}
```

### Testing

#### Test Backend Integration:
1. Get a valid quiz ID from backend
2. Start QuizActivity with that ID:
   ```kotlin
   intent.putExtra("extra_quiz_id", "your_quiz_id_here")
   ```
3. Answer questions
4. Complete quiz
5. Check logs for backend calls:
   ```
   D/QuizActivity: Starting backend attempt for quiz: ...
   D/QuizActivity: Backend attempt started successfully: ...
   D/QuizActivity: Submitting answer - Attempt: ...
   D/QuizActivity: Completing backend attempt: ...
   ```

#### Expected Logs:
```
D/QuizActivity: Starting backend attempt for quiz: 672417b95677e3d6864240f5
I/okhttp: --> POST http://10.0.2.2:8080/api/v1/attempts/start
I/okhttp: <-- 201 Created
D/QuizActivity: Backend attempt started successfully: 672417b95677e3d6864240f6

D/QuizActivity: Submitting answer - Question: q_123, Answer: true, Time: 5
I/okhttp: --> POST http://10.0.2.2:8080/api/v1/attempts/answer
I/okhttp: <-- 200 OK
D/QuizActivity: Answer submitted - Correct: true, Points: 10

D/QuizActivity: Completing backend attempt: 672417b95677e3d6864240f6
I/okhttp: --> PUT http://10.0.2.2:8080/api/v1/attempts/complete
I/okhttp: <-- 200 OK
D/QuizActivity: Backend attempt completed successfully

// QuizRewardsActivity now receives REAL attempt ID
I/okhttp: --> GET http://10.0.2.2:8080/api/v1/attempts/672417b95677e3d6864240f6/xp
I/okhttp: <-- 200 OK ✅
```

### Next Steps

To fully integrate with backend, you need to:

1. **Update Quiz List/Detail Screen** to pass quiz IDs:
   ```kotlin
   intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quiz.id)
   ```

2. **Store Question IDs** in your Question model:
   ```kotlin
   data class Question(
       val id: String? = null, // Add this
       val question: String,
       // ... other fields
   )
   ```

3. **Fetch Quizzes from Backend** instead of using local/trivia API:
   ```kotlin
   val quizzes = quizRepository.getQuizzes() // From backend
   ```

4. **Test End-to-End**:
   - Login → Get token
   - Browse quizzes → Select quiz
   - Start quiz → Get attempt ID
   - Answer questions → Submit to backend
   - Complete quiz → Get XP and rewards
   - View leaderboard

### Troubleshooting

**Problem**: Still getting "temp_attempt_id" error
- **Solution**: Make sure you're passing `EXTRA_QUIZ_ID` in the intent

**Problem**: 400 Bad Request on start attempt
- **Solution**: Verify quiz ID exists and is approved in backend

**Problem**: 401 Unauthorized
- **Solution**: Ensure auth token is set: `ApiClient.setAuthToken(token)`

**Problem**: Quiz runs but no backend calls
- **Solution**: Check logs for "No attempt ID - skipping backend completion"
  This means EXTRA_QUIZ_ID was not provided

---

## Summary

✅ **QuizActivity now supports backend integration**  
✅ **Real attempt IDs are passed to QuizRewardsActivity**  
✅ **All answers are submitted to backend**  
✅ **Quiz completion triggers XP calculation**  
✅ **Graceful fallback to local mode if backend fails**  
✅ **Backward compatible with existing local quiz flow**

The error `{"error":"Invalid attempt ID"}` should now be resolved when you pass a valid quiz ID to QuizActivity! 🎉
