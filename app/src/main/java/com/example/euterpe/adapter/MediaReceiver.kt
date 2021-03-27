package com.example.euterpe.adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.euterpe.model.TrackListViewModel

class MediaReceiver: BroadcastReceiver(){
    val ACTION_PLAY = "action_play"
    val ACTION_PAUSE = "action_pause"
    val ACTION_REWIND = "action_rewind"
    val ACTION_FAST_FORWARD = "action_fast_foward"
    val ACTION_NEXT = "action_next"
    val ACTION_PREVIOUS = "action_previous"
    val ACTION_STOP = "action_stop"

    private val REQUEST_CODE = 0
    private val NOTIFICATION_ID = 0
    private var  viewModel: TrackListViewModel? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Media Receiver", intent!!.action.toString())
        when {
            ACTION_PAUSE == intent!!.action -> {
                val pauseIntent = Intent(context, MediaReceiver::class.java)
                pauseIntent.action = MediaService.ACTION_PAUSE
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(pauseIntent)
            }
            ACTION_NEXT == intent!!.action -> {
                val nextIntent = Intent(context, MediaReceiver::class.java)
                nextIntent.action = MediaService.ACTION_NEXT
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(nextIntent)
            }
            ACTION_PREVIOUS == intent!!.action -> {
                val previousIntent = Intent(context, MediaReceiver::class.java)
                previousIntent.action = MediaService.ACTION_PREVIOUS
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(previousIntent)
            }
            ACTION_PLAY == intent!!.action -> {
                val playIntent = Intent(context, MediaReceiver::class.java)
                playIntent.action = MediaService.ACTION_PLAY
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(playIntent)
            }
            else -> {
                Log.i("Broadcast Receiver", "Couldn't categorise intent")
            }
        }

    }

}