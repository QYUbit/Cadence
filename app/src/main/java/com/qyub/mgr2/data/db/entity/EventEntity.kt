package com.qyub.mgr2.data.db.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qyub.mgr2.domain.model.Event
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val color: Int,
    val date: Long,
    val startTime: Int,
    val duration: Int
)

fun EventEntity.toEvent() = Event(
    id = id,
    title = title,
    color = Color(color),
    date = LocalDate.ofEpochDay(date),
    startTime = LocalTime.ofSecondOfDay(startTime.toLong()),
    duration = duration
)

fun Event.toEntity() = EventEntity(
    id = id,
    title = title,
    color = color.toArgb(),
    date = date.toEpochDay(),
    startTime = startTime.toSecondOfDay(),
    duration = duration
)
