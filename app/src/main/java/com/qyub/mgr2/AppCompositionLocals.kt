package com.qyub.mgr2

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf

val LocalAppDrawerState = compositionLocalOf<DrawerState> {
    error("No DrawerState provided")
}

val LocalDrawerGestureStartX = compositionLocalOf {
    mutableFloatStateOf(0f)
}