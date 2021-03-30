package com.example.euterpe

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.example.euterpe.adapter.MediaReceiver
import com.example.euterpe.adapter.MediaService


class AudioService : MediaBrowserServiceCompat() {
    private lateinit var context: Context
    lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int{
        Log.i("AudioService", "in on start command")
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.i("AudioService", "on load children")
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle? ): BrowserRoot? {
        Log.i("AudioService", "on get root")
        return BrowserRoot("Euterpe", null)
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("AudioService", "On creating mediasession")
        mediaSession = MediaSessionCompat(this, "MusicService").apply{
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            setPlaybackState(stateBuilder.build())
            isActive = true
        }
        mediaSession.setCallback(object: MediaSessionCompat.Callback(){
            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                Log.i("Audio Service", "Cause mediabutton event in session")
                Log.i("Audio Service", mediaButtonEvent.toString())
                Log.i("Audio Service", mediaButtonEvent!!.action.toString())

                val intent = Intent(this@AudioService, MediaReceiver::class.java)
                intent.action = MediaService.ACTION_PLAY
                var pending =  PendingIntent.getBroadcast(this@AudioService, 0, intent, 0)
                pending.send()

                return super.onMediaButtonEvent(mediaButtonEvent)
            }

            override fun onPlay() {
                super.onPlay()
                Log.i("Audio Service", "Cause onplay event")
            }

            override fun onPause() {
                super.onPause()
                Log.i("Audio Service", "Cause mediabutton event")
            }
        })
        
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        val context = applicationContext
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            context, 99 /*request code*/,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        mediaSession.setSessionActivity(pi)
    }

}
