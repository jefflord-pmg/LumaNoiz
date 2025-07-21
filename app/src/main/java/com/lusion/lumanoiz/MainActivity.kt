package com.lusion.lumanoiz

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private var mediaController: MediaController? = null
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    data class SoundItem(
        val name: String,
        val displayName: String,
        val resourceId: Int
    )

    private val soundItems = listOf(
        SoundItem("white_noise", "White Noise", R.raw.white_noise),
        SoundItem("brown_noise", "Brown Noise", R.raw.brown_noise),
        SoundItem("grey_noise", "Grey Noise", R.raw.grey_noise),
        SoundItem("pink_noise", "Pink Noise", R.raw.pink_noise),
        SoundItem("relaxing_smoothed_brown_noise", "Relaxing Smoothed Brown Noise", R.raw.relaxing_smoothed_brown_noise),
        SoundItem("soft_brown_noise", "Soft Brown Noise", R.raw.soft_brown_noise)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        userPreferencesRepository = UserPreferencesRepository(this)
        
        // Initialize Media Controller
        val sessionToken = SessionToken(this, ComponentName(this, SoundService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, MoreExecutors.directExecutor())

        setContent {
            LumaNoizTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LumaNoiz",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(soundItems) { soundItem ->
                    SoundCard(
                        soundItem = soundItem,
                        onSoundSelected = { playSound(it) }
                    )
                }
            }

            Button(
                onClick = {
                    val intent = Intent(context, LightsActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(
                    text = "Show Lights",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }

    @Composable
    fun SoundCard(
        soundItem: SoundItem,
        onSoundSelected: (SoundItem) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { onSoundSelected(soundItem) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = soundItem.displayName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    private fun playSound(soundItem: SoundItem) {
        lifecycleScope.launch {
            userPreferencesRepository.saveLastSelectedSound(soundItem.name)
        }
        
        val uri = "android.resource://$packageName/${soundItem.resourceId}"
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(soundItem.displayName)
                    .build()
            )
            .build()

        mediaController?.let { controller ->
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }

        // Update widget state
        LumaNoizAppWidget.updateAllWidgets(this, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaController.releaseFuture(mediaController?.applicationLooper?.let { 
            com.google.common.util.concurrent.Futures.immediateFuture(mediaController)
        } ?: com.google.common.util.concurrent.Futures.immediateFuture(null))
    }
}

@Composable
fun LumaNoizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}