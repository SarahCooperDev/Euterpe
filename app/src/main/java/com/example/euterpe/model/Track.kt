package com.example.euterpe.model

import android.net.Uri

class Track(id: Long, uri: Uri, title: String, artist: String, album: String, dateAdded: Long, duration: Int){
    var id = id
    var uri = uri
    var title = title
    var artist = artist
    var duration = duration
    var album = album
    var dateAdded = dateAdded
    var isFavourited = false

    var detailSummaryString = artist + " - " + album
}