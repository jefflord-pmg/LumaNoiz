package com.lusion.lumanoiz

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class SoundService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    companion object {
        const val ACTION_TOGGLE_PLAYBACK = "com.lusion.lumanoiz.ACTION_TOGGLE_PLAYBACK"
        const val ACTION_PLAY_SOUND = "com.lusion.lumanoiz.ACTION_PLAY_SOUND"
        const val EXTRA_SOUND_RES_ID = "sound_res_id"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "LumaNoizChannel"
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateAllWidgets(isPlaying)
                    }
                })
            }
        mediaSession = MediaSession.Builder(this, player!!)
            .setCallback(object : MediaSession.Callback {})
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_TOGGLE_PLAYBACK -> togglePlayback()
            ACTION_PLAY_SOUND -> {
                val soundResId = intent.getIntExtra(EXTRA_SOUND_RES_ID, -1)
                if (soundResId != -1) {
                    playSound(soundResId)
                }
            }
        }
        return START_STICKY
    }

    private fun togglePlayback() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                if (it.currentMediaItem == null) {
                    val prefs = getSharedPreferences(LumaNoizAppWidget.PREFS_NAME, Context.MODE_PRIVATE)
                    val lastSoundResId = prefs.getInt(LumaNoizAppWidget.PREF_LAST_SOUND_RES_ID, -1)
                    if (lastSoundResId != -1) {
                        playSound(lastSoundResId)
                    }
                } else {
                    it.play()
                }
            }
        }
    }

    private fun playSound(soundResId: Int) {
        val mediaItem = MediaItem.fromUri("android.resource://$packageName/$soundResId")

        // Only set media item and prepare if it's a new sound or player is not ready
        if (player?.currentMediaItem != mediaItem || player?.playbackState == Player.STATE_IDLE) {
            player?.setMediaItem(mediaItem)
            player?.prepare()
        }
        player?.play()

        val prefs = getSharedPreferences(LumaNoizAppWidget.PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putInt(LumaNoizAppWidget.PREF_LAST_SOUND_RES_ID, soundResId)
        prefs.apply()

        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LumaNoiz is playing")
            .setContentText("Playing background noise")
            .setSmallIcon(R.drawable.moon_on)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LumaNoiz Background Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateAllWidgets(isPlaying: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, LumaNoizAppWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        for (appWidgetId in appWidgetIds) {
            LumaNoizAppWidget.updateWidget(this, appWidgetManager, appWidgetId, isPlaying)
        }
    }

    override fun onDestroy() {
        player?.release()
        mediaSession?.release()
        super.onDestroy()
    }
}
