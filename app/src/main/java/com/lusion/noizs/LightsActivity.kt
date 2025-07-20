package com.lusion.noizs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.lusion.noizs.ui.theme.NoizsAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class LightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            NoizsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    BallAnimationScreen()
                }
            }
        }
    }
}

@Composable
fun BallAnimationScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }

    val ballSize by userPreferencesRepository.ballSize.collectAsState(initial = 0.1f)
    val minDuration by userPreferencesRepository.minDuration.collectAsState(initial = 100f)
    val maxDuration by userPreferencesRepository.maxDuration.collectAsState(initial = 2000f)
    val totalDurationMinutes by userPreferencesRepository.totalDurationMinutes.collectAsState(initial = 10f)

    var ballHiddenCount by remember { mutableStateOf(0) }
    var showPauseMenu by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }
    var lastBallSpeed by remember { mutableStateOf(0L) }
    var timeRemaining by remember { mutableStateOf(totalDurationMinutes * 60 * 1000) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isPaused = true
                        showPauseMenu = true
                        isVisible = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = with(LocalDensity.current) { maxWidth.toPx() }
            val screenHeight = with(LocalDensity.current) { maxHeight.toPx() }
            val isPortrait = screenHeight > screenWidth

            val minBallSizePx = screenWidth * 0.01f
            val maxBallSizePx = screenHeight
            var ballSizePx = minBallSizePx + (maxBallSizePx - minBallSizePx) * ballSize

            if (isPortrait) {
                ballSizePx *= 0.5f
            }

            val ballSizeDp = with(LocalDensity.current) { ballSizePx.toDp() }

            val xPosition = remember { Animatable(0f) }

            val yOffset = with(LocalDensity.current) { ((screenHeight - ballSizePx) / 2).toDp() }

            LaunchedEffect(isPaused, ballSize, minDuration, maxDuration, totalDurationMinutes) {
                if (!isPaused) {
                    val totalDurationMillis = totalDurationMinutes * 60 * 1000
                    val startTime = System.currentTimeMillis()

                    // Coroutine to update time remaining
                    val timerJob = launch {
                        while (isActive) {
                            val elapsedTime = System.currentTimeMillis() - startTime
                            timeRemaining = totalDurationMillis - elapsedTime
                            delay(1000)
                        }
                    }

                    withTimeoutOrNull(totalDurationMillis.toLong()) {
                        while (isActive) {
                            // Left to Right
                            isVisible = true
                            val durationLtr = Random.nextLong(minDuration.toLong(), maxDuration.toLong())
                            lastBallSpeed = durationLtr
                            xPosition.animateTo(
                                targetValue = screenWidth,
                                animationSpec = tween(
                                    durationMillis = durationLtr.toInt(),
                                    easing = LinearEasing
                                )
                            )

                            // Pause at right
                            isVisible = false
                            ballHiddenCount++
                            delay(1000)

                            // Teleport to right edge before animating back
                            xPosition.snapTo(screenWidth)

                            // Right to Left
                            isVisible = true
                            val durationRtl = Random.nextLong(minDuration.toLong(), maxDuration.toLong())
                            lastBallSpeed = durationRtl
                            xPosition.animateTo(
                                targetValue = -ballSizePx,
                                animationSpec = tween(
                                    durationMillis = durationRtl.toInt(),
                                    easing = LinearEasing
                                )
                            )

                            // Pause at left
                            isVisible = false
                            ballHiddenCount++
                            delay(2000)

                            // Teleport to left edge
                            xPosition.snapTo(-ballSizePx)
                        }
                    }
                    timerJob.cancel()
                }
            }

            if (isVisible) {
                Canvas(
                    modifier = Modifier
                        .size(ballSizeDp)
                        .offset(
                            x = with(LocalDensity.current) { xPosition.value.toDp() },
                            y = yOffset
                        )
                ) {
                    drawCircle(color = Color.White)
                }
            }
        }

        if (showPauseMenu) {
            // Ball Pause Menu
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        isPaused = false
                        showPauseMenu = false
                    })
                }
            ) {
                Text(
                    text = ballHiddenCount.toString(),
                    color = Color.White,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (lastBallSpeed > 0) {
                    Text(
                        text = "Last Speed: ${lastBallSpeed}ms",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                if (timeRemaining > 0) {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining.toLong())
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining.toLong()) % 60
                    Text(
                        text = "Time Remaining: %02d:%02d".format(minutes, seconds),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            isPaused = false
                            showPauseMenu = false
                        },
                        modifier = Modifier.size(width = 120.dp, height = 60.dp)
                    ) {
                        Text("Resume")
                    }
                    Button(
                        onClick = {
                            ballHiddenCount = 0
                            isPaused = false
                            showPauseMenu = false
                        },
                        modifier = Modifier.size(width = 120.dp, height = 60.dp)
                    ) {
                        Text("Restart")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Text("Ball Size", color = Color.White)
                Slider(
                    value = ballSize,
                    onValueChange = { newSize ->
                        coroutineScope.launch { userPreferencesRepository.setBallSize(newSize) }
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Min Ball Speed", color = Color.White, modifier = Modifier.width(120.dp))
                    Text("${minDuration.toInt()}ms", color = Color.White)
                }
                Slider(
                    value = minDuration,
                    onValueChange = { newDuration ->
                        coroutineScope.launch { userPreferencesRepository.setMinDuration(newDuration.coerceAtMost(maxDuration - 1)) }
                    },
                    valueRange = 100f..5000f,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Max Ball Speed", color = Color.White, modifier = Modifier.width(120.dp))
                    Text("${maxDuration.toInt()}ms", color = Color.White)
                }
                Slider(
                    value = maxDuration,
                    onValueChange = { newDuration ->
                        coroutineScope.launch { userPreferencesRepository.setMaxDuration(newDuration.coerceAtLeast(minDuration + 1)) }
                    },
                    valueRange = 100f..5000f,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total Duration", color = Color.White, modifier = Modifier.width(120.dp))
                    Text("${totalDurationMinutes.toInt()} min", color = Color.White)
                }
                Slider(
                    value = totalDurationMinutes,
                    onValueChange = { newMinutes ->
                        coroutineScope.launch { userPreferencesRepository.setTotalDurationMinutes(newMinutes) }
                    },
                    valueRange = 1f..60f,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}
