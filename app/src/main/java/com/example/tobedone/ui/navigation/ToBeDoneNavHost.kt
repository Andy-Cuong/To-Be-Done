package com.example.tobedone.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tobedone.ui.screen.AddNoteDestination
import com.example.tobedone.ui.screen.AddNoteScreen
import com.example.tobedone.ui.screen.EditNoteDestination
import com.example.tobedone.ui.screen.EditNoteScreen
import com.example.tobedone.ui.screen.HomeDestination
import com.example.tobedone.ui.screen.HomeScreen

@Composable
fun ToBeDoneNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navHostController,
        startDestination = HomeDestination.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(durationMillis = 800)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(durationMillis = 800)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(durationMillis = 800)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(durationMillis = 800)
            )
        }
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onAddButtonClicked = { navHostController.navigate(AddNoteDestination.route) },
                onTextNoteClicked = { textNote ->
                    navHostController.navigate("${EditNoteDestination.route}/${textNote.id}")
                }
            )
        }
        composable(route = AddNoteDestination.route) {
            AddNoteScreen(
                onNavigateUp = { navHostController.navigateUp() },
                onNavigateBack = { navHostController.popBackStack() }
            )
        }
        composable(
            // Use navigation route with placeholder argument to retrieve it from SavedStateHandle
            route = EditNoteDestination.routeWithArgs,
            arguments = listOf(navArgument(name = EditNoteDestination.NOTE_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            EditNoteScreen(
                onNavigateUp = { navHostController.navigateUp() },
                onNavigateBack = { navHostController.popBackStack() }
            )
        }
    }
}