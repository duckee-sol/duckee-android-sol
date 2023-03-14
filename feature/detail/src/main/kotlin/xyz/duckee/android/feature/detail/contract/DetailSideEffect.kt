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
package xyz.duckee.android.feature.detail.contract

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import xyz.duckee.android.core.model.Payment

internal sealed interface DetailSideEffect {

    @Stable
    object GoRecipeScreen : DetailSideEffect

    @Immutable
    data class PurchaseWithStripe(
        val payment: Payment,
    ) : DetailSideEffect

    @Immutable
    data class GoDetailScreen(
        val tokenId: Long,
    ) : DetailSideEffect
}
