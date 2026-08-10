package com.qyub.mgr2.domain.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Int,
    val title: String,
    val color: Color,
    val date: LocalDate,
    val startTime: LocalTime,
    val duration: Int
)