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
package xyz.duckee.android.core.network

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xyz.duckee.android.core.network.api.AuthAPI
import xyz.duckee.android.core.network.interceptor.AuthorizationHeaderInterceptor
import xyz.duckee.android.core.network.model.DeeplinkAuthResult
import xyz.duckee.android.core.network.model.ResponseSignIn
import xyz.duckee.android.core.network.model.request.RequestSignIn
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    apiProvider: APIProvider,
    private val json: Json,
    private val authorizationHeaderInterceptor: AuthorizationHeaderInterceptor,
) : AuthDataSource {

    private val api = apiProvider[AuthAPI::class.java]

    override suspend fun signIn(rawData: String): ApiResponse<ResponseSignIn> {
        val result: DeeplinkAuthResult = json.decodeFromString(rawData)

        if (result.idToken.isBlank()) {
            return ApiResponse.error(NullPointerException("firebase idToken == null"))
        }

        return api.signIn(
            payload = RequestSignIn(
                channel = "web3auth",
                token = result.idToken,
                address = result.address,
            ),
        ).suspendOnSuccess {
            authorizationHeaderInterceptor.setAccessToken(token = data.credentials.accessToken)
        }
    }

    override suspend fun signUp(rawData: String): ApiResponse<ResponseSignIn> {
        val result: DeeplinkAuthResult = json.decodeFromString(rawData)
        val idToken = result.idToken

        if (idToken.isBlank()) {
            return ApiResponse.error(NullPointerException("firebase idToken == null"))
        }

        return api.signUp(
            payload = RequestSignIn(
                channel = "web3auth",
                token = idToken,
                address = result.address,
            ),
        ).suspendOnSuccess {
            authorizationHeaderInterceptor.setAccessToken(token = data.credentials.accessToken)
        }
    }
}
