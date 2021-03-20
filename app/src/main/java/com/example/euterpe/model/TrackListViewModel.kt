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

    private val _activeTrackList = MutableLiveData<TrackList>()
    val activeTrackList: LiveData<TrackList> = _activeTrackList

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private val _currentIndex = MutableLiveData<Int>()
    val currentIndex: LiveData<Int> = _currentIndex

    private var _mediaPlayer = MutableLiveData<MediaPlayer>()
    var mediaPlayer: LiveData<MediaPlayer> = _mediaPlayer

    private var _isRandom = MutableLiveData<Boolean>()
    var isRandom: LiveData<Boolean> = _isRandom

    private var _isPaused = MutableLiveData<Boolean>()
    var isPaused: LiveData<Boolean> = _isPaused

    fun setTrackList(tracks: TrackList){
        _trackList.value = tracks
    }

    private fun setActiveTrackList(tracks: TrackList){
        _activeTrackList.value = tracks
    }

    private fun setActiveTracks(tracks: MutableList<Track>){
        _activeTrackList.value!!.trackList = tracks
    }

    private fun setCurrentTrack(track: Track){
        _currentTrack.value = track
    }

    private fun setCurrentIndex(index: Int){
        _currentIndex.value = index
    }

    fun setMediaPlayer(mediaP: MediaPlayer){
        _mediaPlayer.value = mediaP
    }

    private fun setIsPaused(isPaused: Boolean){
        _isPaused.value = isPaused
    }

    private fun getTrackFromIndex(index: Int): Track{
        return _activeTrackList.value!!.trackList[index]
    }

    private fun stopTrack(){
        _mediaPlayer.value!!.stop()
        _mediaPlayer.value!!.reset()
    }

    private fun prepareTrack(context: Context, uri: Uri){
        _mediaPlayer.value!!.setDataSource(context, uri)
        _mediaPlayer.value!!.prepare()
    }

    private fun resetActiveTracklist(){
        val duplicateList = TrackList()
        duplicateList.trackList = _trackList.value!!.trackList.toMutableList()
        setActiveTrackList(duplicateList)
    }

    private fun shuffleTracklist(){
        resetActiveTracklist()
        _activeTrackList.value!!.trackList.shuffle()
        setCurrentIndex(0)
    }

    fun playOnClick(context: Context, uri: Uri){
        shuffleTracklist()

        val clickedTrack = _activeTrackList.value!!.trackList.find{ it.uri == uri}
        _activeTrackList.value!!.trackList.remove(clickedTrack)
        _activeTrackList.value!!.trackList.add(0, clickedTrack!!)

        setCurrentTrack(clickedTrack!!)
        setCurrentIndex(0)
        stopTrack()
        prepareTrack(context, uri)
        _mediaPlayer.value!!.start()
        setIsPaused(false)
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

    fun playPreviousTrack(context: Context){
        val isCurrentlyPlaying = _mediaPlayer.value!!.isPlaying

        if(_currentIndex.value!! != 0){
            val track = getTrackFromIndex(_currentIndex.value!! - 1 )
            setCurrentTrack(track)
            setCurrentIndex(_currentIndex.value!! - 1)
            stopTrack()
            prepareTrack(context, _currentTrack.value!!.uri)

            if(isCurrentlyPlaying){
                _mediaPlayer.value!!.start()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playNextTrack(context: Context){
        val isCurrentlyPlaying = _mediaPlayer.value!!.isPlaying

        val track = getTrackFromIndex(_currentIndex.value!! + 1)
        setCurrentTrack(track)
        setCurrentIndex(_currentIndex.value!! + 1)
        stopTrack()
        prepareTrack(context, _currentTrack.value!!.uri)

        if(isCurrentlyPlaying){
            _mediaPlayer.value!!.start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context){
        resetActiveTracklist()

        _isRandom.value = true
        shuffleTracklist()
        setIsPaused(true)

        _mediaPlayer.value = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        _mediaPlayer.value!!.setOnCompletionListener {
            //autoPlayNextTrack(context)
            playNextTrack(context)
        }

        val track = getTrackFromIndex(_currentIndex.value!!)
        setCurrentTrack(track)
        prepareTrack(context, track.uri)
    }
}