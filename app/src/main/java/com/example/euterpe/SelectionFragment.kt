package com.example.euterpe

import android.content.ContentUris
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.euterpe.databinding.FragmentSelectionBinding
import com.example.euterpe.model.Track
import com.example.euterpe.model.TrackList
import com.example.euterpe.model.TrackListViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import layout.SelectionAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [SelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectionFragment : Fragment() {
    data class Audio(val uri: Uri,
                     val title: String,
                     val artist: String,
                     val album: String,
                     val duration: Int
    )

    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var viewPager: ViewPager2
    private val audioList = mutableListOf<Audio>()
    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_selection, container, false);
        val view: View = binding.root

        val popupButton = view.findViewById<View>(R.id.orderby_menu_btn) as ImageButton

        popupButton.setOnClickListener { v ->
            this.showMenu(v)
        }

        return view
    }

    private fun showMenu(v: View) {
        PopupMenu(requireActivity(), v).apply {
            // MainActivity implements OnMenuItemClickListener
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.menu_alphabetical -> {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.menu_artist -> {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.menu_album -> {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.menu_recently -> {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                    }
                }

                true
            })
            inflate(R.menu.orderby_menu)
            show()
        }
    }

    private fun loadMusic(){
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.DURATION)

        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} ASC"

        val query = context?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use{ cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

            Log.i("Query count", cursor.count.toString())

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(idCol)
                        val name = cursor.getString(nameCol)
                        val duration = cursor.getInt(durationCol)
                        val artist = cursor.getString(artistCol)
                        val album = cursor.getString(albumCol)
                        val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)

                        audioList += Audio(contentUri, name, artist, album, duration)
                    } while(cursor.moveToNext())
                } else {
                    Log.i("Query", "Cursor move to first fail")
                }
            }
            else{
                Log.i("Query", "Cursor is null")
            }
        }
        Log.i("Mediastore", audioList.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
        var menuItem = menu.findItem(R.id.randomise_btn)
        var title = menuItem.getTitle().toString()

        if (title != null) {
            val s = SpannableString(title)
            s.setSpan(ForegroundColorSpan(Color.parseColor("#ab000d")), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            menuItem.setTitle(s)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun randomise(item: MenuItem){
        Log.i("Selection Fragment", "Randomising")
        viewModel.switchRandom()
        var title = item.getTitle().toString()
        var color = "#ab000d"

        if(!viewModel.isRandom.value!!){
            color = "#ffffff"
        }

        if (title != null) {
            val s = SpannableString(title)
            s.setSpan(ForegroundColorSpan(Color.parseColor(color)),0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            item.setTitle(s)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId

        when(id) {
            R.id.randomise_btn -> randomise(item)
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("Selection Fragment", "Selection Fragment on view created")
        selectionAdapter = SelectionAdapter(this)
        viewPager = view.findViewById(R.id.selection_pager)
        viewPager.adapter = selectionAdapter

        var tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Playback"
                1 -> tab.text = "Favourites"
                2 -> tab.text = "Tracks"
                else -> tab.text = "Playlists"
            }
        }.attach()

        loadMusic()

        binding?.apply{
            trackListViewModel = viewModel
        }

        var trackList = TrackList()

        for(track in audioList){
            var newTrack = Track(track.uri, track.title, track.artist, track.album, track.duration)
            trackList.addTrack(newTrack)
        }

        viewModel.setTrackList(trackList)
        viewModel.init(requireContext())

        Log.i("Selection Fragment", viewModel.trackList.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mediaPlayer.value!!.release()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = SelectionFragment().apply { }
    }

}