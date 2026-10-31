package com.ffcommunity.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ffcommunity.app.ui.navigation.FFBottomNavigation
import com.ffcommunity.app.ui.navigation.Screen
import com.ffcommunity.app.ui.screens.*
import com.ffcommunity.app.ui.theme.FFBlack
import com.ffcommunity.app.ui.theme.FFCommunityTheme
import com.ffcommunity.app.ui.viewmodel.FFViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FFViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FFCommunityTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

                Scaffold(
                    containerColor = FFBlack,
                    bottomBar = {
                        if (currentRoute != Screen.Auth.route) {
                            FFBottomNavigation(
                                navController = navController,
                                currentRoute = currentRoute,
                                unreadNotificationCount = unreadCount
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Directory.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Directory.route) {
                            DirectoryScreen(
                                viewModel = viewModel,
                                onNavigateToProfile = { userId ->
                                    navController.navigate(Screen.Profile.route)
                                }
                            )
                        }
                        composable(Screen.SquadFinder.route) {
                            SquadFinderScreen(viewModel = viewModel)
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(viewModel = viewModel)
                        }
                        composable(Screen.Notifications.route) {
                            NotificationsScreen(viewModel = viewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = viewModel,
                                onLogout = {
                                    navController.navigate(Screen.Auth.route) {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }
                        composable(Screen.Auth.route) {
                            AuthScreen(
                                onAuthSuccess = {
                                    navController.navigate(Screen.Directory.route) {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
