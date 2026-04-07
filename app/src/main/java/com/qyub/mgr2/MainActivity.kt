package com.qyub.mgr2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.qyub.mgr2.ui.navigation.AppNavigation
import com.qyub.mgr2.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                val isDarkTheme = isSystemInDarkTheme()

                LaunchedEffect(isDarkTheme) {
                    val controller = WindowInsetsControllerCompat(window, window.decorView)
                    controller.isAppearanceLightNavigationBars = !isDarkTheme
                    window.navigationBarColor = Color.TRANSPARENT
                }

                AppNavigation()
            }
        }
    }
}
