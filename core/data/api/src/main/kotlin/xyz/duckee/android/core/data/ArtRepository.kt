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
package xyz.duckee.android.core.data

import com.skydoves.sandwich.ApiResponse
import xyz.duckee.android.core.model.ArtDetails
import xyz.duckee.android.core.model.ArtList

interface ArtRepository {

    suspend fun getArtFeed(startAfter: Int?, limit: Int?, tags: String?): ApiResponse<ArtList>

    suspend fun uploadArt(
        forSale: Boolean,
        imageUrl: String,
        description: String?,
        priceInFlow: Double,
        royaltyFee: Int,
        isImported: Boolean,
        modelName: String,
        prompt: String,
        sizeWidth: Int,
        sizeHeight: Int,
        negativePrompt: String?,
        guidanceScale: Int?,
        runs: Int?,
        sampler: String?,
        seed: Int?,
        parentTokenId: Int?,
    ): ApiResponse<Unit>

    suspend fun getArtDetails(tokenId: String): ApiResponse<ArtDetails>

    suspend fun putArtLike(tokenId: String, liked: Boolean): ApiResponse<Unit>
}
