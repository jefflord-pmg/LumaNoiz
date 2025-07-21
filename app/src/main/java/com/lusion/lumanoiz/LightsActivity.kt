package com.lusion.lumanoiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lusion.lumanoiz.ui.theme.LumaNoizAppTheme

class LightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LumaNoizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    LightsScreen()
                }
            }
        }
    }
}

@Composable
fun LightsScreen() {
    var showInstructions by remember { mutableStateOf(true) }
    var isGameStarted by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main lights content will go here when game starts
        if (isGameStarted) {
            LightsGameContent()
        }
        
        // Show instructions dialog on first launch
        if (showInstructions) {
            InstructionsDialog(
                onStartGame = {
                    showInstructions = false
                    isGameStarted = true
                }
            )
        }
    }
}

@Composable
fun InstructionsDialog(onStartGame: () -> Unit) {
    Dialog(onDismissRequest = { /* Prevent dismissal */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LumaNoiz - Lights",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = """
                        You will see gently moving light to help relax your mind by stimulating calm brainwave activity through closed eyelids. Simply close your eyes and let the soft motion guide your focus.
                        
                        A light will slowly travel left and right across the screen. Your goal is to count how many times the light passes in front of your closed eyes.
                        
                        You can hold anywhere on the screen (long press) to pause the light and view settings, time remaining, and how many passes you've counted so far.
                        
                        It's a simple, soothing game of counting electric sheep. Relax, breathe, and enjoy
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onStartGame,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start")
                }
            }
        }
    }
}

@Composable
fun LightsGameContent() {
    var isPaused by remember { mutableStateOf(false) }
    var passCount by remember { mutableStateOf(0) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isPaused = !isPaused
                    }
                )
            }
    ) {
        // Placeholder for the moving light animation
        if (!isPaused) {
            Text(
                text = "Moving light will go here...\n(Long press to pause)",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        } else {
            // Pause overlay with settings
            PauseOverlay(
                passCount = passCount,
                onResume = { isPaused = false }
            )
        }
    }
}

@Composable
fun PauseOverlay(passCount: Int, onResume: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Paused",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Passes counted: $passCount",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Time remaining: 5:00", // Placeholder
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Resume")
                }
            }
        }
    }
}