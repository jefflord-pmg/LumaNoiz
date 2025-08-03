package com.lusion.lumanoiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.lusion.lumanoiz.ui.theme.LumaNoizAppTheme
import kotlin.math.roundToInt
import kotlin.random.Random

data class FrequencyRange(val name: String, val minHz: Float, val maxHz: Float)

class StrobeLightsActivity : ComponentActivity() {
    private var strobeHandler: Handler? = null
    private var strobeRunnable: Runnable? = null
    private var isStrobing = false
    private var currentFrequency = 8f // Default Alpha range frequency

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            LumaNoizAppTheme {
                StrobeLightsScreen(
                    onFrequencyChange = { frequency ->
                        currentFrequency = frequency
                        if (isStrobing) {
                            stopStrobe()
                            startStrobe(frequency)
                        }
                    }
                )
            }
        }

        // Start strobing immediately with default frequency
        startStrobe(currentFrequency)
    }

    override fun onResume() {
        super.onResume()
        if (!isStrobing) {
            startStrobe(currentFrequency)
        }
    }

    override fun onPause() {
        super.onPause()
        stopStrobe()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStrobe()
    }

    private fun startStrobe(frequencyHz: Float) {
        stopStrobe() // Stop any existing strobe

        strobeHandler = Handler(Looper.getMainLooper())
        val intervalMs = (1000f / frequencyHz).toLong()

        strobeRunnable = object : Runnable {
            override fun run() {
                // The strobe effect is handled by the Compose UI
                strobeHandler?.postDelayed(this, intervalMs)
            }
        }

        isStrobing = true
        strobeHandler?.post(strobeRunnable!!)
    }

    private fun stopStrobe() {
        strobeHandler?.removeCallbacks(strobeRunnable ?: return)
        strobeHandler = null
        strobeRunnable = null
        isStrobing = false
    }
}

@Composable
fun StrobeLightsScreen(onFrequencyChange: (Float) -> Unit) {
    var isWhite by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var currentFrequency by remember { mutableStateOf(8f) }

    val frequencyRanges = remember {
        listOf(
            FrequencyRange("Delta", 0.5f, 4f),
            FrequencyRange("Theta", 4f, 8f),
            FrequencyRange("Alpha", 8f, 13f), // Default
            FrequencyRange("Beta", 13f, 30f)
        )
    }

    // Strobe effect
    LaunchedEffect(currentFrequency) {
        val intervalMs = (1000f / currentFrequency).toLong()
        while (true) {
            isWhite = !isWhite
            kotlinx.coroutines.delay(intervalMs)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isWhite) Color.White else Color.Black)
            .clickable { showMenu = true },
        contentAlignment = Alignment.Center
    ) {
        // White dot in center (visible when background is black)
        if (!isWhite) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        // Show current frequency in top corner for debugging
        Text(
            text = "${currentFrequency.roundToInt()} Hz",
            color = if (isWhite) Color.Black else Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
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
                        text = "Strobe Frequency",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    frequencyRanges.forEach { range ->
                        Button(
                            onClick = {
                                val randomFreq = Random.nextFloat() * (range.maxHz - range.minHz) + range.minHz
                                currentFrequency = randomFreq
                                onFrequencyChange(randomFreq)
                                showMenu = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentFrequency >= range.minHz && currentFrequency <= range.maxHz)
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
