package com.example.mediaplayer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mediaplayer.ui.screens.PlayListScreen
import com.example.mediaplayer.ui.screens.SongDetailScreen
import com.example.mediaplayer.ui.screens.SongListScreen

@Composable
fun MediaPlayerNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TrackListDest.route,
        modifier = modifier
    ) {
        composable(route = TrackListDest.route) {
            SongListScreen {
                navController.navigateToTrackDetail(it)
            }
        }
        composable(
            route = TrackDetailDest.routeWithArgs,
            arguments = TrackDetailDest.arguments,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ) { navBackStackEntry ->
            val songId = navBackStackEntry.arguments?.getLong(TrackDetailDest.ID_ARGS)
            if (songId != null) {
                SongDetailScreen(songId = songId, onBack = {
                    navController.navigateUp()
                }, onNavigateToPlayListScreen = {
                    navController.navigateToPlayList(it, false)
                })
            }
        }
        composable(
            route = PlayListDest.routeWithArgs,
            arguments = PlayListDest.arguments,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ){navBackStackEntry->
            val songId = navBackStackEntry.arguments?.getLong(PlayListDest.ID_ARGS)
            if (songId != null){
                PlayListScreen(songId){
                    navController.navigateUp()
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {

        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

private fun NavHostController.navigateToTrackDetail(songId: Long) {
    this.navigateSingleTopTo("${TrackDetailDest.route}/$songId")
}

private fun NavHostController.navigateToPlayList(songId: Long, canModify: Boolean) {
    this.navigateSingleTopTo("${PlayListDest.route}/$songId/$canModify")
}