package com.lusion.lumanoiz

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class LightsActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        userPreferencesRepository = UserPreferencesRepository(this)
        textToSpeech = TextToSpeech(this, this)
        
        setContent {
            LumaNoizTheme {
                LightsScreen()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
        }
    }

    @Composable
    fun LightsScreen() {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        
        var ballSize by remember { mutableFloatStateOf(50f) }
        var minSpeed by remember { mutableIntStateOf(1000) }
        var maxSpeed by remember { mutableIntStateOf(3000) }
        var sessionDuration by remember { mutableIntStateOf(5) }
        var isPaused by remember { mutableStateOf(false) }
        var isSessionActive by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        var ballCount by remember { mutableIntStateOf(0) }
        var remainingTime by remember { mutableIntStateOf(0) }

        // Load preferences
        LaunchedEffect(Unit) {
            userPreferencesRepository.ballSize.collect { ballSize = it }
        }
        LaunchedEffect(Unit) {
            userPreferencesRepository.minBallSpeed.collect { minSpeed = it }
        }
        LaunchedEffect(Unit) {
            userPreferencesRepository.maxBallSpeed.collect { maxSpeed = it }
        }
        LaunchedEffect(Unit) {
            userPreferencesRepository.sessionDuration.collect { sessionDuration = it }
        }

        // Ball animation state
        val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
        
        var ballX by remember { mutableFloatStateOf(screenWidth / 2) }
        var ballY by remember { mutableFloatStateOf(screenHeight / 2) }
        var velocityX by remember { mutableFloatStateOf(5f) }
        var velocityY by remember { mutableFloatStateOf(5f) }
        var lastHorizontalBounce by remember { mutableFloatStateOf(0f) }
        var lastVerticalBounce by remember { mutableFloatStateOf(0f) }

        // Timer logic
        LaunchedEffect(isSessionActive, isPaused) {
            if (isSessionActive && !isPaused) {
                remainingTime = sessionDuration * 60 // Convert minutes to seconds
                
                while (remainingTime > 0 && isSessionActive && !isPaused) {
                    delay(1000)
                    remainingTime--
                }
                
                if (remainingTime <= 0 && isSessionActive) {
                    // Session completed
                    isSessionActive = false
                    val completionMessage = "Session completed! Ball crossed $ballCount times."
                    textToSpeech?.speak(completionMessage, TextToSpeech.QUEUE_FLUSH, null, null)
                    
                    // Reset for next session
                    ballCount = 0
                    ballX = screenWidth / 2
                    ballY = screenHeight / 2
                }
            }
        }

        // Ball animation logic
        LaunchedEffect(isSessionActive, isPaused, minSpeed, maxSpeed, ballSize) {
            while (isSessionActive && !isPaused) {
                val currentSpeed = Random.nextLong(minSpeed.toLong(), maxSpeed.toLong())
                delay(currentSpeed)
                
                // Update ball position
                ballX += velocityX
                ballY += velocityY
                
                val ballRadius = ballSize / 2
                
                // Check horizontal boundaries and count crossings
                if (ballX <= ballRadius || ballX >= screenWidth - ballRadius) {
                    velocityX = -velocityX
                    ballX = if (ballX <= ballRadius) ballRadius else screenWidth - ballRadius
                    
                    // Only count if ball actually crossed the boundary
                    // This prevents the off-by-one error by ensuring we don't double-count bounces
                    val currentBounceX = ballX
                    if (abs(currentBounceX - lastHorizontalBounce) > ballRadius) {
                        ballCount++
                        lastHorizontalBounce = currentBounceX
                    }
                }
                
                // Check vertical boundaries and count crossings
                if (ballY <= ballRadius || ballY >= screenHeight - ballRadius) {
                    velocityY = -velocityY
                    ballY = if (ballY <= ballRadius) ballRadius else screenHeight - ballRadius
                    
                    // Only count if ball actually crossed the boundary
                    // This prevents the off-by-one error by ensuring we don't double-count bounces
                    val currentBounceY = ballY
                    if (abs(currentBounceY - lastVerticalBounce) > ballRadius) {
                        ballCount++
                        lastVerticalBounce = currentBounceY
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (isSessionActive) {
                                isPaused = true
                                showSettings = true
                            }
                        },
                        onTap = {
                            if (!isSessionActive) {
                                // Start new session
                                isSessionActive = true
                                isPaused = false
                                ballCount = 0
                                remainingTime = sessionDuration * 60
                                ballX = screenWidth / 2
                                ballY = screenHeight / 2
                                lastHorizontalBounce = 0f
                                lastVerticalBounce = 0f
                            }
                        }
                    )
                }
        ) {
            // Ball animation
            if (isSessionActive) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White,
                        radius = ballSize / 2,
                        center = Offset(ballX, ballY)
                    )
                }
            }

            // Session info overlay (top of screen)
            if (isSessionActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Time: ${remainingTime / 60}:${String.format("%02d", remainingTime % 60)}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Crossings: $ballCount",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }

            // Start instruction (center of screen when not active)
            if (!isSessionActive && !showSettings) {
                Text(
                    text = "Tap to start visual therapy session",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Pause menu/Settings
            if (showSettings) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(32.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pause Menu",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Show current ball count prominently
                            Text(
                                text = "Ball Crossings: $ballCount",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Remaining Time: ${remainingTime / 60}:${String.format("%02d", remainingTime % 60)}",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Divider(color = Color.Gray)

                            // Ball Size Setting
                            Text(
                                text = "Ball Size: ${ballSize.toInt()}",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Slider(
                                value = ballSize,
                                onValueChange = { ballSize = it },
                                valueRange = 20f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF6200EE),
                                    activeTrackColor = Color(0xFF6200EE)
                                )
                            )

                            // Min Speed Setting
                            Text(
                                text = "Min Speed: ${minSpeed}ms",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Slider(
                                value = minSpeed.toFloat(),
                                onValueChange = { minSpeed = it.toInt() },
                                valueRange = 100f..5000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF6200EE),
                                    activeTrackColor = Color(0xFF6200EE)
                                )
                            )

                            // Max Speed Setting
                            Text(
                                text = "Max Speed: ${maxSpeed}ms",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Slider(
                                value = maxSpeed.toFloat(),
                                onValueChange = { maxSpeed = it.toInt() },
                                valueRange = 100f..5000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF6200EE),
                                    activeTrackColor = Color(0xFF6200EE)
                                )
                            )

                            // Session Duration Setting
                            Text(
                                text = "Session Duration: ${sessionDuration} minutes",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Slider(
                                value = sessionDuration.toFloat(),
                                onValueChange = { sessionDuration = it.toInt() },
                                valueRange = 1f..10f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF6200EE),
                                    activeTrackColor = Color(0xFF6200EE)
                                )
                            )

                            Divider(color = Color.Gray)

                            // Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // Resume session
                                        isPaused = false
                                        showSettings = false
                                        
                                        // Save preferences
                                        lifecycleScope.launch {
                                            userPreferencesRepository.saveBallSize(ballSize)
                                            userPreferencesRepository.saveBallSpeed(minSpeed, maxSpeed)
                                            userPreferencesRepository.saveSessionDuration(sessionDuration)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Resume", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        // Restart session
                                        isSessionActive = true
                                        isPaused = false
                                        showSettings = false
                                        ballCount = 0
                                        remainingTime = sessionDuration * 60
                                        ballX = screenWidth / 2
                                        ballY = screenHeight / 2
                                        lastHorizontalBounce = 0f
                                        lastVerticalBounce = 0f
                                        
                                        // Save preferences
                                        lifecycleScope.launch {
                                            userPreferencesRepository.saveBallSize(ballSize)
                                            userPreferencesRepository.saveBallSpeed(minSpeed, maxSpeed)
                                            userPreferencesRepository.saveSessionDuration(sessionDuration)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                                ) {
                                    Text("Restart", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        // End session and return to main activity
                                        finish()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                                ) {
                                    Text("Exit", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}