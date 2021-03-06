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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration

/**
 * @author Kingsley Adio
 * @since 05 Mar, 2021
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Countdown(
    isRunning: Boolean,
    totalTime: Duration,
    remainingTime: Duration,
    onStart: () -> Unit,
    onDurationChange: (Duration) -> Unit,
    onStop: (shouldReset: Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        CountdownCard(totalTime, remainingTime)
        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(!isRunning) {
            CountdownInput(
                duration = totalTime,
                onDurationChange = onDurationChange,
                modifier = Modifier.wrapContentSize()
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        CountdownController(isRunning, onStart, onStop)
    }
}

@Composable
fun CountdownCard(totalTime: Duration, remainingTime: Duration, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = Modifier.height(300.dp)) {
        val fullSize = (sqrt(maxWidth.value.pow(2) + maxHeight.value.pow(2))).toInt()
        val progress = (totalTime - remainingTime) / totalTime
        val progressWidth by animateFloatAsState(fullSize * progress.toFloat())

        Card(
            elevation = 8.dp,
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                val circleColor = MaterialTheme.colors.surface
                Canvas(modifier = Modifier.requiredSize(progressWidth.dp)) {
                    drawCircle(circleColor)
                }

                val infiniteTransition = rememberInfiniteTransition()
                val textColor by when {
                    progress >= 1f -> infiniteTransition.animateColor(
                        initialValue = MaterialTheme.colors.onSurface,
                        targetValue = MaterialTheme.colors.primaryVariant,
                        animationSpec = infiniteRepeatable(
                            animation = tween(250),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    else -> animateColorAsState(MaterialTheme.colors.onSurface)
                }
                val textScale by when {
                    progress >= 1f -> infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(250),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    else -> animateFloatAsState(targetValue = 1f)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = remainingTime.formatAsString(),
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.scale(textScale)
                    )
                    Text(
                        text = "left",
                        fontFamily = MaterialTheme.typography.h3.fontFamily,
                        color = textColor,
                        modifier = Modifier.scale(textScale)
                    )
                }
            }
        }
    }
}

@Composable
fun CountdownInput(duration: Duration, onDurationChange: (Duration) -> Unit, modifier: Modifier) {
    val (minute, second) = duration.formatAsString().split(":")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium), MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Counter(
            label = "Minutes",
            value = minute.toInt(),
            maxValue = 99,
            onValueChanged = { onDurationChange(Duration(it, second.toInt())) },
            modifier = Modifier.weight(1f)
        )
        Text(text = ":", style = MaterialTheme.typography.h6)
        Counter(
            label = "Seconds",
            value = second.toInt(),
            maxValue = 59,
            onValueChanged = { onDurationChange(Duration(minute.toInt(), it)) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun Counter(label: String, value: Int, maxValue: Int, onValueChanged: (Int) -> Unit, modifier: Modifier) {
    Column(
        // verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = label, style = MaterialTheme.typography.caption, modifier = Modifier.padding(bottom = 4.dp))
        IconButton(onClick = { onValueChanged((value + 1).coerceAtMost(maxValue)) }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Increment")
        }
        Text(
            text = "%02d".format(value),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .border(
                    1.dp,
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    MaterialTheme.shapes.medium
                )
                .padding(8.dp)
        )
        IconButton(onClick = { onValueChanged((value - 1).coerceAtLeast(0)) }) {
            Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrement")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownController(isRunning: Boolean, onStart: () -> Unit, onStop: (shouldReset: Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
        AnimatedVisibility(visible = !isRunning) {
            Button(
                onClick = { onStart() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            ) {
                Text(text = "Start")
            }
        }
        AnimatedVisibility(visible = isRunning) {
            Button(onClick = { onStop(false) }) {
                Text(text = "Stop")
            }
        }
        Button(onClick = { onStop(true) }) {
            Text(text = "Reset")
        }
    }
}
