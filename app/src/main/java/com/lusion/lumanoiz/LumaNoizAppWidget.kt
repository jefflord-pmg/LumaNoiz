package com.lusion.lumanoiz

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat

class LumaNoizAppWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_PLAYBACK = "com.lusion.lumanoiz.ACTION_TOGGLE_PLAYBACK"
        const val PREFS_NAME = "LumaNoizAppWidgetPrefs"
        const val PREF_LAST_SOUND_RES_ID = "last_sound_res_id"

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, isPlaying: Boolean) {
            val views = RemoteViews(context.packageName, R.layout.lumanoiz_app_widget)
            val imageRes = if (isPlaying) R.drawable.moon_on else R.drawable.moon_off
            views.setImageViewResource(R.id.widget_button, imageRes)

            val intent = Intent(context, LumaNoizAppWidget::class.java).apply {
                action = ACTION_TOGGLE_PLAYBACK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            // Update the widget with the initial state (not playing)
            updateWidget(context, appWidgetManager, appWidgetId, false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE_PLAYBACK) {
            val serviceIntent = Intent(context, SoundService::class.java).apply {
                action = SoundService.ACTION_TOGGLE_PLAYBACK
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
