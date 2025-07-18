package com.lusion.noizs

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.lusion.noizs.ui.theme.NoizsAppTheme
import android.content.Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NoizsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoizsAppScreen()
                }
            }
        }
    }
}

data class Sound(val name: String, val resourceId: Int)

@Composable
fun NoizsAppScreen() {
    val context = LocalContext.current
    val sounds = remember {
        listOf(
            Sound("White Noise", R.raw.white_noise),
            Sound("Brown Noise", R.raw.brown_noise),
            Sound("Grey Noise", R.raw.grey_noise),
            Sound("Pink Noise", R.raw.pink_noise),
            Sound("Relaxing Smoothed Brown Noise", R.raw.relaxing_smoothed_brown_noise),
            Sound("Soft Brown Noise", R.raw.soft_brown_noise)
        )
    }

    var currentPlayingSound by remember { mutableStateOf<Sound?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Media3 Controller setup
    var mediaController by remember { mutableStateOf<MediaController?>(null) }

    LaunchedEffect(context) {
        val sessionToken = SessionToken(context, ComponentName(context, SoundService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    // Observe playback state from MediaController
    LaunchedEffect(mediaController) {
        mediaController?.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying = isPlayingValue
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentPlayingSound = mediaItem?.mediaMetadata?.title?.toString()?.let { name ->
                    sounds.find { it.name == name }
                }
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Noizs App",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sounds) { sound ->
                SoundCard(sound = sound, isEnabled = mediaController != null) {
                    Log.d("NoizsApp", "SoundCard clicked: ${sound.name}")
                    val mediaItem = MediaItem.Builder()
                        .setMediaId(sound.resourceId.toString())
                        .setUri("android.resource://" + context.packageName + "/" + sound.resourceId)
                        .setMediaMetadata(
                            androidx.media3.common.MediaMetadata.Builder()
                                .setTitle(sound.name)
                                .build()
                        )
                        .build()
                    mediaController?.setMediaItem(mediaItem)
                    mediaController?.prepare()
                    mediaController?.play()
                    currentPlayingSound = sound

                    // Save the selected sound to SharedPreferences
                    val prefs = context.getSharedPreferences(NoizsAppWidget.PREFS_NAME, Context.MODE_PRIVATE)
                    with(prefs.edit()) {
                        putInt(NoizsAppWidget.PREF_LAST_SOUND_RES_ID, sound.resourceId)
                        apply()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Playback Controls
        if (currentPlayingSound != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Playing: ${currentPlayingSound?.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = {
                        if (isPlaying) {
                            mediaController?.pause()
                        } else {
                            mediaController?.play()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
                ) {
                    Text(text = if (isPlaying) "Pause" else "Play", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SoundCard(sound: Sound, isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF333333),
            disabledContainerColor = Color(0xFF666666)
        )
    ) {
        Text(
            text = sound.name,
            color = if (isEnabled) Color.White else Color.Gray,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoizsAppTheme {
        NoizsAppScreen()
    }
}