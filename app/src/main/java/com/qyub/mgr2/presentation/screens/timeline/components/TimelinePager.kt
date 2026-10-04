package com.qyub.mgr2.presentation.screens.timeline.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qyub.mgr2.presentation.screens.timeline.EventUIState
import com.qyub.mgr2.presentation.screens.timeline.TimelineUIState
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

private val ANCHOR_DATE = LocalDate.of(2000, 1, 1)

@Composable
fun TimelinePager(
    modifier: Modifier = Modifier,
    uiState: TimelineUIState,
    onDayChange: (LocalDate) -> Unit = {},
    onEventClick: (EventUIState) -> Unit = {}
) {
    val initialPage = remember { pageForDay(uiState.displayDay) }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    // Shared scroll state for all pages
    val scrollState = rememberScrollState()

    // Sync pager with external day changes
    LaunchedEffect(uiState.displayDay) {
        val targetPage = pageForDay(uiState.displayDay)
        if (pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    // Sync external day with pager swipes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onDayChange(dayForPage(page))
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    ) { page ->
        val day = dayForPage(page)
        val allDayEvents = uiState.allDayEvents[day] ?: emptyList()

        Column {
            if (allDayEvents.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    allDayEvents.forEach { task ->
                        AllDayEventCard(
                            task = task,
                            onClick = {
                                // TODO This can be cleaner
                                val eventUIState = EventUIState(
                                    id = task.id,
                                    taskRef = task,
                                    color = Color(task.color),
                                    startTime = LocalTime.MIN,
                                    endTime = LocalTime.MAX,
                                    top = 0,
                                    left = 0f,
                                    width = 1f,
                                    height = 0
                                )
                                onEventClick(eventUIState)
                            }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }

            Timeline(
                events = uiState.events[day] ?: emptyList(),
                day = day,
                onEventClick = onEventClick,
                scrollState = scrollState
            )
        }
    }
}

fun pageForDay(day: LocalDate): Int =
    Int.MAX_VALUE / 2 + ChronoUnit.DAYS.between(ANCHOR_DATE, day).toInt()

fun dayForPage(page: Int): LocalDate =
    ANCHOR_DATE.plusDays((page - Int.MAX_VALUE / 2).toLong())
