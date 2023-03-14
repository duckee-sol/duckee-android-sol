/*
 * Copyright 2023 The Duckee Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.duckee.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import xyz.duckee.android.core.designsystem.DuckeeBottomTab
import xyz.duckee.android.core.designsystem.foundation.drawColoredShadow
import xyz.duckee.android.core.navigation.ExploreDirections
import xyz.duckee.android.core.navigation.collectionNavigationRoute
import xyz.duckee.android.core.navigation.exploreNavigationRoute
import xyz.duckee.android.core.navigation.navigateToCollectionTab
import xyz.duckee.android.core.navigation.navigateToDetailScreen
import xyz.duckee.android.core.navigation.navigateToExploreTab
import xyz.duckee.android.core.navigation.navigateToRecipeResultMetadataScreen
import xyz.duckee.android.core.navigation.navigateToRecipeResultScreen
import xyz.duckee.android.core.navigation.navigateToRecipeScreen
import xyz.duckee.android.core.navigation.navigateToRecipeSuccessScreen
import xyz.duckee.android.core.navigation.navigateToRecipeTab
import xyz.duckee.android.core.navigation.navigateToSignInScreen
import xyz.duckee.android.core.navigation.recipeNavigationRoute
import xyz.duckee.android.feature.collection.navigation.collectionScreen
import xyz.duckee.android.feature.detail.navigation.detailScreen
import xyz.duckee.android.feature.explore.navigation.exploreScreen
import xyz.duckee.android.feature.recipe.navigation.recipeScreen
import xyz.duckee.android.feature.signin.navigation.signInScreen

private val bottomNavigationShowRoutes = listOf(
    exploreNavigationRoute,
    recipeNavigationRoute,
    collectionNavigationRoute,
)

@Composable
fun DuckeeApp(
    isAuthenticated: suspend () -> Boolean,
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(initialValue = null)
    val currentRoute by remember(navBackStackEntry) {
        derivedStateOf { navBackStackEntry?.destination?.route }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090A)),
    ) {
        MaterialMotionNavHost(
            navController = navController,
            startDestination = ExploreDirections.main.destination,
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) },
            modifier = Modifier.zIndex(1f),
        ) {
            exploreScreen(
                goSignInScreen = navController::navigateToSignInScreen,
                goDetailScreen = navController::navigateToDetailScreen,
            )
            signInScreen(
                goExploreTab = { navController.navigateToExploreTab(inclusive = true) },
            )
            detailScreen(
                goRecipeScreen = { navController.navigateToRecipeScreen(tryMode = true) },
                goDetailScreen = { navController.navigateToDetailScreen(it.toString()) },
            )
            recipeScreen(
                goRecipeResultScreen = navController::navigateToRecipeResultScreen,
                goRecipeMetadataScreen = navController::navigateToRecipeResultMetadataScreen,
                goSuccessScreen = navController::navigateToRecipeSuccessScreen,
                goExploreTab = { navController.navigateToExploreTab(inclusive = true) },
                goMyTab = { navController.navigateToCollectionTab(inclusive = true) },
                goGenerateScreen = { navController.navigateToRecipeScreen(importMode = it) },
            )
            collectionScreen(
                goDetailScreen = navController::navigateToDetailScreen,
            )
        }

        val density = LocalDensity.current
        val isBottomNavigationShowed by remember(currentRoute) {
            derivedStateOf { bottomNavigationShowRoutes.any { currentRoute == it } }
        }
        val coroutineScope = rememberCoroutineScope()

        AnimatedVisibility(
            visible = isBottomNavigationShowed,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { with(density) { 120.dp.roundToPx() } },
            ),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { with(density) { 120.dp.roundToPx() } },
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(99f),
        ) {
            DuckeeBottomTab(
                currentRoute = currentRoute.orEmpty(),
                onClick = {
                    when (it) {
                        exploreNavigationRoute -> navController.navigateToExploreTab()
                        recipeNavigationRoute -> {
                            coroutineScope.launch {
                                if (!isAuthenticated()) {
                                    navController.navigateToSignInScreen()
                                } else {
                                    navController.navigateToRecipeTab()
                                }
                            }
                        }

                        collectionNavigationRoute -> {
                            coroutineScope.launch {
                                if (!isAuthenticated()) {
                                    navController.navigateToSignInScreen()
                                } else {
                                    navController.navigateToCollectionTab()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .drawColoredShadow(
                        color = Color.Black,
                        alpha = 0.3f,
                        shadowRadius = 12.dp,
                        offsetY = 4.dp,
                        borderRadius = 40.dp,
                    )
                    .align(Alignment.BottomCenter)
                    .zIndex(2f),
            )
        }
    }
}
