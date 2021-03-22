package com.example.euterpe.model

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.SeekBar
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

    private fun setIsRandom(isRandom: Boolean){
        _isRandom.value = isRandom
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

    private fun orderTracklist(){
        val currentTrack = _currentTrack.value!!
        resetActiveTracklist()

        var index = _activeTrackList.value!!.trackList.indexOf(currentTrack)
        setCurrentTrack(currentTrack)
        setCurrentIndex(index)
    }

    private fun changeTrack(context: Context, newCurrentTrack: Track, newIndex: Int){
        setCurrentTrack(newCurrentTrack)
        setCurrentIndex(newIndex)
        stopTrack()
        prepareTrack(context, newCurrentTrack.uri)
    }

    fun createPlaylist(activity: Activity, name: String): Long{
        var cv = ContentValues()
        var resolver = activity.getContentResolver()
        cv.put(MediaStore.Audio.Playlists.NAME, name);
        cv.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
        cv.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
        var uri: Uri? = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cv)

        if(uri == null){
            Log.i("Selection Fragment", "Failed to insert playlist")
        }
        var idString = uri!!.lastPathSegment
        if(idString == null){
            Log.i("Selection Fragment", "Failed to parse uri last segment")
        }

        return idString!!.toLong()
    }

    fun addMemberToPlaylist(context: Context, playlistId: Long){
        Log.i("Add Member", "Adding member to playlist")
        var uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        var columns = arrayOf<String>(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER
        )

        var resolver = context.getContentResolver()
        var contentValues = ContentValues()
        var cursor = resolver.query(uri, columns, null, null, null)
        var playOrder = 0

        if (cursor != null) {
            cursor.moveToFirst()
            playOrder = cursor.getInt(0)
            cursor.close()

            contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, currentTrack.value!!.id);
            contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, (playOrder + currentTrack.value!!.id).toInt())

            resolver.insert(uri, contentValues);
        }
    }

    fun removeMemberFromPlaylist(context: Context, playlistId: Long){
        Log.i("Remove member", "Deleting member from playlist")
        var uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        var columns = arrayOf<String>(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER
        )

        var resolver = context.getContentResolver()
        var contentValues = ContentValues()
        var cursor = resolver.query(uri, columns, null, null, null)

        if(cursor != null){
            cursor.moveToFirst();
            var base = cursor.getInt(0)
            cursor.close()

            resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + currentTrack.value!!.id, null);
        }
    }

    fun switchRandom(){
        if(_isRandom.value!!){
            setIsRandom(false)
            orderTracklist()
        } else {
            setIsRandom(true)
            shuffleTracklist()
        }
    }

    fun adjustCurrentPosition(seekBar: SeekBar){
        mediaPlayer.value!!.seekTo(seekBar.progress)
    }

    fun toggleFavouriteTrack(context: Context){
        Log.i("Favourited", currentTrack.value!!.isFavourited.toString())
        currentTrack.value!!.isFavourited = !currentTrack.value!!.isFavourited
        var playlist = playlists.value!!.find{it.name == "Favourites"}

        if(currentTrack.value!!.isFavourited) {
            addMemberToPlaylist(context, playlist!!.id)
        } else {
            removeMemberFromPlaylist(context, playlist!!.id)
        }

        Log.i("Favourited", currentTrack.value!!.isFavourited.toString())
    }

    fun playOnClick(context: Context, uri: Uri){
        val clickedTrack = _activeTrackList.value!!.trackList.find{ it.uri == uri}
        if(_isRandom.value!!){
            shuffleTracklist()

            _activeTrackList.value!!.trackList.remove(clickedTrack)
            _activeTrackList.value!!.trackList.add(0, clickedTrack!!)

            changeTrack(context, clickedTrack, 0)
        } else {
            var index = _activeTrackList.value!!.trackList.indexOf(clickedTrack)
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
        val isCurrentlyPlaying = _mediaPlayer.value!!.isPlaying

        val track = getTrackFromIndex(_currentIndex.value!! + 1)
        changeTrack(context, track, _currentIndex.value!! + 1)

        if(isCurrentlyPlaying){
            _mediaPlayer.value!!.start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun autoplayNextTrack(context: Context){
        val track = getTrackFromIndex(_currentIndex.value!! + 1)
        changeTrack(context, track, _currentIndex.value!! + 1)
         _mediaPlayer.value!!.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context){
        resetActiveTracklist()
        setPlaylists(arrayListOf<Playlist>())

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