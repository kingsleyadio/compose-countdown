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

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.minutes
import kotlin.time.seconds

/**
 * @author Kingsley Adio
 * @since 05 Mar, 2021
 */
fun Duration.formatAsString(): String {
    val minutes = toInt(DurationUnit.MINUTES)
    val seconds = toInt(DurationUnit.SECONDS) % 60

    return "%02d:%02d".format(minutes, seconds)
}

fun Duration(minute: Int, second: Int): Duration {
    return minute.minutes + second.seconds
}
