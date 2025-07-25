package com.lusion.lumanoiz

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.lusion.lumanoiz.ui.theme.LumaNoizAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import android.speech.tts.TextToSpeech
import java.util.Locale

class LightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            LumaNoizAppTheme {
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
    val activity = LocalContext.current as? Activity

    val tts = remember {
        TextToSpeech(context, null).apply {
            language = Locale.US
            setPitch(0.8f)
            setSpeechRate(0.9f)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val ballSize by userPreferencesRepository.ballSize.collectAsState(initial = 0.1f)
    val minDuration by userPreferencesRepository.minDuration.collectAsState(initial = 100f)
    val maxDuration by userPreferencesRepository.maxDuration.collectAsState(initial = 2000f)
    val totalDurationMinutes by userPreferencesRepository.totalDurationMinutes.collectAsState(initial = 10f)

    var ballHiddenCount by rememberSaveable { mutableStateOf(0) }
    var showPauseMenu by rememberSaveable { mutableStateOf(false) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    var isVisible by rememberSaveable { mutableStateOf(true) }
    var lastBallSpeed by rememberSaveable { mutableStateOf(0L) }
    var timeRemaining by rememberSaveable { mutableStateOf(0L) }
    var animationKey by rememberSaveable { mutableStateOf(0) } // Key to restart animation
    var initialDurationLoaded by rememberSaveable { mutableStateOf(false) }
    var savedOrientation by rememberSaveable { mutableStateOf(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) }
    var isInitialOrientationSet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showPauseMenu) {
        activity?.let { act ->
            if (showPauseMenu) {
                if (!isInitialOrientationSet) {
                    savedOrientation = act.requestedOrientation
                    isInitialOrientationSet = true
                }
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                if (isInitialOrientationSet) {
                    act.requestedOrientation = savedOrientation
                    isInitialOrientationSet = false // Reset for next time
                }
            }
        }
    }

    LaunchedEffect(totalDurationMinutes) {
        if (!initialDurationLoaded) {
            timeRemaining = (totalDurationMinutes * 60 * 1000).toLong()
            initialDurationLoaded = true
        }
    }

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

            LaunchedEffect(isPaused, ballSize, minDuration, maxDuration, totalDurationMinutes, animationKey) {
                if (!isPaused) {
                    val animationDuration = timeRemaining
                    val startTime = System.currentTimeMillis()

                    // Coroutine to update time remaining
                    val timerJob = launch {
                        while (isActive) {
                            val elapsedTime = System.currentTimeMillis() - startTime
                            timeRemaining = (animationDuration - elapsedTime).coerceAtLeast(0)
                            delay(1000)
                        }
                    }

                    val result = withTimeoutOrNull(animationDuration) {
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
                            delay(Random.nextLong(300, 3001))

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
                            delay(Random.nextLong(300, 1501))

                            // Teleport to left edge
                            xPosition.snapTo(-ballSizePx)
                        }
                    }
                    timerJob.cancel()
                    if (result == null) { // Timeout occurred
                        isPaused = true
                        showPauseMenu = true
                        isVisible = false
                        val finalCount = if (ballHiddenCount > 0) ballHiddenCount - 1 else 0
                        val textToSpeak = "${numberToWords(finalCount)} times"
                        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = {
                            if (timeRemaining > 0) {
                                isPaused = false
                                showPauseMenu = false
                            }
                        })
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val displayCount = if (ballHiddenCount > 0) ballHiddenCount - 1 else 0
                    Text(
                        text = displayCount.toString(),
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
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining)
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
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
                            enabled = timeRemaining > 0,
                            modifier = Modifier.size(width = 120.dp, height = 60.dp)
                        ) {
                            Text("Resume")
                        }
                        Button(
                            onClick = {
                                ballHiddenCount = 0
                                timeRemaining = (totalDurationMinutes * 60 * 1000).toLong()
                                animationKey++ // This will re-trigger the LaunchedEffect
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
                        valueRange = 1f..10f,
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        }
    }
}

fun numberToWords(number: Int): String {
    if (number == 0) return "zero"

    val units = arrayOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
    val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

    fun convert(n: Int): String {
        return when {
            n < 20 -> units[n]
            n < 100 -> tens[n / 10] + (if (n % 10 != 0) " " + units[n % 10] else "")
            n < 1000 -> units[n / 100] + " hundred" + (if (n % 100 != 0) " " + convert(n % 100) else "")
            else -> ""
        }
    }

    return convert(number)
}
