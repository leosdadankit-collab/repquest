package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class BottomNavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "bottom_nav_home"),
    WORKOUTS("workouts", "Workouts", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "bottom_nav_workouts"),
    PROGRESS("progress", "Progress", Icons.Filled.BarChart, Icons.Outlined.BarChart, "bottom_nav_progress"),
    REWARDS("rewards", "Rewards", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "bottom_nav_rewards"),
    PROFILE("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person, "bottom_nav_profile")
}

@Composable
fun RepQuestBottomNav(
    currentRoute: String,
    onNavigateToTab: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("repquest_bottom_navigation"),
        containerColor = SurfaceCard,
        contentColor = TextPrimary
    ) {
        BottomNavTab.entries.forEach { tab ->
            val isSelected = currentRoute == tab.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextPrimary,
                    selectedTextColor = ElectricCyan,
                    indicatorColor = NeonViolet,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
