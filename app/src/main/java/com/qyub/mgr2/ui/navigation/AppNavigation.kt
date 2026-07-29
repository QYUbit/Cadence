package com.qyub.mgr2.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qyub.mgr2.LocalAppDrawerState
import com.qyub.mgr2.LocalDrawerGestureStartX
import com.qyub.mgr2.ui.components.AppDrawer
import com.qyub.mgr2.ui.screens.calendar.CalendarScreen
import com.qyub.mgr2.ui.screens.inbox.InboxScreen
import com.qyub.mgr2.ui.screens.settings.SettingsScreen
import com.qyub.mgr2.ui.screens.timeline.TimelineScreen
import com.qyub.mgr2.ui.screens.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val gestureStartX = remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    CompositionLocalProvider(
        LocalDrawerGestureStartX provides gestureStartX,
        LocalAppDrawerState provides drawerState
    ) {
        Box(
           modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colorScheme.background)
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawer(
                        currentRoute = currentRoute,
                        onRouteSelected = { route ->
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.Timeline.fullRoute,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawerEdgeGesture(drawerState, scope = scope)
                ) {
                    composable(
                        route = NavigationRoutes.Timeline.fullRoute,
                        arguments = listOf(navArgument("day") {
                            type = NavType.StringType
                            defaultValue = LocalDate.now().toString()
                            nullable = true
                        })
                    ) { backStackEntry ->
                        val dayString = backStackEntry.arguments?.getString("day")
                        val day = LocalDate.parse(dayString)

                        TimelineScreen(
                            startDay = day,
                            onMenuRequest = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable(NavigationRoutes.Calendar.fullRoute) {
                        CalendarScreen(
                            onMenuRequest = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            onDateClick = { date ->
                                navController.navigate("timeline?day=$date")
                            }
                        )
                    }

                    composable(NavigationRoutes.Inbox.fullRoute) {
                        InboxScreen(
                            onMenuRequest = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                        )
                    }

                    composable(NavigationRoutes.Settings.fullRoute) {
                        SettingsScreen(
                            onMenuRequest = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.drawerEdgeGesture(
    drawerState: DrawerState,
    edgeWidthDp: Dp = 20.dp,
    scope: CoroutineScope
): Modifier = this.pointerInput(drawerState) {
    val edgeWidthPx = with(density) { edgeWidthDp.toPx() }

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)

        if (down.position.x > edgeWidthPx) return@awaitEachGesture

        val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
            change.consume()
        } ?: return@awaitEachGesture

        val dx = drag.position.x - down.position.x
        if (dx > 0f) {
            scope.launch { drawerState.open() }
        }
    }
}

