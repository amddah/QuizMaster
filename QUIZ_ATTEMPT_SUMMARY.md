# Quiz Attempt Backend Integration - Summary

## ✅ What Was Done

### 1. Updated Data Models (`QuizAttempt.kt`)
- **Answer Model**: Updated to match backend structure with correct field names
  - `questionId`, `studentAnswer`, `isCorrect`, `pointsEarned`, `timeToAnswer`, `answeredAt`
- **XPReward Model**: Added new model for XP breakdown
  - Tracks base XP, speed bonus, accuracy bonus, difficulty bonus, streak bonus
  - Includes level-up information
- **QuizAttempt Model**: Completely refactored to match backend Swagger spec
  - Changed field names to match backend (e.g., `max_score` instead of `max_possible_score`)
  - Updated to use proper timestamp strings instead of Long
  - Added helper methods: `getPercentage()`, `getPerformanceRating()`, `getCorrectAnswersCount()`, etc.

### 2. Updated API Service (`QuizAttemptApiService.kt`)
- Fixed all request/response models with proper `@SerializedName` annotations
- **StartAttemptRequest**: Uses `quizId` field
- **SubmitAnswerRequest**: Uses `attemptId`, `questionId`, `answer`, `timeToAnswer`
- **CompleteAttemptRequest**: Added new request model (uses body, not path param)
- **LeaderboardEntry**: Updated with all fields from backend
- Added **new endpoint**: `GET /attempts/{id}/xp` for XP details
- Fixed **completeAttempt** endpoint to use `PUT /attempts/complete` with request body
- All endpoints now have proper documentation comments

### 3. Updated Repository (`QuizAttemptRepository.kt`)
- Updated all methods to use corrected request models
- Improved error handling with detailed error messages from response body
- Added comprehensive documentation for each method
- Added new method: `getAttemptXpDetails()` for XP breakdown
- All methods now properly match the backend API specification

### 4. Documentation Created
Created two comprehensive documentation files:

#### `QUIZ_ATTEMPT_INTEGRATION.md` (Full Guide)
- Complete API integration guide
- Step-by-step quiz attempt flow
- All endpoints with request/response examples
- Data model documentation
- Error handling guide
- ViewModel example
- Troubleshooting section

#### `QUIZ_ATTEMPT_QUICK_REFERENCE.md` (Quick Reference)
- Quick code snippets for all operations
- Data model structures
- Status codes reference
- Answer format guide
- Utility functions list

## 🔗 API Endpoints Implemented

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/attempts/start` | Start a new quiz attempt |
| POST | `/attempts/answer` | Submit answer for a question |
| PUT | `/attempts/complete` | Complete quiz attempt |
| GET | `/attempts` | Get all my attempts |
| GET | `/attempts/{id}` | Get specific attempt details |
| GET | `/attempts/{id}/xp` | Get XP breakdown for attempt |
| GET | `/leaderboards/global` | Get global leaderboard (top 50) |
| GET | `/leaderboards/quiz/{quiz_id}` | Get quiz leaderboard |
| GET | `/leaderboards/quiz/{quiz_id}/my-rank` | Get my rank for quiz |

## 📦 Key Classes

### Models
- `Answer` - Single answer in an attempt
- `XPReward` - XP breakdown with bonuses
- `QuizAttempt` - Complete quiz attempt data
- `LeaderboardEntry` - Leaderboard entry data

### API
- `QuizAttemptApiService` - Retrofit interface
- `StartAttemptRequest` - Start attempt request
- `SubmitAnswerRequest` - Submit answer request
- `CompleteAttemptRequest` - Complete attempt request

### Repository
- `QuizAttemptRepository` - Business logic layer
  - All CRUD operations for quiz attempts
  - Leaderboard operations
  - XP detail retrieval

## 🎯 Usage Example

```kotlin
// 1. Setup
ApiClient.setAuthToken(userToken)
val repository = QuizAttemptRepository()

// 2. Start Quiz
val attempt = repository.startAttempt("quiz_id").getOrThrow()

// 3. Submit Answers (repeat for each question)
repository.submitAnswer(
    attemptId = attempt.id,
    questionId = "q1",
    answer = "true",
    timeToAnswer = 8
)

// 4. Complete Quiz
val result = repository.completeAttempt(attempt.id).getOrThrow()

// 5. Show Results
println("Score: ${result.totalScore}/${result.maxScore}")
println("XP Earned: ${result.xpEarned}")
if (result.xpDetails?.leveledUp == true) {
    println("🎉 Level Up to ${result.xpDetails.newLevel}!")
}
```

## ✨ Key Features

1. **Full Backend Compatibility**: All models and endpoints match Swagger spec exactly
2. **Proper Error Handling**: Detailed error messages from backend
3. **Type Safety**: Strongly typed Kotlin models
4. **Comprehensive Documentation**: Both detailed guide and quick reference
5. **XP Tracking**: Full gamification support with XP breakdown
6. **Leaderboards**: Global and per-quiz leaderboards
7. **Helper Methods**: Utility functions for common calculations

## 🔧 Configuration

Base URL in `ApiClient.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"
```

For physical devices, update to your computer's IP:
```kotlin
private const val BASE_URL = "http://192.168.1.XXX:8080/api/v1/"
```

## 🧪 Testing

To test the integration:

1. Start your Go backend server
2. Register/login to get auth token
3. Set token: `ApiClient.setAuthToken(token)`
4. Follow the quiz attempt flow
5. Verify responses match expected format

## 📝 Notes

- All endpoints require authentication (JWT token in Authorization header)
- Quiz attempts are for students only (professors cannot attempt)
- Some quizzes may require course completion
- Timestamps are in ISO 8601 format
- IDs are MongoDB ObjectID strings
- Answer format: "true"/"false" for T/F, "0"/"1"/"2"/"3" for MC

## 🚀 Next Steps

1. **UI Integration**: Update UI components to use new models
2. **ViewModel Updates**: Update ViewModels to handle new response structures
3. **Testing**: Thoroughly test all endpoints with backend
4. **Error Handling**: Add user-friendly error messages in UI
5. **Offline Support**: Consider caching attempts for offline viewing

## 📚 Documentation Files

- `QUIZ_ATTEMPT_INTEGRATION.md` - Complete integration guide
- `QUIZ_ATTEMPT_QUICK_REFERENCE.md` - Quick reference card
- `QUIZ_ATTEMPT_SUMMARY.md` - This summary

## ✅ Verification Checklist

- [x] Models match backend Swagger spec
- [x] All API endpoints implemented
- [x] Request models have proper serialization
- [x] Response models have proper deserialization
- [x] Error handling implemented
- [x] Repository methods updated
- [x] Documentation created
- [x] No compilation errors
- [ ] Integration tested with backend
- [ ] UI updated to use new models
- [ ] ViewModels updated

---

**Status**: ✅ Backend integration complete and ready for testing

**Files Modified**:
1. `app/src/main/java/com/example/quizmaster/data/model/QuizAttempt.kt`
2. `app/src/main/java/com/example/quizmaster/data/remote/QuizAttemptApiService.kt`
3. `app/src/main/java/com/example/quizmaster/data/repository/QuizAttemptRepository.kt`

**Files Created**:
1. `QUIZ_ATTEMPT_INTEGRATION.md`
2. `QUIZ_ATTEMPT_QUICK_REFERENCE.md`
3. `QUIZ_ATTEMPT_SUMMARY.md`
