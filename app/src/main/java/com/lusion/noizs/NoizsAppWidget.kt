package com.lusion.noizs

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.Futures
import java.util.concurrent.Executor

import androidx.core.content.ContextCompat

/**
 * Implementation of App Widget functionality.
 */
class NoizsAppWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_PLAYBACK = "com.lusion.noizs.ACTION_TOGGLE_PLAYBACK"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.noizs_app_widget)

        // Set up the click listener for the widget to open the app
        val appIntent = Intent(context, MainActivity::class.java)
        val appPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.appwidget_text, appPendingIntent) // Clicking text opens app

        // Set up the click listener for the play/pause button
        val sessionToken = SessionToken(context, ComponentName(context, SoundService::class.java))
        val mediaControllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken).buildAsync()

        mediaControllerFuture.addListener(Runnable {
            val mediaController = mediaControllerFuture.get()
            val togglePlaybackPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_TOGGLE_PLAYBACK).setComponent(ComponentName(context, NoizsAppWidget::class.java)),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.play_pause_button, togglePlaybackPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)

            // Send custom command to toggle playback
            val bundle = android.os.Bundle()
            mediaController.sendCustomCommand(androidx.media3.session.SessionCommand(ACTION_TOGGLE_PLAYBACK, bundle), bundle)

        }, ContextCompat.getMainExecutor(context) as Executor)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null && intent?.action == ACTION_TOGGLE_PLAYBACK) {
            val sessionToken = SessionToken(context, ComponentName(context, SoundService::class.java))
            val mediaControllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken).buildAsync()

            mediaControllerFuture.addListener(Runnable {
                val mediaController = mediaControllerFuture.get()
                val bundle = android.os.Bundle()
                mediaController.sendCustomCommand(androidx.media3.session.SessionCommand(ACTION_TOGGLE_PLAYBACK, bundle), bundle)
            }, ContextCompat.getMainExecutor(context) as Executor)
        }
    }
}
