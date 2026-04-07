package com.qyub.mgr2.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qyub.mgr2.ui.components.AppDrawer
import com.qyub.mgr2.ui.screens.calendar.CalendarScreen
import com.qyub.mgr2.ui.screens.settings.SettingsScreen
import com.qyub.mgr2.ui.screens.settings.SettingsViewModel
import com.qyub.mgr2.ui.screens.timeline.TimelineScreen
import com.qyub.mgr2.ui.screens.timeline.TimelineViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
                modifier = Modifier.fillMaxSize()
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
                    val timelineViewModel: TimelineViewModel = hiltViewModel()

                    TimelineScreen(
                        vm = timelineViewModel,
                        startDay = day,
                        onMenuRequest = { scope.launch { drawerState.open() } }
                    )
                }

                composable(NavigationRoutes.Calendar.fullRoute) {
                    CalendarScreen(
                        onMenuRequest = { scope.launch {
                            drawerState.open()
                        }},
                        onDateClick = { date ->
                            navController.navigate("timeline?day=$date")
                        }
                    )
                }

                composable(NavigationRoutes.Settings.fullRoute) {
                    val settingsViewModel: SettingsViewModel = hiltViewModel()
                    SettingsScreen (
                        viewModel = settingsViewModel,
                        onMenuRequest = { scope.launch {
                            drawerState.open()
                        }},
                    )
                }
            }
        }
    }
}
