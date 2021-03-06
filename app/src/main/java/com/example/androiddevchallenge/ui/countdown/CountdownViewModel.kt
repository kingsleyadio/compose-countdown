/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.androiddevchallenge.ui.countdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration

/**
 * @author Kingsley Adio
 * @since 05 Mar, 2021
 */
class CountdownViewModel(private val tickerProvider: TickerProvider) : ViewModel() {

    val viewState = MutableStateFlow(ViewState())
    private var countdown: Job? = null

    private val remainingTime
        get() = viewState.value.remainingTime

    private val totalTime
        get() = viewState.value.totalTime

    fun start() {
        val runTime = when {
            remainingTime > Duration.ZERO -> remainingTime
            else -> totalTime
        }

        viewState.value = viewState.value.copy(remainingTime = runTime, isRunning = true)
        startCountdown(runTime)
    }

    private fun startCountdown(runTime: Duration) {
        countdown = tickerProvider.createTicker(runTime)
            .onEach {
                viewState.value = viewState.value.copy(
                    remainingTime = it
                )
            }
            .onCompletion { throwable ->
                if (throwable == null) { // Completed normally
                    viewState.value = viewState.value.copy(
                        remainingTime = Duration.ZERO,
                        isRunning = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateCountdown(duration: Duration) {
        stop(true)
        viewState.value = viewState.value.copy(totalTime = duration, remainingTime = duration)
    }

    fun stop(reset: Boolean = false) {
        countdown?.cancel()

        viewState.value = ViewState(
            totalTime = if (reset) Duration.ZERO else totalTime,
            remainingTime = if (reset) Duration.ZERO else remainingTime,
            isRunning = false
        )
    }
}

data class ViewState(
    val totalTime: Duration = Duration.ZERO,
    val remainingTime: Duration = Duration.ZERO,
    val isRunning: Boolean = false
)
