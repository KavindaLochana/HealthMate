package com.example.healthmate.core.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.R
import com.example.healthmate.feature.dashboard.DashboardScreen
import com.example.healthmate.feature.dashboard.DashboardViewModel
import com.example.healthmate.feature.editor.EditorArgs
import com.example.healthmate.feature.editor.RecordEditorScreen
import com.example.healthmate.feature.editor.RecordEditorViewModel
import com.example.healthmate.feature.history.HistoryScreen
import com.example.healthmate.feature.history.HistoryViewModel
import com.example.healthmate.feature.profile.ProfileScreen
import com.example.healthmate.feature.profile.ProfileViewModel

private enum class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    TODAY(Routes.DASHBOARD, R.string.nav_today, Icons.Filled.Today),
    HISTORY(Routes.HISTORY, R.string.nav_history, Icons.Filled.History),
    PROFILE(Routes.PROFILE, R.string.nav_profile, Icons.Filled.Person),
}

// editor and bottom navigation
@Composable
fun MainScaffold(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

    Scaffold(
        // Each screen handles its own status-bar inset; the bar handles the navigation-bar inset.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navController.navigateTopLevel(destination.route) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.DASHBOARD) {
                val viewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
                DashboardScreen(
                    viewModel = viewModel,
                    onAddRecordClick = { navController.navigate(Routes.editor()) },
                )
            }
            composable(Routes.HISTORY) {
                val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
                HistoryScreen(
                    viewModel = viewModel,
                    onAddRecord = { date -> navController.navigate(Routes.editor(date = date)) },
                    onEditRecord = { record -> navController.navigate(Routes.editor(recordId = record.id)) },
                )
            }
            composable(Routes.PROFILE) {
                val viewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
                ProfileScreen(viewModel = viewModel)
            }
            composable(
                route = Routes.EDITOR_PATTERN,
                arguments = listOf(
                    navArgument(EditorArgs.RECORD_ID) {
                        type = NavType.LongType
                        defaultValue = EditorArgs.NO_RECORD
                    },
                    navArgument(EditorArgs.DATE) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                val viewModel: RecordEditorViewModel = viewModel(factory = viewModelFactory)
                RecordEditorScreen(
                    viewModel = viewModel,
                    onClose = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
