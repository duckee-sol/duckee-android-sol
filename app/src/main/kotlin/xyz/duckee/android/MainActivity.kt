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
package xyz.duckee.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import timber.log.Timber
import xyz.duckee.android.core.designsystem.theme.DuckeeTheme
import xyz.duckee.android.core.domain.auth.CheckAuthenticateStateUseCase
import xyz.duckee.android.core.ui.DeeplinkHandlable
import xyz.duckee.android.core.ui.LocalNavigationPopStack
import xyz.duckee.android.core.ui.LocalPaymentSheet
import xyz.duckee.android.core.ui.PurchaseEventManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), DeeplinkHandlable {
    private lateinit var paymentSheet: PaymentSheet

    @Inject
    lateinit var checkAuthenticateStateUseCase: CheckAuthenticateStateUseCase

    @Inject
    lateinit var purchaseEventManager: PurchaseEventManager

    private val _deeplink = MutableStateFlow(Uri.EMPTY)
    override val deeplink = _deeplink.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentSheet = PaymentSheet(this) {
            when (it) {
                is PaymentSheetResult.Canceled -> {
                    purchaseEventManager.purchaseComplete()
                    Timber.e("Canceled")
                }

                is PaymentSheetResult.Failed -> {
                    purchaseEventManager.purchaseComplete()
                    Timber.e("Error: ${it.error}")
                }

                is PaymentSheetResult.Completed -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        purchaseEventManager.purchaseComplete()
                    }

                    purchaseEventManager.purchaseComplete()
                    Timber.e("Completed")
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        setContent {
            val navController = rememberMaterialMotionNavController()

            DuckeeTheme {
                CompositionLocalProvider(
                    LocalNavigationPopStack provides { navController.popBackStack() },
                    LocalPaymentSheet provides paymentSheet,
                ) {
                    DuckeeApp(
                        isAuthenticated = { checkAuthenticateStateUseCase() },
                        navController = navController,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            _deeplink.value = it
        }

        lifecycleScope.launch {
            delay(500)
            _deeplink.value = Uri.EMPTY
        }
    }
}
