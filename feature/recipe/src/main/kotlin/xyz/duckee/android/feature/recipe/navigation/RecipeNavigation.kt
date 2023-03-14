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
package xyz.duckee.android.feature.recipe.navigation

import androidx.navigation.NavGraphBuilder
import xyz.duckee.android.core.navigation.RecipeDirections
import xyz.duckee.android.core.navigation.transition.rootTransitionComposable
import xyz.duckee.android.core.navigation.transition.sharedXTransitionComposable
import xyz.duckee.android.feature.recipe.RecipeGenerateWelcomeRoute
import xyz.duckee.android.feature.recipe.RecipeListSuccessRoute
import xyz.duckee.android.feature.recipe.RecipeMetadataConfigRoute
import xyz.duckee.android.feature.recipe.RecipeResultRoute
import xyz.duckee.android.feature.recipe.RecipeRoute

fun NavGraphBuilder.recipeScreen(
    goRecipeResultScreen: (String) -> Unit,
    goRecipeMetadataScreen: (String) -> Unit,
    goSuccessScreen: (String) -> Unit,
    goMyTab: () -> Unit,
    goExploreTab: () -> Unit,
    goGenerateScreen: (Boolean) -> Unit,
) {
    rootTransitionComposable(
        routeCommand = RecipeDirections.welcome,
        commands = listOf(RecipeDirections.main),
    ) {
        RecipeGenerateWelcomeRoute(
            goGenerateScreen = goGenerateScreen,
        )
    }
    sharedXTransitionComposable(
        command = RecipeDirections.main,
    ) {
        RecipeRoute(
            goRecipeResultScreen = goRecipeResultScreen,
        )
    }
    sharedXTransitionComposable(
        command = RecipeDirections.result,
    ) {
        RecipeResultRoute(
            goRecipeMetadataScreen = goRecipeMetadataScreen,
        )
    }
    sharedXTransitionComposable(
        command = RecipeDirections.resultMetadata,
    ) {
        RecipeMetadataConfigRoute(
            goSuccessScreen = goSuccessScreen,
        )
    }
    sharedXTransitionComposable(
        command = RecipeDirections.success,
    ) {
        RecipeListSuccessRoute(
            goMyTab = goMyTab,
            goExploreTab = goExploreTab,
        )
    }
}
