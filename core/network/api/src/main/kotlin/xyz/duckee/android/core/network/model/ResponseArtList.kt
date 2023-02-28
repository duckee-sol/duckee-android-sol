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
package xyz.duckee.android.core.network.model

import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import xyz.duckee.android.core.model.ArtList

@Serializable
data class ResponseArtList(
    val hasNext: Boolean,
    val results: List<Result>,
    val total: Int,
    val nextStartAfter: String?,
) {

    @Serializable
    data class Result(
        val description: String?,
        val imageUrl: String,
        val liked: Boolean,
        val owner: Owner,
        val priceInFlow: Double,
        val royaltyFee: Double,
        val tokenId: Int,
    ) {

        @Serializable
        data class Owner(
            val address: String,
            val email: String,
            val id: Int,
            val profileImage: String,
        )
    }
}

fun ResponseArtList.toModel(): ArtList =
    ArtList(
        hasNext = hasNext,
        results = results.map { it.toModel() }.toPersistentList(),
        total = total,
        nextStartAfter = nextStartAfter,
    )

fun ResponseArtList.Result.toModel(): ArtList.Result =
    ArtList.Result(
        description = description,
        imageUrl = imageUrl,
        liked = liked,
        owner = owner.toModel(),
        priceInFlow = priceInFlow,
        royaltyFee = royaltyFee,
        tokenId = tokenId,
    )

fun ResponseArtList.Result.Owner.toModel(): ArtList.Result.Owner =
    ArtList.Result.Owner(
        address = address,
        email = email,
        id = id,
        profileImage = profileImage,
    )
