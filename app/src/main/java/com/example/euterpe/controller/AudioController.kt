package com.example.euterpe.controller

import android.content.Context
import android.util.Log
import com.example.euterpe.model.TrackListViewModel

class AudioController{

    companion object{

        fun playbackTrack(context: Context, viewModel: TrackListViewModel){
            if(viewModel.mediaPlayer.value!!.isPlaying){
                viewModel.mediaPlayer.value!!.pause()
                viewModel.setIsPaused(true)
            } else {
                viewModel.mediaPlayer.value!!.seekTo(viewModel.mediaPlayer.value!!.currentPosition)
                viewModel.mediaPlayer.value!!.start()
                viewModel.setIsPaused(false)
            }
        }

        fun playPreviousTrack(context: Context, viewModel: TrackListViewModel){
            val isCurrentlyPlaying = viewModel.mediaPlayer.value!!.isPlaying

            if(viewModel.currentIndex.value!! != 0){
                val currentPosition = viewModel.mediaPlayer.value!!.currentPosition

                if(currentPosition > 10000){
                    viewModel.mediaPlayer.value!!.seekTo(0)
                } else {
                    val track = viewModel.getTrackFromIndex(viewModel.currentIndex.value!! - 1 )
                    viewModel.changeTrack(context, track, viewModel.currentIndex.value!! - 1)

                    if(isCurrentlyPlaying){
                        viewModel.mediaPlayer.value!!.start()
                    }
                }
            }
        }

        fun playNextTrack(context: Context, viewModel: TrackListViewModel){
            Log.i("TrackList", viewModel.currentIndex.value!!.toString())
            Log.i("TrackList", viewModel.playingTrackList.value!!.trackList.size.toString())

            val isCurrentlyPlaying = viewModel.mediaPlayer.value!!.isPlaying

            if(viewModel.currentIndex.value!! + 1 < viewModel.playingTrackList.value!!.trackList.size){
                val track = viewModel.getTrackFromIndex(viewModel.currentIndex.value!! + 1)
                viewModel.changeTrack(context, track, viewModel.currentIndex.value!! + 1)

                if(isCurrentlyPlaying){
                    viewModel.mediaPlayer.value!!.start()
                }
            } else {
                playbackTrack(context, viewModel)
                viewModel.mediaPlayer.value!!.seekTo(viewModel.currentTrack.value!!.duration)
            }
        }
    }
}