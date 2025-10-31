# Quiz Attempt API - Quick Reference

## Setup
```kotlin
// Set authentication token (after login)
ApiClient.setAuthToken("your_jwt_token")

// Create repository instance
val repository = QuizAttemptRepository()
```

## Quiz Attempt Flow

### 1️⃣ Start Quiz
```kotlin
repository.startAttempt(quizId = "quiz_id_123")
    .onSuccess { attempt ->
        val attemptId = attempt.id
        // Save attemptId for next steps
    }
```
**Endpoint**: `POST /api/v1/attempts/start`

### 2️⃣ Submit Each Answer
```kotlin
repository.submitAnswer(
    attemptId = "attempt_id",
    questionId = "question_id",
    answer = "true",        // or "0", "1", "2", "3" for MC
    timeToAnswer = 8        // seconds
)
```
**Endpoint**: `POST /api/v1/attempts/answer`

### 3️⃣ Complete Quiz
```kotlin
repository.completeAttempt(attemptId = "attempt_id")
    .onSuccess { result ->
        println("Score: ${result.totalScore}/${result.maxScore}")
        println("XP: ${result.xpEarned}")
        if (result.xpDetails?.leveledUp == true) {
            println("🎉 Level Up! ${result.xpDetails.newLevel}")
        }
    }
```
**Endpoint**: `PUT /api/v1/attempts/complete`

## Retrieve Data

### Get My Attempts
```kotlin
repository.getMyAttempts()
```
**Endpoint**: `GET /api/v1/attempts`

### Get Attempt Details
```kotlin
repository.getAttemptById(attemptId = "attempt_id")
```
**Endpoint**: `GET /api/v1/attempts/{id}`

### Get XP Breakdown
```kotlin
repository.getAttemptXpDetails(attemptId = "attempt_id")
```
**Endpoint**: `GET /api/v1/attempts/{id}/xp`

## Leaderboards

### Quiz Leaderboard
```kotlin
repository.getQuizLeaderboard(quizId = "quiz_id")
```
**Endpoint**: `GET /api/v1/leaderboards/quiz/{quiz_id}`

### My Rank
```kotlin
repository.getMyRankForQuiz(quizId = "quiz_id")
```
**Endpoint**: `GET /api/v1/leaderboards/quiz/{quiz_id}/my-rank`

### Global Leaderboard
```kotlin
repository.getGlobalLeaderboard()
```
**Endpoint**: `GET /api/v1/leaderboards/global`

## Data Models

### QuizAttempt
```kotlin
id: String                  // Attempt ID
quizId: String             // Quiz ID
studentId: String          // Student ID
answers: List<Answer>      // All answers
totalScore: Double         // Points earned
maxScore: Double           // Max possible points
timeTaken: Int             // Seconds
startedAt: String          // ISO 8601
completedAt: String?       // ISO 8601 or null
xpEarned: Int             // Total XP
xpDetails: XPReward?      // XP breakdown
```

### Answer
```kotlin
questionId: String         // Question ID
studentAnswer: Any?        // Answer submitted
isCorrect: Boolean        // Correct?
pointsEarned: Double      // Points for this Q
timeToAnswer: Int         // Seconds
answeredAt: String?       // ISO 8601
```

### XPReward
```kotlin
baseXp: Int               // Base XP
speedBonus: Int           // Speed bonus
accuracyBonus: Int        // Accuracy bonus
difficultyBonus: Int      // Difficulty bonus
streakBonus: Int          // Streak bonus
totalXp: Int              // Total XP
leveledUp: Boolean        // Did level up?
newLevel: Int             // New level
```

## Utility Functions

```kotlin
// Get percentage
attempt.getPercentage(): Double

// Get rating
attempt.getPerformanceRating(): String
// Returns: "Excellent! 🏆", "Great Job! ⭐", etc.

// Get correct count
attempt.getCorrectAnswersCount(): Int

// Get total questions
attempt.getTotalQuestions(): Int

// Get avg time per question
attempt.getAverageTimePerQuestion(): Double
```

## Error Handling

```kotlin
result.fold(
    onSuccess = { data -> /* handle success */ },
    onFailure = { error ->
        when {
            error.message?.contains("401") == true -> // Unauthorized
            error.message?.contains("403") == true -> // Forbidden
            error.message?.contains("404") == true -> // Not found
            error.message?.contains("409") == true -> // Conflict
            else -> // Other error
        }
    }
)
```

## Status Codes
- **200** ✅ Success
- **201** ✅ Created
- **400** ❌ Bad Request
- **401** ❌ Unauthorized
- **403** ❌ Forbidden
- **404** ❌ Not Found
- **409** ❌ Conflict
- **500** ❌ Server Error

## Answer Formats
- **True/False**: `"true"` or `"false"`
- **Multiple Choice**: `"0"`, `"1"`, `"2"`, or `"3"` (option index)

## Base URL
- **Emulator**: `http://10.0.2.2:8080/api/v1/`
- **Physical Device**: `http://YOUR_IP:8080/api/v1/`

---
For detailed documentation, see: `QUIZ_ATTEMPT_INTEGRATION.md`
