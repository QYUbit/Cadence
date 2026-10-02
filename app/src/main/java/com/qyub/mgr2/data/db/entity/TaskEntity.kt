package com.qyub.mgr2.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay

@Entity(tableName = "events")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Not represented in domain Task for now
    val exceptionParentId: Long? = null,

    val title: String,
    val notes: String?,
    val color: Int,

    val rangeStart: Long? = null,
    val rangeEnd: Long? = null,

    val occurrenceType: String? = null,
    val fixedDate: Long? = null,
    val weeklyDaysMask: Int? = null,
    val dayOfMonth: Int? = null,
    val monthOfYear: Int? = null,
    val dayOfYearMonth: Int? = null,

    val startTime: Int? = null,
    val duration: Long?
)

fun Task.toEntity(): TaskEntity {
    when (this) {
        is Task.Scheduled -> return TaskEntity(
            id = id,
            title = title,
            notes = notes,
            color = color,
            duration = duration?.toMinutes(),
            rangeStart = rangeStart?.toEpochDay(),
            rangeEnd = rangeEnd?.toEpochDay(),
            occurrenceType = when(occurrence) {
                is Occurrence.FixedDate -> "FixedDate"
                is Occurrence.Daily -> "Daily"
                is Occurrence.Weekly -> "Weekly"
                is Occurrence.Monthly -> "Monthly"
                is Occurrence.Yearly -> "Yearly"
            },
            fixedDate = if (occurrence is Occurrence.FixedDate)
                occurrence.date.toEpochDay() else null,
            weeklyDaysMask = if (occurrence is Occurrence.Weekly)
                occurrence.weekDays.toBitMask() else null,
            dayOfMonth = if (occurrence is Occurrence.Monthly)
                occurrence.monthDate else null,
            monthOfYear = if (occurrence is Occurrence.Yearly)
                occurrence.yearDate.month.value else null,
            dayOfYearMonth = if (occurrence is Occurrence.Yearly)
                occurrence.yearDate.dayOfMonth else null,
            startTime = startTime?.toSecondOfDay(),
        )

        is Task.Unscheduled -> return TaskEntity(
            id = id,
            title = title,
            notes = notes,
            color = color,
            duration = duration?.toMinutes(),
        )
    }
}

fun TaskEntity.toDomain(): Task {
    if (occurrenceType == null) {
        return Task.Unscheduled(
            id = id,
            title = title,
            notes = notes,
            color = color,
            duration = if (duration != null)
                Duration.ofMinutes(duration) else null,
        )
    }

    return Task.Scheduled(
        id = id,
        title = title,
        notes = notes,
        color = color,
        duration = if (duration != null)
            Duration.ofMinutes(duration) else null,
        rangeStart = if (rangeStart != null)
            LocalDate.ofEpochDay(rangeStart) else null,
        rangeEnd = if (rangeEnd != null)
            LocalDate.ofEpochDay(rangeEnd) else null,
        startTime = if (startTime != null)
            LocalTime.ofSecondOfDay(startTime.toLong()) else null,
        occurrence = when (occurrenceType) {
            "FixedDate" -> Occurrence.FixedDate(LocalDate.ofEpochDay(fixedDate!!))
            "Daily" -> Occurrence.Daily
            "Weekly" -> Occurrence.Weekly(weeklyDaysMask!!.toDayOfWeekSet())
            "Monthly" -> Occurrence.Monthly(dayOfMonth!!)
            "Yearly" -> Occurrence.Yearly(
                MonthDay.of(monthOfYear!!, dayOfYearMonth!!))
            else -> throw IllegalStateException("Occurrence type $occurrenceType not supported")
        }
    )
}

private fun Set<DayOfWeek>.toBitMask(): Int =
    fold(0) { mask, day ->
        mask or (1 shl (day.ordinal))
    }

private fun Int.toDayOfWeekSet(): Set<DayOfWeek> =
    DayOfWeek.entries.filterTo(mutableSetOf()) { day ->
        this and (1 shl day.ordinal) != 0
    }
