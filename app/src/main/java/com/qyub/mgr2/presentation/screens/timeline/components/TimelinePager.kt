package com.qyub.mgr2.presentation.screens.timeline.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qyub.mgr2.presentation.screens.timeline.EventUIState
import com.qyub.mgr2.presentation.screens.timeline.TimelineUIState
import java.time.LocalDate
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

        Column {
            Timeline(
                uiState = uiState.copy(
                    events = uiState.events.filter { it.eventRef.date == day }
                ),
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