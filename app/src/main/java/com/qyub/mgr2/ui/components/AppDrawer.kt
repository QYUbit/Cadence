package com.qyub.mgr2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qyub.mgr2.ui.navigation.NavigationRoutes

@Composable
fun AppDrawer(
    currentRoute: String?,
    onRouteSelected: (NavigationRoutes) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Text(
                text = "Cadence",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            NavigationRoutes.allRoutes.forEach { route ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = route.icon,
                            contentDescription = route.title
                        )
                    },
                    label = { Text(route.title) },
                    selected = currentRoute?.contains(route.route) ?: false,
                    onClick = { onRouteSelected(route) },
                    modifier = Modifier.padding(vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}