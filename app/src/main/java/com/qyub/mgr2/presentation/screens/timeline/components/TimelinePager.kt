package com.qyub.mgr2.presentation.screens.timeline.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qyub.mgr2.presentation.screens.timeline.EventUIState
import com.qyub.mgr2.presentation.screens.timeline.TimelineUIState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TimelinePager(
    modifier: Modifier = Modifier,
    uiState: TimelineUIState,
    onDayChange: (LocalDate) -> Unit = {},
    onEventClick: (EventUIState) -> Unit = {}
) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

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
            /*if (allDayEvents.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    allDayEvents.forEach { ev ->
                        Box(
                            event = ev,
                            onClick = {
                                eventToEdit = ev
                                showSheet = true
                            },
                            modifier = Modifier
                                .height(30.dp)
                                .wrapContentWidth(unbounded = true)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }*/

            Timeline(
                uiState = uiState,
                day = day,
                onEventClick = onEventClick
            )
        }
    }
}

fun pageForDay(day: LocalDate): Int =
    Int.MAX_VALUE / 2 + ChronoUnit.DAYS.between(LocalDate.now(), day).toInt()

fun dayForPage(page: Int): LocalDate =
    LocalDate.now().plusDays((page - Int.MAX_VALUE / 2).toLong())