package com.example.euterpe.controller

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.example.euterpe.MainActivity
import com.example.euterpe.adapter.MediaReceiver


class AudioService : MediaBrowserServiceCompat() {
    private val TAG = "Audio Service"
    lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int{
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle? ): BrowserRoot? {
        return BrowserRoot("Euterpe", null)
    }

    private fun createMediaSession(){
        Log.i(TAG, "On creating mediasession")
        mediaSession = MediaSessionCompat(this, "AudioService").apply{
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

        var mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.action = Intent.ACTION_MEDIA_BUTTON
        val mediaButtonReceiverPendingIntent: PendingIntent = PendingIntent.getBroadcast(this@AudioService, 0, mediaButtonIntent, 0)

        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
        mediaSession.setCallback(object: MediaSessionCompat.Callback(){
            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                var keyEvent  = mediaButtonEvent!!.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

                if (keyEvent != null) {
                    if (keyEvent.action == KeyEvent.ACTION_UP)
                    {
                        Log.i(TAG, "Cause mediabutton event in session")
                        Log.i(TAG, mediaButtonEvent!!.action.toString())
                        val intent = Intent(this@AudioService, MediaReceiver::class.java)
                        intent.action = NotificationService.ACTION_PLAY
                        var pending =  PendingIntent.getBroadcast(this@AudioService, 0, intent, 0)
                        pending.send()
                    }
                }

                return super.onMediaButtonEvent(mediaButtonEvent)
            }

            override fun onPlay() {
                super.onPlay()
            }

            override fun onPause() {
                super.onPause()
            }
        })

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        val context = applicationContext
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mediaSession.setSessionActivity(pi)
    }

    override fun onCreate() {
        super.onCreate()
        createMediaSession()
        sessionToken = mediaSession.sessionToken
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}
