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

import androidx.lifecycle.ViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import xyz.duckee.android.core.domain.user.GetMyProfileUseCase
import xyz.duckee.android.feature.collection.contract.CollectionState
import javax.inject.Inject

@HiltViewModel
internal class CollectionViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
) : ViewModel(), ContainerHost<CollectionState, Unit> {

    override val container = container<CollectionState, Unit>(CollectionState())

    init {
        getMyProfile()
    }

    private fun getMyProfile() = intent {
        getMyProfileUseCase()
            .suspendOnSuccess {
                reduce { state.copy(user = data) }
            }
    }
}