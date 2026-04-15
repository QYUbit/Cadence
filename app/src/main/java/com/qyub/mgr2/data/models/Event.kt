package com.qyub.mgr2.data.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
@Entity(
    tableName = "events",
    indices = [Index(value = ["exceptionParentId"])]
)
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val title: String,
    val description: String? = null,

    val isInbox: Boolean = false,

    val isRepeating: Boolean = false,
    val repeatFor: RepeatType = RepeatType.NONE,
    val repeatAt: List<Int>? = null,

    val date: LocalDate? = null,

    val isAllDay: Boolean = false,
    val startTime: LocalTime? = null,
    val duration: Int? = null,

    val isException: Boolean = false,
    val exceptionParentId: Long? = null,

    val color: Color? = null,

    val hasNotification: Boolean = true,
    val notificationMinutes: Int = 15,
    val notificationType: NotificationType = NotificationType.REMINDER, // Unused for now

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun Event.isActiveAtDate(targetDate: LocalDate = LocalDate.now()): Boolean {
    if (!isRepeating) return date == targetDate

    return when (repeatFor) {
        RepeatType.DAILY -> true

        RepeatType.WEEK_DAY -> {
            val todayIndex = targetDate.dayOfWeek.value - 1
            repeatAt?.contains(todayIndex) == true
        }

        RepeatType.MONTH_DAY -> {
            val todayDay = targetDate.dayOfMonth
            repeatAt?.contains(todayDay) == true
        }

        RepeatType.YEAR_DAY -> {
            val month = targetDate.monthValue
            val day = targetDate.dayOfMonth
            repeatAt?.let { it.size == 2 && it[0] == month && it[1] == day } == true
        }

        RepeatType.NONE -> false
    }
}