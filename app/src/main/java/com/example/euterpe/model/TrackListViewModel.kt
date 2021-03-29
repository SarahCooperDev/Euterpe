package com.example.euterpe.model

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.euterpe.controller.AudioController


class TrackListViewModel : ViewModel() {
    private val _trackList = MutableLiveData<TrackList>()
    val trackList: LiveData<TrackList> = _trackList

    private val _viewTrackList = MutableLiveData<TrackList>()
    val viewTrackList: LiveData<TrackList> = _viewTrackList

    private val _playingTrackList = MutableLiveData<TrackList>()
    val playingTrackList: LiveData<TrackList> = _playingTrackList

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private val _currentIndex = MutableLiveData<Int>()
    val currentIndex: LiveData<Int> = _currentIndex

    private var _currentOrder = MutableLiveData<String>()
    var currentOrder: LiveData<String> = _currentOrder

    private var _currentPlaylist = MutableLiveData<String>()
    var currentPlaylist: LiveData<String> = _currentPlaylist

    private var _mediaPlayer = MutableLiveData<MediaPlayer>()
    var mediaPlayer: LiveData<MediaPlayer> = _mediaPlayer

    private var _isRandom = MutableLiveData<Boolean>()
    var isRandom: LiveData<Boolean> = _isRandom

    private var _isPaused = MutableLiveData<Boolean>()
    var isPaused: LiveData<Boolean> = _isPaused

    private var _playlists = MutableLiveData<MutableList<Playlist>>()
    var playlists: LiveData<MutableList<Playlist>> = _playlists

    fun addToPlaylist(playlist: Playlist){
        _playlists.value!!.add(playlist)
    }

    fun setPlaylists(playlists: MutableList<Playlist>){
        _playlists.value = playlists
    }

    fun setTrackList(tracks: TrackList){
        _trackList.value = tracks
    }

    fun setCurrentOrder(order: String){
        _currentOrder.value = order
    }

    fun setCurrentPlaylist(current: String){
        _currentPlaylist.value = current
    }

    fun setViewTrackList(tracks: TrackList){
        _viewTrackList.value = tracks
    }

    fun setPlayingTrackList(tracks: TrackList){
        _playingTrackList.value = tracks
    }

    fun setIsRandom(isRandom: Boolean){
        _isRandom.value = isRandom
    }

    fun setActiveTracks(tracks: MutableList<Track>){
        _playingTrackList.value!!.trackList = tracks
    }

    fun setCurrentTrack(track: Track){
        _currentTrack.value = track
    }

    fun setCurrentIndex(index: Int){
        _currentIndex.value = index
    }

    fun setMediaPlayer(mediaP: MediaPlayer){
        _mediaPlayer.value = mediaP
    }

    fun setIsPaused(isPaused: Boolean){
        _isPaused.value = isPaused
    }

    fun getTrackFromIndex(index: Int): Track{
        return _playingTrackList.value!!.trackList[index]
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context){
        setViewTrackList(trackList.value!!)
        AudioController.resetPlayingTracklist(this)
        setPlaylists(arrayListOf<Playlist>())

        _isRandom.value = true
        _currentPlaylist.value = "Tracks"
        _currentOrder.value = "Alphabetical"
        AudioController.shufflePlayingTracklist(this)
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
            AudioController.autoplayNextTrack(context, this)
        }

        val track = getTrackFromIndex(0)
        AudioController.changeTrack(context, this, track, 0)
    }
}