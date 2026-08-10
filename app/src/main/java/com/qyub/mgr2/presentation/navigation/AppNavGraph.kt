package com.qyub.mgr2.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.qyub.mgr2.presentation.screens.timeline.TimelineScreen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph() {
    val scope = rememberCoroutineScope()

    val backStack = rememberSaveable { mutableStateListOf<AppDestination>(Timeline) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    currentDestination = backStack.last(),
                    onDestinationSelected = { destination ->
                        navigateToTab(backStack, destination)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is Timeline -> NavEntry(key) {
                            TimelineScreen(
                                onOpenMenu = { scope.launch { drawerState.open() } }
                            )
                        }

                        is Settings -> NavEntry(key) {

                        }
                    }
                }
            )
        }
    }
}

private fun navigateToTab(backStack: MutableList<AppDestination>, target: AppDestination) {
    if (backStack.lastOrNull() == target) return

    backStack.clear()
    if (target != Timeline) {
        backStack.add(Timeline)
    }
    backStack.add(target)
}