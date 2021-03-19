package com.example.euterpe.model

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackListViewModel : ViewModel() {
    private val _trackList = MutableLiveData<TrackList>()
    val trackList: LiveData<TrackList> = _trackList

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private var _mediaPlayer = MutableLiveData<MediaPlayer>()
    var mediaPlayer: LiveData<MediaPlayer> = _mediaPlayer

    private var _isRandom = MutableLiveData<Boolean>()
    var isRandom: LiveData<Boolean> = _isRandom

    private var _isPaused = MutableLiveData<Boolean>()
    var isPaused: LiveData<Boolean> = _isPaused

    fun setTrackList(tracks: TrackList){
        _trackList.value = tracks
    }

    private fun setCurrentTrack(track: Track){
        _currentTrack.value = track
    }

    fun setMediaPlayer(mediaP: MediaPlayer){
        _mediaPlayer.value = mediaP
    }

    private fun setIsPaused(isPaused: Boolean){
        _isPaused.value = isPaused
    }

    private fun setCurrentToRandomTrack(){
        val size = _trackList.value!!.getCount() - 1
        val randomNo = (0..size).random()
        var randomTrack = _trackList.value!!.getTrackAtIndex(randomNo)
        setCurrentTrack(randomTrack)
    }

    private fun stopTrack(){
        _mediaPlayer.value!!.stop()
        _mediaPlayer.value!!.reset()
    }

    private fun prepareTrack(context: Context, uri: Uri){
        _mediaPlayer.value!!.setDataSource(context, uri)
        _mediaPlayer.value!!.prepare()
    }

    private fun autoPlayNextTrack(context: Context){
        if(_isRandom.value!!){
            setCurrentToRandomTrack()
            stopTrack()
            prepareTrack(context, _currentTrack.value!!.uri)
            _mediaPlayer.value!!.start()
        }

    }

    fun playOnClick(context: Context, uri: Uri){
        val clickedTrack = _trackList.value!!.trackList.find{ it.uri == uri}
        setCurrentTrack(clickedTrack!!)
        stopTrack()
        prepareTrack(context, uri)
        _mediaPlayer.value!!.start()
    }

    fun playbackTrack(context: Context){
        if(_mediaPlayer.value!!.isPlaying){
            _mediaPlayer.value!!.pause()
            setIsPaused(true)
        } else {
            mediaPlayer.value!!.seekTo(_mediaPlayer.value!!.currentPosition)
            _mediaPlayer.value!!.start()
            setIsPaused(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playNextTrack(context: Context){
        //TODO: implement non-random next

        val isCurrentlyPlaying = _mediaPlayer.value!!.isPlaying

        setCurrentToRandomTrack()
        stopTrack()
        prepareTrack(context, _currentTrack.value!!.uri)

        if(isCurrentlyPlaying){
            _mediaPlayer.value!!.start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context){
        setCurrentToRandomTrack()
        _isRandom.value = true

        _mediaPlayer.value = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        _mediaPlayer.value!!.setOnCompletionListener {
            autoPlayNextTrack(context)
        }

        prepareTrack(context, _currentTrack.value!!.uri)
    }
}