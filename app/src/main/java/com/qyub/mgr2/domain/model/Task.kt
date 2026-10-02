package com.qyub.mgr2.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

sealed interface Task {
    val id: Int
    val title: String
    val notes: String?
    val color: Int
    val duration: Duration?


    data class Unscheduled(
        override val id: Int,
        override val title: String,
        override val notes: String?,
        override val color: Int,
        override val duration: Duration?
    ) : Task

    data class Scheduled(
        override val id: Int,
        override val title: String,
        override val notes: String?,
        override val color: Int,
        override val duration: Duration?,

        val occurrence: Occurrence,
        val rangeStart: LocalDate?,
        val rangeEnd: LocalDate?,
        val startTime: LocalTime?
    ) : Task
}
