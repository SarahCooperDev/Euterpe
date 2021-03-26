package com.example.euterpe.adapter

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.euterpe.MainActivity
import com.example.euterpe.R
import androidx.media.app.NotificationCompat as MediaNotification

class MediaService{

    companion object{
        val ACTION_PLAY = "action_play"
        val ACTION_PAUSE = "action_pause"
        val ACTION_REWIND = "action_rewind"
        val ACTION_FAST_FORWARD = "action_fast_foward"
        val ACTION_NEXT = "action_next"
        val ACTION_PREVIOUS = "action_previous"
        val ACTION_STOP = "action_stop"

        private val mMediaPlayer: MediaPlayer? = null
        private val mManager: MediaSessionManager? = null
        private val mSession: MediaSession? = null

        private val NOTIFICATION_ID = 0
        private val REQUEST_CODE = 0
        private val FLAGS = 0
        private val notificationChannel: String = "EuterpeChannel"


        fun NotificationManager.sendNotification(messageBody: String, context: Context, mediaSession: MediaSessionCompat, audioReceiver: BroadcastReceiver){
            Log.i("Media Service", "Send notification")
            val contentIntent = Intent(context, MainActivity::class.java)
            val contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val eggImage = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
            val bigPicStyle = NotificationCompat.BigPictureStyle().bigPicture(eggImage).bigLargeIcon(null)

            val pauseIntent = Intent(context, MediaReceiver::class.java)
            pauseIntent.action = ACTION_PAUSE
            val pendingPauseMediaReceiver = PendingIntent.getBroadcast(context, 0, pauseIntent, 0)

            val nextIntent = Intent(context, MediaReceiver::class.java)
            nextIntent.action = ACTION_NEXT
            val pendingNextMediaReceiver = PendingIntent.getBroadcast(context, 0, nextIntent, 0)

            val previousIntent = Intent(context, MediaReceiver::class.java)
            previousIntent.action = ACTION_PREVIOUS
            val pendingPreviousMediaReceiver = PendingIntent.getBroadcast(context, 0, previousIntent, 0)

            val builder = NotificationCompat.Builder(context, notificationChannel)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Euterpe")
                .setContentText(messageBody)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setStyle(bigPicStyle)
                .setLargeIcon(eggImage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(MediaNotification.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(mediaSession.sessionToken))
                .addAction(R.mipmap.ic_previous_btn_dark_foreground, "Previous", pendingPreviousMediaReceiver)
                .addAction(R.mipmap.ic_pause_btn_dark_foreground, "Pause", pendingPauseMediaReceiver)
                .addAction(R.mipmap.ic_next_btn_dark_foreground, "Next", pendingNextMediaReceiver)

            cancelNotifications()

            Log.i("Media Service", "Notify")
            notify(NOTIFICATION_ID, builder.build())
        }

        fun NotificationManager.cancelNotifications(){
            cancelAll()
        }
    }

}