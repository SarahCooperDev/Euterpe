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
        Log.i("Broadcast Receiver", "In Media Receiver")

        if(ACTION_PAUSE == intent!!.action){
            Log.i("Broadcast Receiver", "Pause Button")
            val pauseIntent = Intent(context, MediaReceiver::class.java)
            pauseIntent.action = MediaService.ACTION_PAUSE
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(pauseIntent)
        } else if(ACTION_NEXT == intent!!.action){
            Log.i("Broadcast Receiver", "Next Button")
            val nextIntent = Intent(context, MediaReceiver::class.java)
            nextIntent.action = MediaService.ACTION_NEXT
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(nextIntent)
        } else if(ACTION_PREVIOUS == intent!!.action){
            Log.i("Broadcast Receiver", "Previous Button")
            val previousIntent = Intent(context, MediaReceiver::class.java)
            previousIntent.action = MediaService.ACTION_PREVIOUS
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(previousIntent)
        } else {
            Log.i("Broadcast Receiver", "Couldn't categorise intent")
        }

    }

}