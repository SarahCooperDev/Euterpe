package com.example.euterpe.model

class TrackList {
    var trackList = mutableListOf<Track>()

    fun addTrack(track: Track){
        trackList.add(track)
    }

    fun getCount(): Int {
        return trackList.size
    }

    fun getTrackAtIndex(index: Int): Track{
        return trackList[index]
    }
}