# 🧠 QuizMaster - Educational Gamified Quiz Application

A mobile quiz application built with Kotlin for Android, featuring role-based access control, time-based scoring, course integration, and comprehensive gamification elements.

## 📱 Features Overview

### 🎯 Core Features

#### 1. **Role-Based Access Control**
- **Two User Types:**
  - **Students:** Can take quizzes, create quiz proposals, view leaderboards
  - **Professors:** Can create quizzes (auto-approved), approve/reject student quizzes, view analytics

#### 2. **Quiz Categories & Difficulty Levels**
- **8 Categories:** General Knowledge, Science & Nature, History, Technology, Sports, Entertainment, Geography, Animals
- **3 Difficulty Levels:** Easy, Medium, Hard
- Each category supports all three difficulty levels

#### 3. **Course Integration**
- Quizzes are linked to specific courses
- External API integration to verify course completion
- **Access Control:** Students can only access quizzes if they've completed the linked course
- Real-time course completion verification

#### 4. **Quiz Structure**
- **Question Types:**
  - True/False questions
  - Multiple-choice questions (4 options)
- **Time Limit:** 15 seconds per question
- **Time-Based Scoring System:**
  - ⚡ **0-5 seconds:** 100% of max score + Fast bonus
  - 🏃 **5-10 seconds:** 70% of max score + Medium bonus
  - 🚶 **10-15 seconds:** 40% of max score
  - 🐌 **>15 seconds:** 0 points (too slow)

#### 5. **Gamification System**

##### Experience & Leveling
- Base XP per correct answer: **10 XP**
- Fast response bonus: **+5 XP**
- Medium response bonus: **+3 XP**
- Perfect score bonus: **+20 XP**
- Level progression: Each level requires 100 XP

##### Badges & Achievements
- 🎓 **First Steps** - Complete your first quiz
- ⚡ **Speed Demon** - Answer 10 questions in under 3 seconds each
- 💯 **Perfect Score** - Get 100% on any quiz
- 🔥 **5-Day Streak** - Complete quizzes for 5 days in a row
- 🔥🔥 **10-Day Streak** - Complete quizzes for 10 days in a row
- 👑 **Quiz Master** - Complete 50 quizzes
- 🌟 **Category Expert** - Get 90%+ on 5 quizzes in same category
- 🥉 **Level 10** - Reach level 10
- 🥈 **Level 25** - Reach level 25
- 🥇 **Level 50** - Reach level 50

##### Performance Tiers
- 🏆 **Excellent** (90-100%) - "Outstanding performance!"
- ⭐ **Great** (75-89%) - "Excellent work!"
- 👍 **Good** (60-74%) - "Good job!"
- 😊 **Average** (50-59%) - "Not bad!"
- 💪 **Needs Improvement** (<50%) - "Keep practicing!"

#### 6. **Leaderboard & Competition**
- Quiz-specific leaderboards
- Global leaderboard across all quizzes
- Real-time ranking updates
- Compare your performance with other students
- View:
  - Your rank
  - Top performers
  - Score percentile
  - "Better than X students" statistics

#### 7. **Quiz Approval Workflow**
- **Professor-created quizzes:** Auto-approved
- **Student-created quizzes:** 
  - Status: PENDING
  - Requires professor approval
  - Can be APPROVED or REJECTED with reason
- Professors can review quiz details before approval

## 🏗️ Architecture & Technical Implementation

### Tech Stack
- **Language:** Kotlin
- **UI:** Android Views with Material Design
- **Async:** Coroutines & Flow
- **Networking:** Retrofit + OkHttp
- **JSON:** Gson
- **Local Storage:** DataStore (for user sessions)
- **Architecture:** Repository Pattern + MVVM

### Project Structure

```
app/src/main/java/com/example/quizmaster/
├── data/
│   ├── model/                    # Data models
│   │   ├── User.kt              # User model with XP, level, badges
│   │   ├── UserRole.kt          # STUDENT, PROFESSOR enum
│   │   ├── AuthModels.kt        # Login/Register request/response
│   │   ├── QuizModel.kt         # Quiz with creator, approval, course link
│   │   ├── QuestionModel.kt     # Question with time-based scoring
│   │   ├── QuizAttempt.kt       # Student quiz attempt tracking
│   │   ├── Course.kt            # Course completion models
│   │   └── Gamification.kt      # Badges, achievements, stats
│   ├── remote/                   # API services
│   │   ├── ApiClient.kt         # Retrofit configuration
│   │   ├── AuthApiService.kt    # Authentication endpoints
│   │   ├── QuizApiService.kt    # Quiz CRUD & approval
│   │   ├── QuizAttemptApiService.kt  # Attempts & leaderboard
│   │   └── CourseApiService.kt  # Course completion check
│   ├── repository/               # Data repositories
│   │   ├── QuizManagementRepository.kt
│   │   └── LeaderboardRepository.kt
│   └── local/
│       ├── UserSessionManager.kt # DataStore session management
│       └── QuizDataStore.kt     # Offline data
├── ui/
│   ├── auth/                     # Authentication screens
│   │   ├── LoginActivity.kt     # ✅ Implemented
│   │   └── RegisterActivity.kt  # 🔲 To be implemented
│   ├── student/                  # Student-specific screens
│   │   └── StudentDashboardActivity.kt  # 🔲 Placeholder
│   ├── professor/                # Professor-specific screens
│   │   ├── ProfessorDashboardActivity.kt  # 🔲 Placeholder
│   │   └── ApprovalActivity.kt  # 🔲 Placeholder
│   ├── quiz/
│   │   ├── QuizActivity.kt      # 🔲 Needs timer update
│   │   └── QuizCreationActivity.kt  # 🔲 Placeholder
│   ├── leaderboard/
│   │   └── LeaderboardActivity.kt  # 🔲 Placeholder
│   ├── profile/
│   │   └── ProfileActivity.kt   # 🔲 Placeholder
│   └── ResultsActivity.kt       # 🔲 Needs leaderboard integration
└── utils/
    └── ScoreCalculator.kt       # ✅ Time-based scoring logic
```

## 🔐 Security & Access Control

### Authentication Flow
1. User logs in with email/password and role selection
2. Backend validates credentials
3. JWT token returned and stored in DataStore
4. Token attached to all API requests via Interceptor

### Course Access Control
```kotlin
// Before allowing quiz access:
1. Get quiz.linkedCourseId
2. Call courseApiService.checkCourseCompletion(studentId, courseId)
3. If completed = true → Allow quiz access
4. If completed = false → Show "Complete [Course Name] to unlock this quiz"
```

### Role-Based Permissions

| Feature | Student | Professor |
|---------|---------|-----------|
| Create Quiz | ✅ (Pending approval) | ✅ (Auto-approved) |
| Take Quiz | ✅ (If course completed) | ❌ |
| Approve Quizzes | ❌ | ✅ |
| View Leaderboards | ✅ | ✅ |
| View Analytics | Limited | Full |

## 📊 Scoring Algorithm

### Question Score Calculation
```kotlin
fun calculateScore(responseTime: Int, isCorrect: Boolean, maxScore: Int): Int {
    if (!isCorrect) return 0
    
    val multiplier = when {
        responseTime <= 5  -> 1.0   // 100%
        responseTime <= 10 -> 0.7   // 70%
        responseTime <= 15 -> 0.4   // 40%
        else -> 0.0                 // 0%
    }
    
    return (maxScore * multiplier).toInt()
}
```

### Experience Points Calculation
```kotlin
fun calculateXP(correctAnswers: Int, avgResponseTime: Double): Int {
    val baseXP = correctAnswers * 10
    val speedBonus = when {
        avgResponseTime <= 5  -> correctAnswers * 5
        avgResponseTime <= 10 -> correctAnswers * 3
        else -> 0
    }
    val perfectBonus = if (correctAnswers == totalQuestions) 20 else 0
    
    return baseXP + speedBonus + perfectBonus
}
```

## 🎮 User Experience Flow

### Student Journey
1. **Login** → Student Dashboard
2. **Browse Quizzes** → Filtered by completed courses
3. **Select Quiz** → Course completion verified
4. **Take Quiz:**
   - See 15-second timer per question
   - Real-time visual feedback
   - Score displayed after each answer
5. **Submit Quiz**
6. **View Results:**
   - Total score & percentage
   - Time breakdown
   - Performance tier
   - XP gained & level progress
   - New badges unlocked
   - Leaderboard position
7. **Compare Performance** → See rank among peers

### Professor Journey
1. **Login** → Professor Dashboard
2. **View Pending Quizzes** → Student submissions
3. **Review Quiz:**
   - Preview all questions
   - See student creator info
4. **Approve/Reject** → Provide feedback
5. **Create Quiz:**
   - Set category, difficulty, course link
   - Add True/False and Multiple Choice questions
   - Auto-approved upon submission
6. **View Analytics** → Quiz statistics

## 🌐 API Integration

### Base URLs (Configure in ApiClient.kt)
```kotlin
// Main API
private const val BASE_URL = "https://your-api-server.com/"

// Course API (External)
private const val COURSE_API_BASE_URL = "https://your-course-api.com/"
```

### Key Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/me` - Get current user

#### Quizzes
- `GET /api/quizzes` - Get all quizzes (with filters)
- `POST /api/quizzes` - Create quiz
- `GET /api/quizzes/pending` - Get pending approvals (Professor)
- `POST /api/quizzes/{id}/approve` - Approve quiz
- `GET /api/students/{id}/available-quizzes` - Student's available quizzes

#### Quiz Attempts
- `POST /api/quiz-attempts` - Submit attempt
- `GET /api/quizzes/{id}/leaderboard` - Get leaderboard
- `GET /api/students/{id}/rank/{quizId}` - Get student rank

#### Course Integration
- `POST /api/courses/check-completion` - Verify course completion
- `GET /api/students/{id}/completed-courses` - Get completed courses

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.9+
- Android SDK 24+
- JDK 11

### Setup Instructions

1. **Clone the Repository**
```bash
cd /Users/tamtam1/AndroidStudioProjects/MyApplication
```

2. **Configure API Endpoints**
Edit `app/src/main/java/com/example/quizmaster/data/remote/ApiClient.kt`:
```kotlin
private const val BASE_URL = "YOUR_API_URL_HERE"
private const val COURSE_API_BASE_URL = "YOUR_COURSE_API_URL_HERE"
```

3. **Build the Project**
```bash
./gradlew build
```

4. **Run on Device/Emulator**
```bash
./gradlew installDebug
```

### Current Implementation Status

✅ **Completed:**
- User models (Student, Professor with roles)
- Quiz models with time-based scoring
- Question models (True/False, Multiple Choice)
- Course integration models
- Gamification system (badges, XP, levels)
- All API service interfaces
- Repository pattern implementation
- Score calculator utility
- Leaderboard comparison logic
- User session management
- LoginActivity with complete implementation
- Basic placeholder activities
- String resources
- Theme configuration
- AndroidManifest setup

🔲 **To Be Implemented:**
- RegisterActivity UI
- Student Dashboard UI
- Professor Dashboard UI
- Quiz Creation UI
- Enhanced QuizActivity with timer
- Results Activity with leaderboard
- Approval Activity UI
- Leaderboard Activity UI
- Profile Activity UI
- Backend API integration
- Unit tests
- UI tests

## 📝 Implementation Guide

Refer to `IMPLEMENTATION_GUIDE.md` for detailed instructions on:
- Completing each Activity
- UI/UX design guidelines
- API integration steps
- Testing procedures
- Deployment checklist

## 🎨 Design System

### Colors
- **Primary:** #4CAF50 (Green)
- **Primary Dark:** #388E3C
- **Primary Light:** #E8F5E8
- **Accent:** #FFD700 (Gold)
- **Error:** #F44336
- **Success:** #4CAF50
- **Text Dark:** #2E2E2E
- **Text Light:** #666666

### Typography
- **Titles:** Bold, 24-32sp
- **Subtitles:** Regular, 18-20sp
- **Body:** Regular, 14-16sp
- **Caption:** Light, 12-14sp

## 📄 License

[Add your license here]

## 👥 Contributors

[Add contributors here]

## 📧 Support

For issues and questions, please [create an issue](link-to-issues).

---

**Happy Quizzing! 🧠✨**
