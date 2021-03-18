package com.example.euterpe.model

class TrackList {
    var trackList = listOf<Track>()

    fun addTrack(track: Track){
        trackList += track
    }

    fun getCount(): Int {
        return trackList.size
    }

    fun getTrackAtIndex(index: Int): Track{
        return trackList[index]
    }
}