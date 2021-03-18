package com.example.euterpe.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackListViewModel : ViewModel() {
    private val _trackList = MutableLiveData<TrackList>()
    val trackList: LiveData<TrackList> = _trackList

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    fun setTrackList(tracks: TrackList){
        _trackList.value = tracks
    }

    fun setCurrentTrack(track: Track){
        _currentTrack.value = track
    }

    fun setCurrentToRandomTrack(){
        val size = _trackList.value!!.getCount() - 1
        val randomNo = (0..size).random()
        var randomTrack = _trackList.value!!.getTrackAtIndex(randomNo)
        setCurrentTrack(randomTrack)
    }

}