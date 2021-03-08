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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration
import kotlin.time.milliseconds

/**
 * @author Kingsley Adio
 * @since 06 Mar, 2021
 */
interface TickerProvider {
    fun createTicker(duration: Duration, interval: Duration = 50.milliseconds): Flow<Duration>
}

class GoodEnoughTickerProvider : TickerProvider {

    override fun createTicker(duration: Duration, interval: Duration): Flow<Duration> {
        return flow {
            var remainingTime = duration
            while (remainingTime > Duration.ZERO) {
                emit(remainingTime)
                delay(interval)
                remainingTime -= interval
            }
        }.flowOn(Dispatchers.Default)
    }
}
