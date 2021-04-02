package com.example.euterpe.controller

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.widget.SeekBar
import com.example.euterpe.adapter.MediastoreAdapter
import com.example.euterpe.model.Track
import com.example.euterpe.model.TrackList
import com.example.euterpe.model.TrackListViewModel

class AudioController(mediaSession: MediaSessionCompat){

    companion object{
        private var mSession: MediaSessionCompat? = null
        private val TAG = "Audio Controller"

        @Volatile
        private var INSTANCE: AudioController? = null

        @Synchronized
        fun getInstance(mediaSession: MediaSessionCompat): AudioController = INSTANCE ?: AudioController(mediaSession).also { INSTANCE = it }

        fun setMSession(mediaSession: MediaSessionCompat){
            mSession = mediaSession
        }

        fun getMSession(): MediaSessionCompat?{
            return mSession
        }

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
                    changeTrack(context, viewModel, track, viewModel.currentIndex.value!! - 1)

                    if(isCurrentlyPlaying){
                        viewModel.mediaPlayer.value!!.start()
                    }
                }
            }
        }

        fun playNextTrack(context: Context, viewModel: TrackListViewModel){
            val isCurrentlyPlaying = viewModel.mediaPlayer.value!!.isPlaying

            if(viewModel.currentIndex.value!! + 1 < viewModel.playingTrackList.value!!.trackList.size){
                val track = viewModel.getTrackFromIndex(viewModel.currentIndex.value!! + 1)
                changeTrack(context, viewModel, track, viewModel.currentIndex.value!! + 1)

                if(isCurrentlyPlaying){
                    viewModel.mediaPlayer.value!!.start()
                }
            } else {
                playbackTrack(context, viewModel)
                viewModel.mediaPlayer.value!!.seekTo(viewModel.currentTrack.value!!.duration)
            }
        }

        fun autoplayNextTrack(context: Context, viewModel: TrackListViewModel){
            if(viewModel.currentIndex.value!! + 1 < viewModel.playingTrackList.value!!.trackList.size) {
                val track = viewModel.getTrackFromIndex(viewModel.currentIndex.value!! + 1)
                changeTrack(context, viewModel, track, viewModel.currentIndex.value!! + 1)
                viewModel.mediaPlayer.value!!.start()
            } else {
                AudioController.playbackTrack(context, viewModel)
                viewModel.mediaPlayer.value!!.seekTo(viewModel.currentTrack.value!!.duration)
            }
        }

        fun playOnClick(context: Context, viewModel: TrackListViewModel, uri: Uri, isPlaylist: Boolean){
            val clickedTrack = viewModel.trackList.value!!.trackList.find{ it.uri == uri}

            if(!isPlaylist){
                resetPlayingTracklist(viewModel)
            }

            if(viewModel.isRandom.value!!){
                viewModel.playingTrackList.value!!.trackList.shuffle()
                viewModel.playingTrackList.value!!.trackList.remove(clickedTrack)
                viewModel.playingTrackList.value!!.trackList.add(0, clickedTrack!!)

                changeTrack(context, viewModel, clickedTrack, 0)
            } else {
                var index = viewModel.playingTrackList.value!!.trackList.indexOf(clickedTrack)
                changeTrack(context, viewModel, clickedTrack!!, index)
            }

            viewModel.mediaPlayer.value!!.start()
            viewModel.setIsPaused(false)
        }

        fun toggleFavouriteTrack(context: Context, viewModel: TrackListViewModel){
            viewModel.currentTrack.value!!.isFavourited = !viewModel.currentTrack.value!!.isFavourited
            var playlist = viewModel.playlists.value!!.find{it.name == "Favourites"}

            if(viewModel.currentTrack.value!!.isFavourited) {
                MediastoreAdapter.createMemberInPlaylist(context, playlist!!.id, viewModel.currentTrack.value!!)
                MediastoreAdapter.readPlaylistMembers(context, viewModel, playlist!!.id)
            } else {
                MediastoreAdapter.deleteMemberInPlaylist(context, playlist!!.id, viewModel.currentTrack.value!!)
                MediastoreAdapter.readPlaylistMembers(context, viewModel, playlist!!.id)
            }

            var allPlaylists = viewModel.playlists.value
            viewModel.setPlaylists(allPlaylists!!)

            var changingTrack = viewModel.currentTrack.value
            viewModel.setCurrentTrack(changingTrack!!)
        }

        fun adjustCurrentPosition(seekBar: SeekBar, viewModel: TrackListViewModel){
            viewModel.mediaPlayer.value!!.seekTo(seekBar.progress)
        }

        fun toggleRandom(viewModel: TrackListViewModel){
            if(viewModel.isRandom.value!!){
                viewModel.setIsRandom(false)
                orderTracklist(viewModel)
            } else {
                viewModel.setIsRandom(true)
                resetPlayingTracklist(viewModel)
                shufflePlayingTracklist(viewModel)
            }
        }

        fun changeTrack(context: Context, viewModel: TrackListViewModel, newCurrentTrack: Track, newIndex: Int){
            viewModel.setCurrentTrack(newCurrentTrack)
            viewModel.setCurrentIndex(newIndex)
            viewModel.mediaPlayer.value!!.stop()
            viewModel.mediaPlayer.value!!.reset()
            viewModel.mediaPlayer.value!!.setDataSource(context, newCurrentTrack.uri)
            viewModel.mediaPlayer.value!!.prepare()
        }

        fun setPlayingTracklistToFav(viewModel: TrackListViewModel){
            resetPlayingTracklist(viewModel)

            var favouritePlaylist = viewModel.playlists.value!!.find{it.name == "Favourites"}
            var newPlayingList = viewModel.trackList.value!!.trackList.filter {favouritePlaylist!!.members!!.contains(it.id)}
            viewModel.playingTrackList.value!!.trackList = newPlayingList.toMutableList()

            viewModel.setPlayingTrackList(viewModel.playingTrackList.value!!)
        }

        private fun orderTracklist(viewModel: TrackListViewModel){
            val currentTrack = viewModel.currentTrack.value!!
            resetPlayingTracklist(viewModel)

            var index = viewModel.playingTrackList.value!!.trackList.indexOf(currentTrack)
            viewModel.setCurrentTrack(currentTrack)
            viewModel.setCurrentIndex(index)
        }

        fun reorderTracklist(order: String, viewModel: TrackListViewModel){
            when (order) {
                "Alphabetical" -> {
                    if(viewModel.currentOrder.value!! == "Alphabetical") {
                        viewModel.setCurrentOrder("Reverse Alphabetical")
                        viewModel.viewTrackList.value!!.trackList.sortByDescending { it.title }
                    } else {
                        viewModel.setCurrentOrder("Alphabetical")
                        viewModel.viewTrackList.value!!.trackList.sortBy { it.title }
                    }
                } "Artist" -> {
                    if(viewModel.currentOrder.value!! == "Artist") {
                        viewModel.setCurrentOrder("Reverse Artist")
                        viewModel.viewTrackList.value!!.trackList.sortByDescending { it.artist }
                    } else {
                        viewModel.setCurrentOrder("Artist")
                        viewModel.viewTrackList.value!!.trackList.sortBy { it.artist }
                    }
                } "Album" -> {
                    if(viewModel.currentOrder.value!! == "Album") {
                        viewModel.setCurrentOrder("Reverse Album")
                        viewModel.viewTrackList.value!!.trackList.sortByDescending { it.album }
                    } else {
                        viewModel.setCurrentOrder("Album")
                        viewModel.viewTrackList.value!!.trackList.sortBy { it.album }
                    }
                } "Recently" -> {
                    if(viewModel.currentOrder.value!! == "Recently") {
                        viewModel.setCurrentOrder("Reverse Recently")
                        viewModel.viewTrackList.value!!.trackList.sortBy { it.dateAdded }
                    } else {
                        viewModel.setCurrentOrder("Recently")
                        viewModel.viewTrackList.value!!.trackList.sortByDescending { it.dateAdded }
                    }
                }
            }

            viewModel.setViewTrackList(viewModel.viewTrackList.value!!)

            if(!viewModel.isRandom.value!!){
                val currentTrack = viewModel.currentTrack.value!!
                viewModel.setPlayingTrackList(viewModel.viewTrackList.value!!)

                var index = viewModel.playingTrackList.value!!.trackList.indexOf(currentTrack)
                viewModel.setCurrentTrack(currentTrack)
                viewModel.setCurrentIndex(index)
            }
        }

        fun shufflePlayingTracklist(viewModel: TrackListViewModel){
            viewModel.playingTrackList.value!!.trackList.shuffle()
            viewModel.setCurrentIndex(0)
        }

        fun resetPlayingTracklist(viewModel: TrackListViewModel){
            val duplicateList = TrackList()
            duplicateList.trackList = viewModel.trackList.value!!.trackList.toMutableList()
            viewModel.setPlayingTrackList(duplicateList)
        }
    }
}
