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
package xyz.duckee.android.core.network.api

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.duckee.android.core.network.model.ResponseArtDetail
import xyz.duckee.android.core.network.model.ResponseArtList
import xyz.duckee.android.core.network.model.request.RequestArtLike
import xyz.duckee.android.core.network.model.request.RequestUploadArt

internal interface ArtAPI {

    @GET("art/v1")
    suspend fun getArtFeed(
        @Query("start_after") startAfter: Int?,
        @Query("limit") limit: Int?,
        @Query("tags") tags: String?,
    ): ApiResponse<ResponseArtList>

    @POST("art/v1")
    suspend fun uploadArt(@Body payload: RequestUploadArt): ApiResponse<Unit>

    @GET("art/v1/{tokenId}/details")
    suspend fun getArtDetails(@Path("tokenId") tokenId: String): ApiResponse<ResponseArtDetail>

    @PUT("art/v1/{tokenId}/like")
    suspend fun putArtLike(@Path("tokenId") tokenId: String, @Body payload: RequestArtLike): ApiResponse<Unit>
}
