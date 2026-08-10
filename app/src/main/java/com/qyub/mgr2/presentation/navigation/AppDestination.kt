package com.qyub.mgr2.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination

@Serializable
data object Timeline : AppDestination

@Serializable
data object Settings : AppDestination

fun AppDestination.showDrawer(): Boolean = when(this) {
    is Timeline -> true
    is Settings -> true
}
