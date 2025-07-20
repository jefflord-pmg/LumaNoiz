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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    var ballHiddenCount by remember { mutableStateOf(0) }
    var showCounter by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isPaused = true
                        showCounter = true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = with(LocalDensity.current) { maxWidth.toPx() }
            val screenHeight = with(LocalDensity.current) { maxHeight.toPx() }
            val ballSize = 50.dp
            val ballSizePx = with(LocalDensity.current) { ballSize.toPx() }

            val xPosition = remember { Animatable(0f) }
            var isVisible by remember { mutableStateOf(true) }

            val yOffset = with(LocalDensity.current) { ((screenHeight - ballSizePx) / 2).toDp() }

            LaunchedEffect(isPaused) {
                if (!isPaused) {
                    withTimeoutOrNull(10 * 60 * 1000L) { // 10 minutes
                        while (isActive) {
                            // Left to Right
                            isVisible = true
                            val durationLtr = Random.nextLong(100, 2000)
                            xPosition.animateTo(
                                targetValue = screenWidth - ballSizePx,
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
                            xPosition.snapTo(screenWidth - ballSizePx)

                            // Right to Left
                            isVisible = true
                            val durationRtl = Random.nextLong(100, 2000)
                            xPosition.animateTo(
                                targetValue = 0f,
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
                            xPosition.snapTo(0f)
                        }
                    }
                }
            }

            if (isVisible) {
                Canvas(
                    modifier = Modifier
                        .size(ballSize)
                        .offset(
                            x = with(LocalDensity.current) { xPosition.value.toDp() },
                            y = yOffset
                        )
                ) {
                    drawCircle(color = Color.White)
                }
            }
        }

        if (showCounter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = ballHiddenCount.toString(),
                    color = Color.White,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        isPaused = false
                        showCounter = false
                    }) {
                        Text("Resume")
                    }
                    Button(onClick = {
                        ballHiddenCount = 0
                        isPaused = false
                        showCounter = false
                    }) {
                        Text("Restart")
                    }
                }
            }
        }
    }
}
