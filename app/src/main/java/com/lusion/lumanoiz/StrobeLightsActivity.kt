package com.lusion.lumanoiz

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lusion.lumanoiz.ui.theme.LumaNoizAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class FrequencyRange(val name: String, val minHz: Float, val maxHz: Float)

// DataStore extension
val Context.strobeDataStore: DataStore<Preferences> by preferencesDataStore(name = "strobe_settings")

// Preference keys
val STROBE_FREQUENCY_KEY = floatPreferencesKey("strobe_frequency")
val BALL_SIZE_KEY = floatPreferencesKey("ball_size")

class StrobeSettingsRepository(private val context: Context) {
    val strobeFrequency: Flow<Float> = context.strobeDataStore.data.map { preferences ->
        preferences[STROBE_FREQUENCY_KEY] ?: 8f // Default Alpha range
    }

    val ballSize: Flow<Float> = context.strobeDataStore.data.map { preferences ->
        preferences[BALL_SIZE_KEY] ?: 0.3f // Default 30% of screen
    }

    suspend fun setStrobeFrequency(frequency: Float) {
        context.strobeDataStore.edit { preferences ->
            preferences[STROBE_FREQUENCY_KEY] = frequency
        }
    }

    suspend fun setBallSize(size: Float) {
        context.strobeDataStore.edit { preferences ->
            preferences[BALL_SIZE_KEY] = size
        }
    }
}

class StrobeLightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Hide system UI elements (status bar, navigation bar, etc.)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            LumaNoizAppTheme {
                StrobeLightsScreen()
            }
        }
    }
}

@Composable
fun StrobeLightsScreen() {
    val context = LocalContext.current
    val settingsRepository = remember { StrobeSettingsRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }
    var selectedRange by remember { mutableStateOf<FrequencyRange?>(null) }

    // Define frequency ranges first, before using them
    val frequencyRanges = remember {
        listOf(
            FrequencyRange("Delta", 0.5f, 4f),
            FrequencyRange("Theta", 4f, 8f),
            FrequencyRange("Alpha", 8f, 13f), // Default
            FrequencyRange("Beta", 13f, 30f)
        )
    }

    // Collect settings from DataStore
    val savedFrequency by settingsRepository.strobeFrequency.collectAsState(initial = 8f)
    val savedBallSize by settingsRepository.ballSize.collectAsState(initial = 0.3f)

    var currentFrequency by remember { mutableStateOf(8f) }
    var ballSize by remember { mutableStateOf(0.3f) }

    // Update state when saved values change
    LaunchedEffect(savedFrequency) {
        currentFrequency = savedFrequency
        // Set initial selected range based on saved frequency
        selectedRange = frequencyRanges.find { range ->
            savedFrequency >= range.minHz && savedFrequency <= range.maxHz
        }
    }

    LaunchedEffect(savedBallSize) {
        ballSize = savedBallSize
    }

    // Dynamic speed control - change frequency every 2 seconds within selected range
    LaunchedEffect(selectedRange) {
        selectedRange?.let { range ->
            while (true) {
                kotlinx.coroutines.delay(2000) // Wait 2 seconds
                val newFreq = Random.nextFloat() * (range.maxHz - range.minHz) + range.minHz
                currentFrequency = newFreq
                // Save the new frequency
                settingsRepository.setStrobeFrequency(newFreq)
            }
        }
    }

    // Strobe effect for the ball - ONLY when menu is NOT visible
    LaunchedEffect(currentFrequency, showMenu) {
        if (!showMenu) { // Only strobe when menu is hidden
            val intervalMs = (1000f / currentFrequency / 2f).toLong() // Divide by 2 for on/off cycle
            while (!showMenu) { // Keep checking menu visibility
                isVisible = !isVisible
                kotlinx.coroutines.delay(intervalMs)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showMenu = true },
        contentAlignment = Alignment.Center
    ) {
        // Strobing ball/circle in center
        if (isVisible) {
            BoxWithConstraints {
                val ballDiameter = minOf(maxWidth, maxHeight) * ballSize
                Box(
                    modifier = Modifier
                        .size(ballDiameter)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }

        // Show current frequency in top corner for reference
        Text(
            text = "${currentFrequency.roundToInt()} Hz",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }

    // Pause Menu Dialog
    if (showMenu) {
        Dialog(onDismissRequest = { showMenu = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D2D2D)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Strobe Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Ball Size Slider - Updated range to 100%
                    Text(
                        text = "Ball Size: ${(ballSize * 100).roundToInt()}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Slider(
                        value = ballSize,
                        onValueChange = { newSize ->
                            ballSize = newSize
                        },
                        onValueChangeFinished = {
                            // Save to DataStore when slider interaction is finished
                            coroutineScope.launch {
                                settingsRepository.setBallSize(ballSize)
                            }
                        },
                        valueRange = 0.1f..1.0f, // Updated to allow 100% size
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF6A4A8A),
                            inactiveTrackColor = Color.Gray
                        )
                    )

                    // Frequency Range Buttons
                    Text(
                        text = "Frequency Range",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    frequencyRanges.forEach { range ->
                        Button(
                            onClick = {
                                val randomFreq = Random.nextFloat() * (range.maxHz - range.minHz) + range.minHz
                                currentFrequency = randomFreq
                                selectedRange = range // Set the selected range for dynamic updates
                                // Save to DataStore
                                coroutineScope.launch {
                                    settingsRepository.setStrobeFrequency(randomFreq)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRange == range)
                                    Color(0xFF6A4A8A) else Color(0xFF4A4A4A)
                            )
                        ) {
                            Text(
                                text = "${range.name}: ${range.minHz}-${range.maxHz} Hz",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showMenu = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF666666)
                        )
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }
}
