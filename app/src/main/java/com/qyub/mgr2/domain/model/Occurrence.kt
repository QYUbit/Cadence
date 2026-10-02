package com.qyub.mgr2.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay

sealed interface Occurrence {
    data class FixedDate(
        val date: LocalDate
    ) : Occurrence

    data object Daily : Occurrence

    data class Weekly(
        val weekDays: Set<DayOfWeek>
    ) : Occurrence

    data class Monthly(
        val monthDate: Int
    ) : Occurrence

    data class Yearly(
        val yearDate: MonthDay
    ) : Occurrence
}

fun Occurrence.appliesToDate(currentDate: LocalDate): Boolean {
    return when (this) {
        is Occurrence.FixedDate -> date == currentDate
        is Occurrence.Daily -> true
        is Occurrence.Weekly -> weekDays.contains(currentDate.dayOfWeek)
        is Occurrence.Monthly -> monthDate == currentDate.dayOfMonth
        is Occurrence.Yearly -> yearDate.month == currentDate.month
                && yearDate.dayOfMonth == currentDate.dayOfMonth
    }
}