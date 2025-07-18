package com.lusion.noizs

import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class SoundService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onCreate() {
        super.onCreate()
        Log.d("NoizsApp", "SoundService onCreate")
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .build().apply {
                repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
            }
        mediaSession = MediaSession.Builder(this, player!!)
            .setCallback(object : MediaSession.Callback {})
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NoizsApp", "SoundService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("NoizsApp", "SoundService onDestroy")
        player?.release()
        mediaSession?.release()
        super.onDestroy()
    }
}
