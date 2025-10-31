# Quiz Attempt Backend Integration Guide

## Overview
This guide explains how to use the quiz attempt functionality that is now fully integrated with the backend API according to the Swagger specification.

## API Base URL
```
http://10.0.2.2:8080/api/v1/  (for Android emulator)
```

## Authentication
All quiz attempt endpoints require authentication. Make sure to set the auth token:
```kotlin
ApiClient.setAuthToken("your_jwt_token_here")
```

## Complete Quiz Attempt Flow

### 1. Start a Quiz Attempt
```kotlin
val repository = QuizAttemptRepository()

// Start attempting a quiz
val result = repository.startAttempt(quizId = "your_quiz_id")

result.onSuccess { attempt ->
    // Save the attempt ID for subsequent operations
    val attemptId = attempt.id
    val startedAt = attempt.startedAt
    
    println("Quiz attempt started: $attemptId")
    println("Quiz ID: ${attempt.quizId}")
    println("Student ID: ${attempt.studentId}")
}

result.onFailure { error ->
    println("Failed to start attempt: ${error.message}")
}
```

**Backend Endpoint:** `POST /api/v1/attempts/start`

**Request Body:**
```json
{
  "quiz_id": "507f1f77bcf86cd799439011"
}
```

**Response (201 Created):**
```json
{
  "id": "attempt_id_123",
  "quiz_id": "507f1f77bcf86cd799439011",
  "student_id": "student_id_456",
  "answers": [],
  "total_score": 0,
  "max_score": 100,
  "time_taken": 0,
  "started_at": "2025-10-31T10:00:00Z",
  "completed_at": null,
  "xp_earned": 0,
  "xp_details": null
}
```

### 2. Submit Answers for Questions
As the user answers each question, submit the answer:

```kotlin
val repository = QuizAttemptRepository()

// Submit answer for a question
val result = repository.submitAnswer(
    attemptId = "attempt_id_123",
    questionId = "question_id_789",
    answer = "true",  // or "0", "1", "2", "3" for multiple choice
    timeToAnswer = 8  // seconds taken to answer
)

result.onSuccess { response ->
    // Response contains feedback about the answer
    val isCorrect = response["is_correct"] as? Boolean
    val pointsEarned = response["points_earned"] as? Double
    
    println("Answer submitted successfully")
    println("Correct: $isCorrect")
    println("Points earned: $pointsEarned")
}

result.onFailure { error ->
    println("Failed to submit answer: ${error.message}")
}
```

**Backend Endpoint:** `POST /api/v1/attempts/answer`

**Request Body:**
```json
{
  "attempt_id": "attempt_id_123",
  "question_id": "question_id_789",
  "answer": "true",
  "time_to_answer": 8
}
```

**Response (200 OK):**
```json
{
  "is_correct": true,
  "points_earned": 10.5,
  "message": "Correct answer!"
}
```

### 3. Complete the Quiz Attempt
After all questions are answered:

```kotlin
val repository = QuizAttemptRepository()

// Complete the attempt and get final results
val result = repository.completeAttempt(attemptId = "attempt_id_123")

result.onSuccess { completedAttempt ->
    println("Quiz completed!")
    println("Total Score: ${completedAttempt.totalScore}/${completedAttempt.maxScore}")
    println("Percentage: ${completedAttempt.getPercentage()}%")
    println("Time Taken: ${completedAttempt.timeTaken} seconds")
    println("XP Earned: ${completedAttempt.xpEarned}")
    
    // Check if user leveled up
    completedAttempt.xpDetails?.let { xpReward ->
        println("\n=== XP Breakdown ===")
        println("Base XP: ${xpReward.baseXp}")
        println("Speed Bonus: ${xpReward.speedBonus}")
        println("Accuracy Bonus: ${xpReward.accuracyBonus}")
        println("Difficulty Bonus: ${xpReward.difficultyBonus}")
        println("Streak Bonus: ${xpReward.streakBonus}")
        println("Total XP: ${xpReward.totalXp}")
        
        if (xpReward.leveledUp) {
            println("\n🎉 LEVEL UP! New Level: ${xpReward.newLevel}")
        }
    }
    
    // Display performance rating
    println("\nRating: ${completedAttempt.getPerformanceRating()}")
}

result.onFailure { error ->
    println("Failed to complete attempt: ${error.message}")
}
```

**Backend Endpoint:** `PUT /api/v1/attempts/complete`

**Request Body:**
```json
{
  "id": "attempt_id_123"
}
```

**Response (200 OK):**
```json
{
  "id": "attempt_id_123",
  "quiz_id": "507f1f77bcf86cd799439011",
  "student_id": "student_id_456",
  "answers": [
    {
      "question_id": "question_id_789",
      "student_answer": "true",
      "is_correct": true,
      "points_earned": 10.5,
      "time_to_answer": 8,
      "answered_at": "2025-10-31T10:00:08Z"
    }
  ],
  "total_score": 85.5,
  "max_score": 100,
  "time_taken": 120,
  "started_at": "2025-10-31T10:00:00Z",
  "completed_at": "2025-10-31T10:02:00Z",
  "xp_earned": 250,
  "xp_details": {
    "base_xp": 170,
    "speed_bonus": 30,
    "accuracy_bonus": 25,
    "difficulty_bonus": 15,
    "streak_bonus": 10,
    "total_xp": 250,
    "leveled_up": true,
    "new_level": 5
  }
}
```

### 4. Get Detailed XP Breakdown (Optional)
You can also retrieve XP details separately:

```kotlin
val repository = QuizAttemptRepository()

val result = repository.getAttemptXpDetails(attemptId = "attempt_id_123")

result.onSuccess { xpDetails ->
    println("XP Details:")
    println(xpDetails)
}
```

**Backend Endpoint:** `GET /api/v1/attempts/{id}/xp`

## Retrieving Quiz Attempts

### Get All My Attempts
```kotlin
val repository = QuizAttemptRepository()

val result = repository.getMyAttempts()

result.onSuccess { attempts ->
    println("Found ${attempts.size} quiz attempts")
    
    attempts.forEach { attempt ->
        println("\nAttempt ID: ${attempt.id}")
        println("Quiz ID: ${attempt.quizId}")
        println("Score: ${attempt.totalScore}/${attempt.maxScore}")
        println("Percentage: ${attempt.getPercentage()}%")
        println("Completed: ${attempt.completedAt ?: "In Progress"}")
    }
}
```

**Backend Endpoint:** `GET /api/v1/attempts`

### Get Specific Attempt by ID
```kotlin
val repository = QuizAttemptRepository()

val result = repository.getAttemptById(attemptId = "attempt_id_123")

result.onSuccess { attempt ->
    println("Attempt details loaded")
    println("Total Questions: ${attempt.getTotalQuestions()}")
    println("Correct Answers: ${attempt.getCorrectAnswersCount()}")
    println("Avg Time/Question: ${attempt.getAverageTimePerQuestion()} seconds")
}
```

**Backend Endpoint:** `GET /api/v1/attempts/{id}`

## Leaderboard Integration

### Get Quiz Leaderboard
```kotlin
val repository = QuizAttemptRepository()

val result = repository.getQuizLeaderboard(quizId = "quiz_id_123")

result.onSuccess { leaderboard ->
    println("=== Leaderboard ===")
    
    leaderboard.forEachIndexed { index, entry ->
        println("${entry.rank}. ${entry.studentName}")
        println("   Score: ${entry.score}/${entry.maxScore} (${entry.percentage}%)")
        println("   Time: ${entry.timeTaken}s")
    }
}
```

**Backend Endpoint:** `GET /api/v1/leaderboards/quiz/{quiz_id}`

### Get My Rank for a Quiz
```kotlin
val repository = QuizAttemptRepository()

val result = repository.getMyRankForQuiz(quizId = "quiz_id_123")

result.onSuccess { rankInfo ->
    val rank = rankInfo["rank"] as? Int
    val totalParticipants = rankInfo["total_participants"] as? Int
    
    println("Your rank: $rank out of $totalParticipants")
}
```

**Backend Endpoint:** `GET /api/v1/leaderboards/quiz/{quiz_id}/my-rank`

### Get Global Leaderboard
```kotlin
val repository = QuizAttemptRepository()

val result = repository.getGlobalLeaderboard()

result.onSuccess { globalLeaderboard ->
    println("=== Global Leaderboard (Top 50) ===")
    
    globalLeaderboard.forEach { entry ->
        val studentName = entry["student_name"] as? String
        val totalXp = entry["total_xp"] as? Int
        val level = entry["level"] as? Int
        
        println("$studentName - Level $level (${totalXp}XP)")
    }
}
```

**Backend Endpoint:** `GET /api/v1/leaderboards/global`

## Data Models

### QuizAttempt
```kotlin
data class QuizAttempt(
    val id: String,                      // Attempt ID
    val quizId: String,                  // Quiz ID
    val studentId: String,               // Student ID
    val answers: List<Answer>,           // List of submitted answers
    val totalScore: Double,              // Total score earned
    val maxScore: Double,                // Maximum possible score
    val timeTaken: Int,                  // Time in seconds
    val startedAt: String,               // ISO 8601 timestamp
    val completedAt: String?,            // ISO 8601 timestamp (null if in progress)
    val xpEarned: Int,                   // Total XP earned
    val xpDetails: XPReward?             // Detailed XP breakdown
)
```

### Answer
```kotlin
data class Answer(
    val questionId: String,              // Question ID
    val studentAnswer: Any?,             // Student's answer
    val isCorrect: Boolean,              // Whether answer was correct
    val pointsEarned: Double,            // Points earned for this question
    val timeToAnswer: Int,               // Time taken in seconds
    val answeredAt: String?              // ISO 8601 timestamp
)
```

### XPReward
```kotlin
data class XPReward(
    val baseXp: Int,                     // Base XP from correct answers
    val speedBonus: Int,                 // Bonus for fast completion
    val accuracyBonus: Int,              // Bonus for high accuracy
    val difficultyBonus: Int,            // Bonus based on difficulty
    val streakBonus: Int,                // Bonus from streak
    val totalXp: Int,                    // Total XP earned
    val leveledUp: Boolean,              // Whether user leveled up
    val newLevel: Int                    // New level if leveled up
)
```

## Error Handling

All repository methods return `Result<T>`, handle both success and failure:

```kotlin
repository.startAttempt(quizId).fold(
    onSuccess = { attempt ->
        // Handle success
    },
    onFailure = { error ->
        when {
            error.message?.contains("401") == true -> {
                // Unauthorized - token expired
                println("Please login again")
            }
            error.message?.contains("403") == true -> {
                // Forbidden - no access
                println("You don't have permission")
            }
            error.message?.contains("404") == true -> {
                // Not found
                println("Quiz not found")
            }
            error.message?.contains("409") == true -> {
                // Conflict - already attempted
                println("You've already attempted this quiz")
            }
            else -> {
                // Generic error
                println("Error: ${error.message}")
            }
        }
    }
)
```

## Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Attempt created successfully
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Missing or invalid auth token
- **403 Forbidden**: No permission (e.g., student trying to access professor-only feature)
- **404 Not Found**: Resource not found
- **409 Conflict**: Attempt already exists or other conflict
- **500 Internal Server Error**: Server error

## Example: Complete Quiz Flow in ViewModel

```kotlin
class QuizAttemptViewModel : ViewModel() {
    private val repository = QuizAttemptRepository()
    
    private val _attemptState = MutableLiveData<QuizAttempt>()
    val attemptState: LiveData<QuizAttempt> = _attemptState
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private var currentAttemptId: String? = null
    
    // Step 1: Start attempt
    fun startQuizAttempt(quizId: String) {
        viewModelScope.launch {
            _loading.value = true
            
            val result = repository.startAttempt(quizId)
            
            result.onSuccess { attempt ->
                currentAttemptId = attempt.id
                _attemptState.value = attempt
                _loading.value = false
            }
            
            result.onFailure { error ->
                _error.value = "Failed to start quiz: ${error.message}"
                _loading.value = false
            }
        }
    }
    
    // Step 2: Submit answer
    fun submitAnswer(questionId: String, answer: String, timeToAnswer: Int) {
        val attemptId = currentAttemptId ?: return
        
        viewModelScope.launch {
            val result = repository.submitAnswer(
                attemptId = attemptId,
                questionId = questionId,
                answer = answer,
                timeToAnswer = timeToAnswer
            )
            
            result.onSuccess { response ->
                // Update UI with feedback
                val isCorrect = response["is_correct"] as? Boolean
                // Show feedback to user
            }
            
            result.onFailure { error ->
                _error.value = "Failed to submit answer: ${error.message}"
            }
        }
    }
    
    // Step 3: Complete attempt
    fun completeQuizAttempt() {
        val attemptId = currentAttemptId ?: return
        
        viewModelScope.launch {
            _loading.value = true
            
            val result = repository.completeAttempt(attemptId)
            
            result.onSuccess { completedAttempt ->
                _attemptState.value = completedAttempt
                _loading.value = false
                
                // Navigate to results screen
            }
            
            result.onFailure { error ->
                _error.value = "Failed to complete quiz: ${error.message}"
                _loading.value = false
            }
        }
    }
}
```

## Notes

1. **Authentication Required**: All endpoints require a valid JWT token in the Authorization header
2. **Student Role Required**: Quiz attempts are for students only
3. **Course Completion**: Some endpoints may require course completion before attempting
4. **Timestamps**: All timestamps are in ISO 8601 format
5. **IDs**: All IDs are strings (MongoDB ObjectIDs)
6. **Answer Format**: 
   - True/False questions: "true" or "false"
   - Multiple Choice: "0", "1", "2", "3" (index of selected option)

## Testing

To test the integration:

1. Start your backend server at `localhost:8080`
2. Register/login to get an auth token
3. Set the auth token: `ApiClient.setAuthToken(token)`
4. Start a quiz attempt with a valid quiz ID
5. Submit answers for each question
6. Complete the attempt
7. View results and XP earned

## Troubleshooting

**Problem**: Connection refused
- **Solution**: Make sure backend is running on port 8080
- For emulator, use `10.0.2.2` instead of `localhost`
- For physical device, use your computer's IP address

**Problem**: 401 Unauthorized
- **Solution**: Token expired or not set. Login again and set the new token

**Problem**: 404 Not Found
- **Solution**: Check if the quiz ID exists and is approved

**Problem**: 409 Conflict
- **Solution**: You may have already attempted this quiz
