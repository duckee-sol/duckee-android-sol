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
package xyz.duckee.android.feature.signin

import androidx.lifecycle.ViewModel
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import xyz.duckee.android.core.domain.auth.SignInUseCase
import xyz.duckee.android.core.domain.auth.SignUpUseCase
import xyz.duckee.android.feature.signin.contract.SignInSideEffect
import xyz.duckee.android.feature.signin.contract.SignInState
import javax.inject.Inject

@HiltViewModel
internal class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
) : ViewModel(), ContainerHost<SignInState, SignInSideEffect> {

    override val container = container<SignInState, SignInSideEffect>(SignInState())

    fun onWebViewLoginResult(rawData: String) = intent {
        signInUseCase(rawData)
            .suspendOnSuccess {
                Timber.d("\uD83D\uDD11 [AuthResult] sign in with google successfully")
                Timber.d(" -> Credentials(${data.first})")
                Timber.d(" -> User(${data.second})")

                reduce { state.copy(isLoading = false) }

                postSideEffect(SignInSideEffect.GoExploreTab)
            }
            .suspendOnException {
                Timber.e(exception)
            }
            .suspendOnError {
                signUpUseCase(rawData)
                    .suspendOnSuccess {
                        Timber.d("\uD83D\uDD11 [AuthResult] sign up successfully")
                        Timber.d(" -> Credentials(${data.first})")
                        Timber.d(" -> User(${data.second})")

                        reduce { state.copy(isLoading = false) }

                        postSideEffect(SignInSideEffect.GoExploreTab)
                    }
                    .suspendOnError {
                        Timber.e(message())
                        postSideEffect(SignInSideEffect.ShowErrorToast)
                        reduce { state.copy(isLoading = false) }
                    }
            }
    }
}
