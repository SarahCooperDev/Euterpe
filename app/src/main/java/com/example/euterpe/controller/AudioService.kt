package com.example.euterpe.controller

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat


class AudioService : MediaBrowserServiceCompat() {
    private val TAG = "Audio Service"
    lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int{
        //MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle? ): BrowserRoot? {
        return BrowserRoot("Euterpe", null)
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "setting token " + AudioController.getMSession()!!.sessionToken.toString())
        sessionToken = AudioController.getMSession()!!.sessionToken
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
