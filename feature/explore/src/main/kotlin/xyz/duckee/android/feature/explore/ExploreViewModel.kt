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
package xyz.duckee.android.feature.explore

import androidx.lifecycle.ViewModel
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import xyz.duckee.android.core.domain.art.GetArtFeedUseCase
import xyz.duckee.android.core.domain.art.LikeArtUseCase
import xyz.duckee.android.core.domain.auth.CheckAuthenticateStateUseCase
import xyz.duckee.android.core.model.ArtList
import xyz.duckee.android.core.ui.ExploreDataManager
import xyz.duckee.android.feature.explore.contract.ExploreSideEffect
import xyz.duckee.android.feature.explore.contract.ExploreState
import javax.inject.Inject

@HiltViewModel
internal class ExploreViewModel @Inject constructor(
    private val checkAuthenticateStateUseCase: CheckAuthenticateStateUseCase,
    private val getArtFeedUseCase: GetArtFeedUseCase,
    private val likeArtUseCase: LikeArtUseCase,
    private val exploreDataManager: ExploreDataManager,
) : ViewModel(), ContainerHost<ExploreState, ExploreSideEffect> {

    override val container = container<ExploreState, ExploreSideEffect>(ExploreState())

    init {
        getArtFeed()
    }

    fun onResume() = intent {
        if (exploreDataManager.reloadState.value) {
            reduce { ExploreState() }
            getArtFeed()
            exploreDataManager.invalidatePendingReloadState()
        }
    }

    @OptIn(OrbitExperimental::class)
    fun onSearchValueChanged(value: String) = blockingIntent {
        reduce { state.copy(searchValue = value) }
    }

    fun onFilterClick(filter: String) = intent {
        reduce { state.copy(selectedFilter = filter) }
    }

    fun onImageClick(tokenId: String) = intent {
        if (checkAuthenticateStateUseCase()) { // if authenticated,
            postSideEffect(ExploreSideEffect.GoDetail(id = tokenId))
        } else {
            postSideEffect(ExploreSideEffect.GoSignInScreen)
        }
    }

    fun onLikeClick(tokenId: String) = intent {
        state.feeds.firstOrNull { it.tokenId == tokenId.toInt() }?.let {
            likeArtUseCase(tokenId, !it.liked)

            reduce {
                state.copy(
                    feeds = buildList<ArtList.Result> {
                        addAll(state.feeds)

                        val index = state.feeds.indexOfFirst { it.tokenId == tokenId.toInt() }
                        this[index] = this[index].copy(
                            liked = !it.liked,
                        )
                    }.toPersistentList(),
                )
            }
        }
    }

    fun onScrollEnd() = intent {
        if (state.hasNext) {
            getArtFeed()
        }
    }

    private fun getArtFeed() = intent {
        if (state.nextStartAfter == null) {
            reduce { state.copy(isLoading = true) }
        }

        getArtFeedUseCase(
            startAfter = state.nextStartAfter.takeIf { !it.isNullOrBlank() }?.toInt(),
        )
            .suspendOnSuccess {
                reduce {
                    state.copy(
                        isLoading = false,
                        hasNext = data.hasNext,
                        nextStartAfter = data.nextStartAfter.orEmpty(),
                        feeds = buildList {
                            addAll(state.feeds)
                            addAll(data.results)
                        }.toPersistentList(),
                    )
                }
            }
            .suspendOnException {
                Timber.e(exception)
            }
    }
}
