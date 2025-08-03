package com.lusion.lumanoiz

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.lusion.lumanoiz.ui.theme.LumaNoizAppTheme
import android.content.Intent
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LumaNoizAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LumaNoizAppScreen()
                }
            }
        }
    }
}

data class Sound(val name: String, val resourceId: Int)

@Composable
fun LumaNoizAppScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
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

    // Read anchor preference from UserPreferencesRepository
    val isAnchoredToBottom by userPreferencesRepository.isAnchoredToBottom.collectAsState(initial = false)

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

    DisposableEffect(Unit) {
        onDispose {
            mediaController?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        coroutineScope.launch {
                            userPreferencesRepository.setAnchoredToBottom(!isAnchoredToBottom)
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isAnchoredToBottom) Arrangement.Bottom else Arrangement.Top
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 180.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sounds) { sound ->
                    SoundCard(
                        sound = sound,
                        isPlaying = sound == currentPlayingSound && isPlaying,
                        onClick = {
                            if (sound == currentPlayingSound) {
                                if (isPlaying) {
                                    mediaController?.pause()
                                } else {
                                    mediaController?.play()
                                }
                                isPlaying = !isPlaying
                            } else {
                                mediaController?.apply {
                                    setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${sound.resourceId}"))
                                    prepare()
                                    play()
                                }
                                currentPlayingSound = sound
                                isPlaying = true
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, LightsActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A4A4A)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Show Lights",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.mipmap.bounce_ball),
                        contentDescription = "Bouncing Ball Icon"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, StrobeLightsActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A4A8A)
                )
            ) {
                Text(
                    text = "Strobe Lights",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SoundCard(sound: Sound, isPlaying: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPlaying) Color.DarkGray else Color(0xFF333333)
        ),
        border = if (isPlaying) BorderStroke(2.dp, Color.White) else null
    ) {
        Text(
            text = sound.name,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LumaNoizAppTheme {
        LumaNoizAppScreen()
    }
}