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
package xyz.duckee.android.feature.collection

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import xyz.duckee.android.core.designsystem.DuckeeNetworkImage
import xyz.duckee.android.core.designsystem.DuckeeScrollableTabRow
import xyz.duckee.android.core.designsystem.theme.DuckeeTheme
import xyz.duckee.android.core.ui.observeAsState
import xyz.duckee.android.feature.collection.component.CollectionBalance
import xyz.duckee.android.feature.collection.component.CollectionProfile
import xyz.duckee.android.feature.collection.component.CollectionTitleBar
import xyz.duckee.android.feature.collection.component.CollectionWallet
import xyz.duckee.android.feature.collection.contract.CollectionFeedState
import xyz.duckee.android.feature.collection.contract.CollectionState
import xyz.duckee.android.feature.collection.ui.pagerTabIndicatorOffset

private val tabs = listOf("Listed", "Bought", "Not for sale", "Liked")

@Composable
internal fun CollectionRoute(
    viewModel: CollectionViewModel = hiltViewModel(),
    listedViewModel: CollectionListedViewModel = hiltViewModel(),
    boughtViewModel: CollectionBoughtViewModel = hiltViewModel(),
    likedViewModel: CollectionLikedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val listedUiState by listedViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val boughtUiState by boughtViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val likedUiState by likedViewModel.container.stateFlow.collectAsStateWithLifecycle()

    val lifecycle by LocalLifecycleOwner.current.lifecycle.observeAsState()
    LaunchedEffect(lifecycle) {
        if (lifecycle == Lifecycle.Event.ON_RESUME) {
            listedViewModel.onResume()
            boughtViewModel.onResume()
            likedViewModel.onResume()
        }
    }

    CollectionScreen(
        uiState = uiState,
        listedUiState = listedUiState,
        boughtUiState = boughtUiState,
        likedUiState = likedUiState,
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CollectionScreen(
    uiState: CollectionState,
    listedUiState: CollectionFeedState,
    boughtUiState: CollectionFeedState,
    likedUiState: CollectionFeedState,
) {
    val state = rememberCollapsingToolbarScaffoldState()
    val density = LocalDensity.current

    val pagerState = rememberPagerState()

    CollapsingToolbarScaffold(
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            val scrollProgress = with(density) { state.toolbarState.height.toDp() }

            Box(
                modifier = Modifier
                    .background(Color(0xFF08090A))
                    .fillMaxWidth()
                    .height(320.dp)
                    .pin(),
            )

            CollectionTitleBar(
                onSettingClick = {},
                modifier = Modifier
                    .height(50.dp)
                    .offset(y = -(320f - scrollProgress.value).dp),
            )

            Column(
                modifier = Modifier
                    .padding(top = 58.dp)
                    .offset(y = -(320f - scrollProgress.value).dp),
            ) {
                CollectionProfile(
                    profileUrl = uiState.user?.profileImage.orEmpty(),
                    recipeCount = uiState.user?.artCount ?: 0,
                    followingCount = uiState.user?.followingCount ?: 0,
                    followerCount = uiState.user?.followerCount ?: 0,
                )
                Spacer(modifier = Modifier.height(12.dp))
                CollectionWallet(
                    profileName = uiState.user?.nickname.orEmpty(),
                    address = uiState.user?.address.orEmpty().run {
                        if (length > 10) {
                            substring(0, 5) + "..." + substring(length - 5, length)
                        } else {
                            ""
                        }
                    },
                    onLinkButtonClick = {},
                )
                Spacer(modifier = Modifier.height(12.dp))
                CollectionBalance(
                    balance = 0.0,
                    estimateBalance = 0.0,
                    onAddButtonClick = {},
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                DuckeeScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    backgroundColor = Color.Transparent,
                    edgePadding = 24.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .pagerTabIndicatorOffset(pagerState, tabPositions)
                                .padding(end = 20.dp),
                            height = 3.dp,
                            color = Color.White,
                        )
                    },
                ) {
                    val coroutineScope = rememberCoroutineScope()

                    tabs.forEachIndexed { index, s ->
                        val colorValue by animateColorAsState(
                            targetValue = if ((pagerState.currentPage) == index) {
                                Color.White
                            } else {
                                Color(
                                    0xFF49565E,
                                )
                            },
                        )
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier
                                .padding(end = 20.dp),
                        ) {
                            Text(
                                text = s,
                                style = DuckeeTheme.typography.h6,
                                color = colorValue,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            )
                        }
                    }
                }
                Divider(
                    thickness = 1.dp,
                    color = Color(0xFF171C20),
                    modifier = Modifier.offset(y = (-1).dp),
                )
            }
        },
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
    ) {
        HorizontalPager(
            count = 4,
            state = pagerState,
        ) { page ->
            LazyVerticalGrid(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 120.dp),
                columns = GridCells
                    .Fixed(2),
                modifier = Modifier.fillMaxSize(),
            ) {
                when (page) {
                    0 -> {
                        items(listedUiState.feeds) {
                            DuckeeNetworkImage(
                                model = it.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(6.5.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                            )
                        }
                    }

                    1 -> {
                        items(boughtUiState.feeds) {
                            DuckeeNetworkImage(
                                model = it.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(6.5.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                            )
                        }
                    }
//
//                    2 -> {
//                        items(uiState.notForSale) {
//                            DuckeeNetworkImage(
//                                model = it,
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .aspectRatio(1f)
//                                    .padding(6.5.dp)
//                                    .clip(RoundedCornerShape(16.dp)),
//                            )
//                        }
//                    }
//
                    3 -> {
                        items(likedUiState.feeds) {
                            DuckeeNetworkImage(
                                model = it.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(6.5.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                            )
                        }
                    }
                }
            }
        }
    }
}