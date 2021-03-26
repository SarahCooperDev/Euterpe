package com.example.euterpe.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.euterpe.adapter.MediastoreAdapter.Companion.createMemberInPlaylist
import com.example.euterpe.adapter.MediastoreAdapter.Companion.deleteMemberInPlaylist
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readPlaylistMembers


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

    private fun setPlayingTrackList(tracks: TrackList){
        _playingTrackList.value = tracks
    }

    private fun setIsRandom(isRandom: Boolean){
        _isRandom.value = isRandom
    }

    private fun setActiveTracks(tracks: MutableList<Track>){
        _playingTrackList.value!!.trackList = tracks
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
        return _playingTrackList.value!!.trackList[index]
    }

    private fun stopTrack(){
        _mediaPlayer.value!!.stop()
        _mediaPlayer.value!!.reset()
    }

    private fun prepareTrack(context: Context, uri: Uri){
        _mediaPlayer.value!!.setDataSource(context, uri)
        _mediaPlayer.value!!.prepare()
    }

    private fun resetPlayingTracklist(){
        val duplicateList = TrackList()
        duplicateList.trackList = _trackList.value!!.trackList.toMutableList()
        setPlayingTrackList(duplicateList)
    }

    private fun shufflePlayingTracklist(){
        _playingTrackList.value!!.trackList.shuffle()
        setCurrentIndex(0)
    }

    private fun orderTracklist(){
        val currentTrack = _currentTrack.value!!
        resetPlayingTracklist()

        var index = _playingTrackList.value!!.trackList.indexOf(currentTrack)
        setCurrentTrack(currentTrack)
        setCurrentIndex(index)
    }

    fun reorderTracklist(order: String){
        Log.i("Reordering", "Reordering tracklist")
        Log.i("Reordering", order.toString())
        Log.i("Reordering", _currentOrder.value!!.toString())
        when (order) {
            "Alphabetical" -> {
                if(_currentOrder.value!! == "Alphabetical") {
                    setCurrentOrder("Reverse Alphabetical")
                     _viewTrackList.value!!.trackList.sortByDescending { it.title }
                    setViewTrackList(_viewTrackList.value!!)
                } else {
                    setCurrentOrder("Alphabetical")
                    _viewTrackList.value!!.trackList.sortBy { it.title }
                    setViewTrackList(_viewTrackList.value!!)
                }
            }
            "Artist" -> {
                if(_currentOrder.value!! == "Artist") {
                    setCurrentOrder("Reverse Artist")
                    _viewTrackList.value!!.trackList.sortByDescending { it.artist }
                    setViewTrackList(_viewTrackList.value!!)
                } else {
                    setCurrentOrder("Artist")
                    _viewTrackList.value!!.trackList.sortBy { it.artist }
                    setViewTrackList(_viewTrackList.value!!)
                }
            }
            "Album" -> {
                if(_currentOrder.value!! == "Album") {
                    setCurrentOrder("Reverse Album")
                    _viewTrackList.value!!.trackList.sortByDescending { it.album }
                    setViewTrackList(_viewTrackList.value!!)
                } else {
                    setCurrentOrder("Album")
                    _viewTrackList.value!!.trackList.sortBy { it.album }
                    setViewTrackList(_viewTrackList.value!!)
                }
            }
            "Recently" -> {
                if(_currentOrder.value!! == "Recently") {
                    setCurrentOrder("Reverse Recently")
                    _viewTrackList.value!!.trackList.sortBy { it.dateAdded }
                    setViewTrackList(_viewTrackList.value!!)
                } else {
                    setCurrentOrder("Recently")
                    _viewTrackList.value!!.trackList.sortByDescending { it.dateAdded }
                    setViewTrackList(_viewTrackList.value!!)
                }
            }
        }

        if(!_isRandom.value!!){
            Log.i("TrackList ViewModel", "Is not random")
            val currentTrack = _currentTrack.value!!
            setPlayingTrackList(_viewTrackList.value!!)

            var index = _playingTrackList.value!!.trackList.indexOf(currentTrack)
            setCurrentTrack(currentTrack)
            setCurrentIndex(index)
        }

    }

    fun setPlayingTracklistToFav(){
        resetPlayingTracklist()

        var favouritePlaylist = playlists.value!!.find{it.name == "Favourites"}
        var newPlayingList = trackList.value!!.trackList.filter {favouritePlaylist!!.members.contains(it.id)}
        playingTrackList.value!!.trackList = newPlayingList.toMutableList()

        setPlayingTrackList(playingTrackList.value!!)
    }

    private fun changeTrack(context: Context, newCurrentTrack: Track, newIndex: Int){
        setCurrentTrack(newCurrentTrack)
        setCurrentIndex(newIndex)
        stopTrack()
        prepareTrack(context, newCurrentTrack.uri)
    }

    fun switchRandom(){
        if(_isRandom.value!!){
            setIsRandom(false)
            orderTracklist()
        } else {
            setIsRandom(true)
            resetPlayingTracklist()
            shufflePlayingTracklist()
        }
    }

    fun adjustCurrentPosition(seekBar: SeekBar){
        mediaPlayer.value!!.seekTo(seekBar.progress)
    }

    fun toggleFavouriteTrack(context: Context){
        currentTrack.value!!.isFavourited = !currentTrack.value!!.isFavourited
        var playlist = playlists.value!!.find{it.name == "Favourites"}

        if(currentTrack.value!!.isFavourited) {
            createMemberInPlaylist(context, playlist!!.id, currentTrack.value!!)
            readPlaylistMembers(context, this, playlist!!.id)
        } else {
            deleteMemberInPlaylist(context, playlist!!.id, currentTrack.value!!)
            readPlaylistMembers(context, this, playlist!!.id)
        }

        var allPlaylists = playlists.value
        setPlaylists(allPlaylists!!)

        var changingTrack = currentTrack.value
        setCurrentTrack(changingTrack!!)

        Log.i("Favourited", currentTrack.value!!.isFavourited.toString())
    }

    fun playPlaylistOnClick(context: Context, uri: Uri){
        val clickedTrack = _trackList.value!!.trackList.find{ it.uri == uri}
        if(_isRandom.value!!){
            _playingTrackList.value!!.trackList.shuffle()
            setCurrentIndex(0)

            _playingTrackList.value!!.trackList.remove(clickedTrack)
            _playingTrackList.value!!.trackList.add(0, clickedTrack!!)

            changeTrack(context, clickedTrack, 0)
        } else {
            var index = _playingTrackList.value!!.trackList.indexOf(clickedTrack)
            changeTrack(context, clickedTrack!!, index)
        }

        _mediaPlayer.value!!.start()
        setIsPaused(false)
    }

    fun playOnClick(context: Context, uri: Uri){
        val clickedTrack = _trackList.value!!.trackList.find{ it.uri == uri}
        resetPlayingTracklist()

        if(_isRandom.value!!){
            shufflePlayingTracklist()

            _playingTrackList.value!!.trackList.remove(clickedTrack)
            _playingTrackList.value!!.trackList.add(0, clickedTrack!!)

            changeTrack(context, clickedTrack, 0)
        } else {
            var index = _playingTrackList.value!!.trackList.indexOf(clickedTrack)
            changeTrack(context, clickedTrack!!, index)
        }

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
            val currentPosition = _mediaPlayer.value!!.currentPosition

            if(currentPosition > 10000){
                mediaPlayer.value!!.seekTo(0)
            } else {
                val track = getTrackFromIndex(_currentIndex.value!! - 1 )
                changeTrack(context, track, _currentIndex.value!! - 1)

                if(isCurrentlyPlaying){
                    _mediaPlayer.value!!.start()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playNextTrack(context: Context){
        Log.i("TrackList", _currentIndex.value!!.toString())
        Log.i("TrackList", _playingTrackList.value!!.trackList.size.toString())

        val isCurrentlyPlaying = _mediaPlayer.value!!.isPlaying

        if(_currentIndex.value!! + 1 < _playingTrackList.value!!.trackList.size){
            val track = getTrackFromIndex(_currentIndex.value!! + 1)
            changeTrack(context, track, _currentIndex.value!! + 1)

            if(isCurrentlyPlaying){
                _mediaPlayer.value!!.start()
            }
        } else {
            playbackTrack(context)
            mediaPlayer.value!!.seekTo(_currentTrack.value!!.duration)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun autoplayNextTrack(context: Context){
        if(_currentIndex.value!! + 1 < _playingTrackList.value!!.trackList.size) {
            val track = getTrackFromIndex(_currentIndex.value!! + 1)
            changeTrack(context, track, _currentIndex.value!! + 1)
            _mediaPlayer.value!!.start()
        } else {
            val isCurrentlyPlaying = _mediaPlayer.value!!.isPlaying
            playbackTrack(context)
            mediaPlayer.value!!.seekTo(_currentTrack.value!!.duration)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context){
        setViewTrackList(trackList.value!!)
        resetPlayingTracklist()
        setPlaylists(arrayListOf<Playlist>())

        _isRandom.value = true
        _currentPlaylist.value = "Tracks"
        _currentOrder.value = "Alphabetical"
        shufflePlayingTracklist()
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
            Log.i("TrackListViewModel", "Autoplaying next song")
            Log.i("TrackListViewModel", _mediaPlayer.value!!.isPlaying.toString())
            autoplayNextTrack(context)
        }

        setCurrentIndex(0)
        val track = getTrackFromIndex(_currentIndex.value!!)
        setCurrentTrack(track)

        prepareTrack(context, track.uri)
    }
}