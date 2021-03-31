package com.example.euterpe.adapter

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.euterpe.model.Track
import com.example.euterpe.model.TrackListViewModel

class MediastoreAdapter{
    data class Audio(val id: Long,
                     val uri: Uri,
                     val title: String,
                     val artist: String,
                     val album: String,
                     val dateAdded: Long,
                     val duration: Int)

    data class TempPlay(val playlistId: Long,
                        val playlistTitle: String)

    companion object{
        private val TAG = "Mediastore Adapter"

        fun readMusic(context: Context): List<Audio>{
            val projection = arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.DATE_ADDED,
                MediaStore.Audio.AudioColumns.DURATION)

            val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} ASC"

            val query = context?.contentResolver?.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            val audioList = mutableListOf<Audio>()

            query?.use{ cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

                Log.i(TAG, "Query count" + cursor.count.toString())

                if(cursor != null) {
                    if(cursor.moveToFirst()) {
                        do {
                            val id = cursor.getLong(idCol)
                            val name = cursor.getString(nameCol)
                            val duration = cursor.getInt(durationCol)
                            val artist = cursor.getString(artistCol)
                            val album = cursor.getString(albumCol)
                            val dateAdded = cursor.getLong(dateCol)
                            val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)

                            audioList += Audio(id, contentUri, name, artist, album, dateAdded, duration)
                        } while(cursor.moveToNext())
                    } else {
                        Log.i(TAG, "Cursor move to first fail")
                    }
                } else {
                    Log.i(TAG, "Cursor is null")
                }
            }
            Log.i(TAG, audioList.toString())

            return audioList
        }


        fun readPlaylists(context: Context): List<TempPlay>{
            var playlistProjection = arrayOf(MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME)
            val playlistQuery = context?.contentResolver?.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, playlistProjection, null, null, null)
            val playlistList = mutableListOf<TempPlay>()

            playlistQuery?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME)

                Log.i(TAG, "Playlist Query count" + playlistQuery.count.toString())

                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            val playlistId = cursor.getLong(idCol)
                            val playlistName = cursor.getString(nameCol)

                            playlistList += TempPlay(playlistId, playlistName)
                        } while (cursor.moveToNext())
                    }
                }
            }
            Log.i(TAG, playlistList.toString())
            return playlistList
        }



        fun readPlaylistMembers(context: Context, viewModel: TrackListViewModel, playlistId: Long){
            var projection = arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members.PLAYLIST_ID)
            val query = context?.contentResolver?.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId), projection, null, null, null)

            query?.use { cursor ->
                val audioIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID)
                val playlistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAYLIST_ID)
                var playlist = viewModel.playlists.value!!.find { it.id == playlistId }
                playlist!!.clearMembership()

                Log.i(TAG, "Member Query count" + query.count.toString())

                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            val audioId = cursor.getLong(audioIdCol)
                            val playlistId = cursor.getLong(playlistIdCol)
                            playlist!!.addMember(audioId)

                            if(playlist.name == "Favourites"){
                                viewModel.trackList.value!!.trackList.find{ it.id == audioId}!!.isFavourited = true
                            }
                        } while (cursor.moveToNext())
                    }
                }
            }
        }

        fun createPlaylist(activity: Activity, name: String): Long{
            var cv = ContentValues()
            var resolver = activity.getContentResolver()
            cv.put(MediaStore.Audio.Playlists.NAME, name);
            cv.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
            cv.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
            var uri: Uri? = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cv)

            if(uri == null){
                Log.i(TAG, "Failed to insert playlist")
            }
            var idString = uri!!.lastPathSegment
            if(idString == null){
                Log.i(TAG, "Failed to parse uri last segment")
            }

            return idString!!.toLong()
        }


        fun createMemberInPlaylist(context: Context, playlistId: Long, currentTrack: Track){
            Log.i(TAG, "Adding member to playlist")
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

                contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, currentTrack.id);
                contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, (playOrder + currentTrack.id).toInt())

                resolver.insert(uri, contentValues);
            }
        }

        fun deleteMemberInPlaylist(context: Context, playlistId: Long, currentTrack: Track){
            Log.i(TAG, "Deleting member from playlist")
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

                resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + currentTrack.id, null);
            }
        }
    }
}