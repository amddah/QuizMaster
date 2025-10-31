# 🎮 Gamification System - Visual Overview

## 📱 User Journey

```
┌─────────────────────────────────────────────────────────────┐
│                    QUIZ LIFECYCLE                           │
└─────────────────────────────────────────────────────────────┘

    Start Quiz
        ↓
    Answer Questions → [Track time & correctness]
        ↓
    Complete Quiz
        ↓
    ┌──────────────────┐
    │  REWARDS SCREEN  │  ← NEW!
    └──────────────────┘
        ↓
    Display:
    • Performance Tier (🏆⭐👍😊💪)
    • XP Gained (animated counter)
    • XP Breakdown (base + bonuses)
    • Level Up (if applicable)
    • New Badges (if unlocked)
        ↓
    Continue to Dashboard
```

## 🎯 XP System Flow

```
┌─────────────────────────────────────────────────────────────┐
│                   XP CALCULATION                            │
└─────────────────────────────────────────────────────────────┘

For each CORRECT answer:
    Base XP: +10 points
    
    Time-based bonus:
    ├─ < 3 seconds   → +5 XP (Fast bonus ⚡)
    ├─ 3-5 seconds   → +3 XP (Medium bonus)
    └─ > 5 seconds   → +0 XP (No bonus)

Quiz completion:
    └─ 100% score    → +20 XP (Perfect score 💯)

Total XP → Level Progress
    Every 100 XP = 1 Level
    
    Example:
    450 XP = Level 4 (50/100 to Level 5)
```

## 🏆 Badge System

```
┌─────────────────────────────────────────────────────────────┐
│                    BADGE CATEGORIES                         │
└─────────────────────────────────────────────────────────────┘

MILESTONE BADGES
├─ 🎓 First Steps    (1st quiz)         [Common]
├─ 👑 Quiz Master    (50 quizzes)       [Epic]
└─ 🌟 Category Expert (90%+ x5 in cat)  [Rare]

PERFORMANCE BADGES
├─ ⚡ Speed Demon    (10 fast answers)  [Rare]
└─ 💯 Perfect Score  (100% on quiz)     [Uncommon]

STREAK BADGES
├─ 🔥 5-Day Streak   (5 consecutive)    [Uncommon]
└─ 🔥🔥 10-Day Streak (10 consecutive)  [Rare]

LEVEL BADGES
├─ 🥉 Level 10       (reach lvl 10)     [Uncommon]
├─ 🥈 Level 25       (reach lvl 25)     [Rare]
└─ 🥇 Level 50       (reach lvl 50)     [Legendary]
```

## 📊 Profile Layout Structure

```
┌─────────────────────────────────────────────────────────────┐
│  GAMIFICATION PROFILE                                       │
└─────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  ┌─────┐  John Doe                                        │
│  │ JD  │  john.doe@example.com                           │
│  └─────┘                                                   │
└───────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  Level 15                              450 / 500 XP       │
│  ████████████████░░░░ 90%                                │
│  50 XP to Level 16                                        │
└───────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  STATISTICS                                               │
│  ┌─────────────┬─────────────┐                          │
│  │     42      │     523     │                          │
│  │  Completed  │  Questions  │                          │
│  └─────────────┴─────────────┘                          │
│  ┌─────────────┬─────────────┐                          │
│  │    87%      │    84%      │                          │
│  │  Accuracy   │  Avg Score  │                          │
│  └─────────────┴─────────────┘                          │
│  ┌─────────────┬─────────────┐                          │
│  │    #15      │      7      │                          │
│  │  Rank       │   Badges    │                          │
│  └─────────────┴─────────────┘                          │
└───────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  CURRENT STREAK                             🔥            │
│  7 days                                                   │
│  Longest Streak: 15 days                                 │
│  Don't break your streak! Complete a quiz today.         │
└───────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  BADGES                                     7 / 10        │
│  ┌────┐ ┌────┐ ┌────┐                                   │
│  │ 🎓 │ │ 💯 │ │ 🔥 │                                   │
│  │1st │ │Perf│ │5-D │                                   │
│  └────┘ └────┘ └────┘                                   │
│  ┌────┐ ┌────┐ ┌────┐                                   │
│  │ ⚡ │ │ 🌟 │ │ 🥉 │                                   │
│  │Sped│ │Expt│ │L10 │                                   │
│  └────┘ └────┘ └────┘                                   │
│  ┌────┐                                                   │
│  │ 👑 │                                                   │
│  │Mstr│                                                   │
│  └────┘                                                   │
└───────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  CATEGORY PERFORMANCE                                     │
│  Programming      🌟 Expert        92%  12 quizzes       │
│  Mathematics                       85%   8 quizzes       │
│  Science                           78%   5 quizzes       │
└───────────────────────────────────────────────────────────┘
```

## 🏅 Leaderboard Layout

```
┌─────────────────────────────────────────────────────────────┐
│  GLOBAL LEADERBOARD                                         │
└─────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  YOUR RANK                                                │
│                                                           │
│  #15        1,450 XP      Top 5%                        │
│                                                           │
│  Better than 142 students                                │
└───────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────┐
│  [  Global  ] [  Quiz   ]                                │
└───────────────────────────────────────────────────────────┘

TOP 3 PODIUM:
┌───────────────────────────────────────────────────────────┐
│       🥈              🥇              🥉                   │
│    Sarah Lee      John Doe        Mike Chen               │
│    2,100 XP       2,450 XP       1,890 XP                │
│    ████████       ████████████   ██████                   │
└───────────────────────────────────────────────────────────┘

ALL RANKINGS:
┌───────────────────────────────────────────────────────────┐
│  4  Emily Wang        Lvl 22 • 3 badges    1,750 XP 89% │
├───────────────────────────────────────────────────────────┤
│  5  Alex Kim          Lvl 20 • 5 badges    1,680 XP 85% │
├───────────────────────────────────────────────────────────┤
│  6  Lisa Park         Lvl 19 • 4 badges    1,520 XP 82% │
├───────────────────────────────────────────────────────────┤
│  ... (continues)                                          │
└───────────────────────────────────────────────────────────┘
```

## 🎁 Rewards Screen Flow

```
┌─────────────────────────────────────────────────────────────┐
│  QUIZ REWARDS                                               │
└─────────────────────────────────────────────────────────────┘

STEP 1: Performance Tier
┌───────────────────────────────────────────────────────────┐
│  ⭐  Excellent work!                                      │
│      87%                                                  │
└───────────────────────────────────────────────────────────┘

STEP 2: XP Earned (Animated Counter)
┌───────────────────────────────────────────────────────────┐
│            XP EARNED                                      │
│            +125 XP                                        │
│                                                           │
│  Correct Answers          +80 XP                        │
│  ⚡ Speed Bonus           +25 XP                        │
│  💯 Perfect Score          +20 XP                        │
└───────────────────────────────────────────────────────────┘

STEP 3: Level Up (if applicable)
┌───────────────────────────────────────────────────────────┐
│                   🎉                                       │
│              LEVEL UP!                                     │
│        You reached Level 15!                              │
└───────────────────────────────────────────────────────────┘

STEP 4: New Badges (if unlocked)
┌───────────────────────────────────────────────────────────┐
│  🏆 NEW BADGES UNLOCKED!                                 │
│  ┌────┐ ┌────┐                                           │
│  │ 💯 │ │ ⚡ │                                           │
│  │Perf│ │Sped│                                           │
│  └────┘ └────┘                                           │
└───────────────────────────────────────────────────────────┘

STEP 5: Actions
┌───────────────────────────────────────────────────────────┐
│  [        Continue         ]                              │
│  [      Share Results      ]                              │
└───────────────────────────────────────────────────────────┘
```

## 🔔 Notification Examples

```
┌─────────────────────────────────────────────────────────────┐
│  BADGE UNLOCK NOTIFICATION                                  │
└─────────────────────────────────────────────────────────────┘

QuizMaster                                            now
🏆 Badge Unlocked!
Perfect Score: Get 100% on any quiz
[Tap to view your achievements]


┌─────────────────────────────────────────────────────────────┐
│  LEVEL UP NOTIFICATION                                      │
└─────────────────────────────────────────────────────────────┘

QuizMaster                                            now
🎉 Level Up! You're now Level 15
You earned 125 XP and reached a new level!
[Tap to view your profile]


┌─────────────────────────────────────────────────────────────┐
│  STREAK REMINDER NOTIFICATION                               │
└─────────────────────────────────────────────────────────────┘

QuizMaster                                          8:00 AM
🔥 Don't break your streak!
You're on a 7-day streak. Complete a quiz today!
[Tap to take a quiz]
```

## 📈 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    DATA FLOW                                │
└─────────────────────────────────────────────────────────────┘

    User completes quiz
            ↓
    Backend calculates:
    • XP earned (base + bonuses)
    • Check badge criteria
    • Update level
    • Update leaderboard
    • Update streak
            ↓
    App receives:
    • XpGainResponse
    • New badges (if any)
    • Updated stats
            ↓
    Display Rewards Screen
            ↓
    Send notifications (if badges/level up)
            ↓
    Update cached data
            ↓
    Refresh profile/leaderboard
```

## 🎨 Color Coding

```
Performance Tiers:
    🏆 Excellent     → Gold    (#FFD700)
    ⭐ Great         → Green   (#4CAF50)
    👍 Good          → Blue    (#2196F3)
    😊 Average       → Orange  (#FF9800)
    💪 Needs Improve → Red     (#F44336)

Badge Rarities:
    Common           → Gray    (#9E9E9E)
    Uncommon         → Green   (#4CAF50)
    Rare             → Blue    (#2196F3)
    Epic             → Purple  (#9C27B0)
    Legendary        → Gold    (#FFD700)
```

## 🔄 State Management

```
Profile Screen States:
    [Loading] → Show spinner
    [Loaded]  → Display data
    [Error]   → Show error message + retry
    [Empty]   → Show empty state

Leaderboard States:
    [Loading] → Show spinner
    [Loaded]  → Display rankings
    [Error]   → Show error message + retry
    [Empty]   → "No rankings yet"

Rewards Screen States:
    [Loading]    → Show spinner
    [Animating]  → Play XP/badge animations
    [Complete]   → Show continue button
```

---

## 📝 Quick Reference

### XP Values
- Base: 10 XP
- Fast bonus: +5 XP
- Medium bonus: +3 XP
- Perfect: +20 XP
- Level: 100 XP each

### Badge Count
- Total: 10 badges
- Common: 1
- Uncommon: 3
- Rare: 4
- Epic: 1
- Legendary: 1

### Performance Ranges
- Excellent: 90-100%
- Great: 75-89%
- Good: 60-74%
- Average: 50-59%
- Needs Work: 0-49%

---

This visual overview provides a quick reference for understanding the gamification system structure and user experience flow.
