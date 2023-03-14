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
package xyz.duckee.android.feature.recipe

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import xyz.duckee.android.core.designsystem.DuckeeButton
import xyz.duckee.android.core.designsystem.R
import xyz.duckee.android.core.designsystem.foundation.clickableSingle
import xyz.duckee.android.core.designsystem.theme.DuckeeTheme
import xyz.duckee.android.core.designsystem.theme.PPObjectSans
import xyz.duckee.android.feature.recipe.contract.RecipeSideEffect

@Composable
internal fun RecipeListSuccessRoute(
    viewModel: RecipeListSuccessViewModel = hiltViewModel(),
    goMyTab: () -> Unit,
    goExploreTab: () -> Unit,
) {
    val context = LocalContext.current
    viewModel.collectSideEffect {
        when (it) {
            is RecipeSideEffect.GoMyTab -> {
                goMyTab()
            }

            is RecipeSideEffect.GoExploreTab -> {
                goExploreTab()
            }

            is RecipeSideEffect.OpenScanUrl -> {
                val scanUrl = Uri.parse(it.scanUrl)
                val scanIntent = CustomTabsIntent.Builder().setToolbarColor(android.graphics.Color.BLACK)
                    .setNavigationBarDividerColor(android.graphics.Color.BLACK)
                    .setShowTitle(false)
                    .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                    .setNavigationBarColor(android.graphics.Color.BLACK).build()
                scanIntent.launchUrl(context, scanUrl)
            }

            else -> {}
        }
    }

    BackHandler { /* NO_OP */ }

    RecipeListSuccessScreen(
        onCheckButtonClick = viewModel::onCheckButtonClick,
        onExploreButtonClick = viewModel::onExploreButtonClick,
        onSolScanClick = viewModel::onScanButtonClick,
    )
}

@Composable
internal fun RecipeListSuccessScreen(
    onCheckButtonClick: () -> Unit,
    onExploreButtonClick: () -> Unit,
    onSolScanClick: () -> Unit,
) {
    Scaffold {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "\uD83E\uDD73\nSuccessfully\nlisted!",
                style = DuckeeTheme.typography.h2,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "You can check your listed NFT\non the explore tab!",
                style = DuckeeTheme.typography.paragraph3,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(44.dp))
            DuckeeButton(
                label = "Check",
                labelColor = Color(0xFF08090A),
                labelStyle = DuckeeTheme.typography.title1,
                backgroundColor = Color(0xFFFBFBFB),
                onClick = onCheckButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            DuckeeButton(
                label = "List another NFT",
                labelColor = Color(0xFFFBFBFB),
                labelStyle = DuckeeTheme.typography.paragraph4.copy(fontFamily = PPObjectSans),
                backgroundColor = Color.Transparent,
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 14.dp),
                onClick = onExploreButtonClick,
                modifier = Modifier
                    .border(width = 1.dp, color = Color(0xFF49565E), shape = RoundedCornerShape(24.dp)),
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickableSingle { onSolScanClick() }
                    .padding(6.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_sol_scan),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "View on Solscan", style = DuckeeTheme.typography.paragraph5, color = Color.White)
            }
            Spacer(modifier = Modifier.height(54.dp))
        }
    }
}
