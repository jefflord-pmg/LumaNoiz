package com.lusion.lumanoiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

class LightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightsActivityContent()
        }
    }
}

@Composable
fun LightsActivityContent() {
    var showInstructions by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (showInstructions) {
            InstructionsDialog(
                onDismiss = { showInstructions = false }
            )
        } else {
            // Placeholder for the actual lights/ball animation
            // This will be the main lights activity functionality
            LightsActivityMain()
        }
    }
}

@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { /* Prevent dismissing by tapping outside */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Lights",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "You will see gently moving light to help relax your mind by stimulating calm brainwave activity through closed eyelids. Simply close your eyes and let the soft motion guide your focus.",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "A light will slowly travel left and right across the screen. Your goal is to count how many times the light passes in front of your closed eyes.",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "You can hold anywhere on the screen (long press) to pause the light and view settings, time remaining, and how many passes you've counted so far.",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "It's a simple, soothing game of counting electric sheep. Relax, breathe, and enjoy the rhythm.",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = "Start",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LightsActivityMain() {
    // Placeholder for the main lights activity
    // This would contain the moving light animation
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Lights Activity - Animation would go here",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}