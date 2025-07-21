package com.lusion.lumanoiz

import androidx.media3.session.MediaSessionService

class SoundService : MediaSessionService() {
    override fun onGetSession(controllerInfo: MediaSessionService.MediaSessionServiceCallback.MediaSessionServiceCallbackInfo): androidx.media3.session.MediaSession? {
        // Placeholder implementation
        return null
    }
}