package com.example.euterpe

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaMetadata
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.example.euterpe.adapter.BluetoothController
import com.example.euterpe.adapter.MediaService.Companion.sendNotification
import com.example.euterpe.adapter.MediastoreAdapter.Companion.createPlaylist
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readMusic
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readPlaylistMembers
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readPlaylists
import com.example.euterpe.databinding.FragmentSelectionBinding
import com.example.euterpe.model.Playlist
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
    data class Audio(val id: Long,
                     val uri: Uri,
                     val title: String,
                     val artist: String,
                     val album: String,
                     val dateAdded: Long,
                     val duration: Int)

    data class TempPlay(val playlistId: Long,
                        val playlistTitle: String)

    val ACTION_PLAY = "action_play"
    val ACTION_PAUSE = "action_pause"
    val ACTION_REWIND = "action_rewind"
    val ACTION_FAST_FORWARD = "action_fast_foward"
    val ACTION_NEXT = "action_next"
    val ACTION_PREVIOUS = "action_previous"
    val ACTION_STOP = "action_stop"
    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var viewPager: ViewPager2
    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentSelectionBinding
    private var menuItemChecked: Int = 0
    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var mediaSession: MediaSessionCompat
    private val notificationChannel: String = "EuterpeChannel"

    private val audioReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Broadcast Receiver", "In Local Media Receiver")

            if(ACTION_PAUSE == intent!!.action){
                Log.i("Broadcast Receiver", "Pause Button")
            } else if(ACTION_PREVIOUS == intent!!.action){
                Log.i("Broadcast Receiver", "Previous Button")
            } else if(ACTION_NEXT == intent!!.action){
                Log.i("Broadcast Receiver", "Next Button")
            } else {
                Log.i("Broadcast Receiver", "Couldn't categorise intent")
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        menuItemChecked = 0
        var lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_PAUSE))
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_PREVIOUS))
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_NEXT))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_selection, container, false);
        val view: View = binding.root

        //BluetoothController.getBluetoothAdapter()!!
        //BluetoothController.getProxy(requireContext())

        val popupButton = view.findViewById<View>(R.id.orderby_menu_btn) as ImageButton
        popupButton.setOnClickListener { v ->
            this.showMenu(v)
        }

        return view
    }

    private fun showMenu(v: View) {
        var contextWrapper = ContextThemeWrapper(requireContext(), R.style.EuterpeTheme_Popup)
        PopupMenu(contextWrapper, v).apply {

            Log.i("Show Menu", "Showing menu construction")

            var menuSize = this.menu.size()

            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                viewModel.reorderTracklist(item!!.title.toString())

                Log.i("Selection Fragment", item.isChecked.toString())
                item.setChecked(true)

                when(item!!.itemId){
                    R.id.menu_alphabetical -> { menuItemChecked = 0
                    } R.id.menu_artist -> { menuItemChecked = 1
                    } R.id.menu_album -> { menuItemChecked = 2
                    } R.id.menu_recently -> { menuItemChecked = 3 }
                }
                true
            })

            inflate(R.menu.orderby_menu)
            this.show()

            menuSize = this.menu.size()

            Log.i("Show Menu", this.menu.size().toString())
            Log.i("Show Menu", this.menu.getItem(0).toString())

            var menuitem = this.menu.getItem(menuItemChecked)
            this.menu.getItem(menuItemChecked).setChecked(true)

            //this.menu.getItem(menuItemChecked).setIcon(R.drawable.orderby_background_selector)
            val s = SpannableString(menuitem.title)
            s.setSpan(ForegroundColorSpan(Color.parseColor("#6d6d6d")), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            s.setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            s.setSpan(UnderlineSpan(), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            this.menu.getItem(menuItemChecked).setTitle(s)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        Log.i("Context", "Calling context menu")

        menu.getItem(menuItemChecked).setChecked(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
        var menuItem = menu.findItem(R.id.randomise_btn)
        var title = menuItem.getTitle().toString()

        Log.i("Options Menu", "Calling options menu creation")

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
            R.id.search_btn -> Toast.makeText(requireContext(), "Searching...", Toast.LENGTH_SHORT)
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectionAdapter = SelectionAdapter(this)
        viewPager = view.findViewById(R.id.selection_pager)
        viewPager.adapter = selectionAdapter

        var tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Playback"
                1 -> tab.text = "Favourites"
                2 -> tab.text = "Tracks"
                else -> tab.text = "Playlists"
            }
        }.attach()

        var audioList = readMusic(requireContext())

        binding?.apply {
            trackListViewModel = viewModel
        }

        var trackList = TrackList()

        for (track in audioList) {
            var newTrack = Track(track.id, track.uri, track.title, track.artist, track.album, track.dateAdded, track.duration)
            trackList.addTrack(newTrack)
        }

        viewModel.setTrackList(trackList)
        viewModel.init(requireContext())

        var playlistList = readPlaylists(requireContext())

        // Check to make sure the Favourites playlist exists, and creates it if it doesn't
        if(playlistList.size == 0 && playlistList.any{it.playlistTitle == "Favourites"}){
            Log.i("Selection Fragment", "There are currently no playlists")
            var id = createPlaylist(requireActivity(), "Favourites")

            playlistList = readPlaylists(requireContext())
        }

        for(list in playlistList){
            var memberList: MutableList<Long> = ArrayList()
            var newList = Playlist(list.playlistId, list.playlistTitle, memberList)
            viewModel.addToPlaylist(newList)

            readPlaylistMembers(requireContext(), viewModel, list.playlistId)
        }

        var mediaController = MediaController()
        mediaController.viewModel = viewModel

        mediaSession = MediaSessionCompat(requireContext(), "BluetoothService")
        Log.i("Selection Fragment", "Created Media session")
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, viewModel.currentTrack.value!!.title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, viewModel.currentTrack.value!!.artist)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, viewModel.currentTrack.value!!.duration.toLong())
                .build())

        Log.i("Selection Fragment", "Set metadata")

        val notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification("Euterpe app", requireContext(), mediaSession, this.audioReceiver)

        Log.i("Selection Fragment", "Creating media session")
        //val mediaStyle = NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mediaPlayer.value!!.release()
        BluetoothController.closeA2dpProxy(bluetoothAdapter, BluetoothController.bluetoothA2dp)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(this.audioReceiver)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SelectionFragment().apply { }
    }
}