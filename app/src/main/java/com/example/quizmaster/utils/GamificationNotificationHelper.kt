package com.example.quizmaster.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quizmaster.R
import com.example.quizmaster.data.model.Achievement
import com.example.quizmaster.ui.profile.GamificationProfileActivity

/**
 * Helper class for showing gamification notifications
 */
class GamificationNotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID_ACHIEVEMENTS = "achievements_channel"
        private const val CHANNEL_ID_LEVEL_UP = "level_up_channel"
        private const val CHANNEL_ID_STREAK = "streak_channel"
        
        private const val NOTIFICATION_ID_ACHIEVEMENT = 1001
        private const val NOTIFICATION_ID_LEVEL_UP = 1002
        private const val NOTIFICATION_ID_STREAK = 1003
    }

    init {
        createNotificationChannels()
    }

    /**
     * Create notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Achievements Channel
            val achievementsChannel = NotificationChannel(
                CHANNEL_ID_ACHIEVEMENTS,
                "Achievements & Badges",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for earned badges and achievements"
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
            }

            // Level Up Channel
            val levelUpChannel = NotificationChannel(
                CHANNEL_ID_LEVEL_UP,
                "Level Progress",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you level up"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }

            // Streak Channel
            val streakChannel = NotificationChannel(
                CHANNEL_ID_STREAK,
                "Streak Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to maintain your streak"
                enableLights(true)
                lightColor = Color.RED
            }

            notificationManager.createNotificationChannel(achievementsChannel)
            notificationManager.createNotificationChannel(levelUpChannel)
            notificationManager.createNotificationChannel(streakChannel)
        }
    }

    /**
     * Show notification for new badge/achievement
     */
    fun showBadgeUnlockedNotification(achievement: Achievement) {
        val intent = Intent(context, GamificationProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${achievement.badgeType.getEmoji()} Badge Unlocked!")
            .setContentText("${achievement.badgeType.getDisplayName()}: ${achievement.badgeType.getDescription()}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${achievement.badgeType.getDisplayName()}: ${achievement.badgeType.getDescription()}")
            )
            .build()

        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_ACHIEVEMENT + achievement.id.hashCode(),
            notification
        )
    }

    /**
     * Show notification for level up
     */
    fun showLevelUpNotification(newLevel: Int, xpGained: Int) {
        val intent = Intent(context, GamificationProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_LEVEL_UP)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🎉 Level Up! You're now Level $newLevel")
            .setContentText("You earned $xpGained XP and reached a new level!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Congratulations! You've leveled up to Level $newLevel. Keep up the great work!")
            )
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_LEVEL_UP, notification)
    }

    /**
     * Show notification for streak reminder
     */
    fun showStreakReminderNotification(currentStreak: Int) {
        val intent = Intent(context, GamificationProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_STREAK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔥 Don't break your streak!")
            .setContentText("You're on a $currentStreak-day streak. Complete a quiz today to keep it going!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_STREAK, notification)
    }

    /**
     * Show notification for multiple badges unlocked
     */
    fun showMultipleBadgesNotification(badgeCount: Int, achievements: List<Achievement>) {
        val intent = Intent(context, GamificationProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val badgesList = achievements.take(3).joinToString(", ") {
            "${it.badgeType.getEmoji()} ${it.badgeType.getDisplayName()}"
        }

        val moreText = if (badgeCount > 3) " and ${badgeCount - 3} more!" else ""

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏆 $badgeCount Badges Unlocked!")
            .setContentText("You unlocked: $badgesList$moreText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("You unlocked: $badgesList$moreText\n\nTap to view all your achievements!")
            )
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ACHIEVEMENT, notification)
    }

    /**
     * Show notification for reaching a milestone
     */
    fun showMilestoneNotification(title: String, message: String) {
        val intent = Intent(context, GamificationProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_ACHIEVEMENT + title.hashCode(),
            notification
        )
    }
}
