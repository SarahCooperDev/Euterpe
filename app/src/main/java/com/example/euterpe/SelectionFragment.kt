package com.example.euterpe

import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.example.euterpe.adapter.MediaReceiver
import com.example.euterpe.adapter.MediaService
import com.example.euterpe.adapter.MediaService.Companion.cancelNotifications
import com.example.euterpe.adapter.MediaService.Companion.sendNotification
import com.example.euterpe.adapter.MediastoreAdapter.Companion.createPlaylist
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readMusic
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readPlaylistMembers
import com.example.euterpe.adapter.MediastoreAdapter.Companion.readPlaylists
import com.example.euterpe.controller.AudioController
import com.example.euterpe.databinding.FragmentSelectionBinding
import com.example.euterpe.model.Playlist
import com.example.euterpe.model.Track
import com.example.euterpe.model.TrackList
import com.example.euterpe.model.TrackListViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import layout.SelectionAdapter

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
    private val NOTIFICATION_ID = 0

    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var viewPager: ViewPager2
    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentSelectionBinding
    private var menuItemChecked: Int = 0
    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var mediaSession: MediaSessionCompat
    private val notificationChannel: String = "EuterpeChannel"
    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    var mediaController: android.widget.MediaController? = null
    private lateinit var mediaBrowser: MediaBrowserCompat

    private val audioReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Broadcast Receiver", "In Local Media Receiver with " + intent!!.action)

            when {
                ACTION_PAUSE == intent!!.action -> {
                    AudioController.playbackTrack(requireContext(), viewModel)
                    notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, this, builder,viewModel.isPaused.value!!)
                }
                ACTION_PREVIOUS == intent!!.action -> {
                    AudioController.playPreviousTrack(requireContext(), viewModel)
                    notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, this, builder,viewModel.isPaused.value!!)
                }
                ACTION_NEXT == intent!!.action -> {
                    AudioController.playNextTrack(requireContext(), viewModel)
                    notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, this, builder,viewModel.isPaused.value!!)
                }
                ACTION_PLAY == intent!!.action -> {
                    AudioController.playbackTrack(requireContext(), viewModel)
                    notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, this, builder,viewModel.isPaused.value!!)
                }
                else -> {
                    Log.i("Broadcast Receiver", "Couldn't categorise intent")
                }
            }
        }

    }

    private val connectionCallbacks = object: MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            Log.i("Connection Callbacks", "In on connected")
            mediaBrowser.sessionToken.also{ token ->
                val mediaController = MediaControllerCompat(
                    requireContext(), token
                )

                MediaControllerCompat.setMediaController(requireActivity(), mediaController)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        menuItemChecked = 0
        setupMediaSession()
        var lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_PAUSE))
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_PLAY))
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_PREVIOUS))
        lbm.registerReceiver(this.audioReceiver, IntentFilter(ACTION_NEXT))

        mediaBrowser = MediaBrowserCompat(requireContext(),
            ComponentName(requireContext(), AudioService::class.java),
            connectionCallbacks,
            null)
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i("Controller callback", "On metadata changed")
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.i("Controller callback", "On playback state changed")
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(requireActivity())?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
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
        var contextWrapper = ContextThemeWrapper(requireContext(), R.style.EuterpeTheme_Popup)
        PopupMenu(contextWrapper, v).apply {
            var menuSize = this.menu.size()

            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                AudioController.reorderTracklist(item!!.title.toString(), viewModel)

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

            var menuitem = this.menu.getItem(menuItemChecked)
            this.menu.getItem(menuItemChecked).setChecked(true)

            val s = SpannableString(menuitem.title)
            s.setSpan(ForegroundColorSpan(Color.parseColor("#6d6d6d")), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            s.setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            s.setSpan(UnderlineSpan(), 0, s.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            this.menu.getItem(menuItemChecked).setTitle(s)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.getItem(menuItemChecked).setChecked(true)
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
        AudioController.toggleRandom(viewModel)
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

    private fun setupMediaSession(){
        var mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.action = Intent.ACTION_MEDIA_BUTTON
        val mediaButtonReceiverPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(requireContext(), 0, mediaButtonIntent, 0)

        mediaSession = MediaSessionCompat(requireContext(), "BluetoothService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaController = android.widget.MediaController(requireContext())
            mediaController!!.setAnchorView(view)
        }


        val callback = object: MediaSessionCompat.Callback(){
            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                Log.i("Selection Fragment", "Cause mediabutton event")
                Log.i("Selection Fragment", mediaButtonEvent.toString())
                Log.i("Selection Fragment", mediaButtonEvent!!.action.toString())
                var keyEvent  = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

                if (keyEvent != null) {
                    if (keyEvent.action == KeyEvent.ACTION_UP)
                    {
                        Log.i("Selection Fragment", "Keyevent Action up")
                        val intent = Intent(requireContext(), MediaReceiver::class.java)
                        intent.action = MediaService.ACTION_PLAY
                        var pending =  PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
                        pending.send()
                    }
                }

                return super.onMediaButtonEvent(mediaButtonEvent)
            }

            override fun onPlay() {
                super.onPlay()
                Log.i("Audio Service", "Cause onplay event")
            }

            override fun onPause() {
                super.onPause()
                Log.i("Audio Service", "Cause mediabutton event")
            }
        }

        mediaSession.setCallback(callback)
        mediaSession.isActive = true
        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent)

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



        builder = MediaService.generateBaseBuilder(requireContext(), mediaSession)

        notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager::class.java) as NotificationManager
        notificationManager!!.cancelNotifications()
        notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, this.audioReceiver, builder, viewModel.isPaused.value!!)


        viewModel.isPaused.observe(viewLifecycleOwner, Observer {
            notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, audioReceiver, builder,viewModel.isPaused.value!!)
        })

        viewModel.currentTrack.observe(viewLifecycleOwner, Observer {
            notificationManager!!.sendNotification(viewModel.currentTrack.value!!.title,viewModel.currentTrack.value!!.artist + " - " + viewModel.currentTrack.value!!.album, requireContext(), mediaSession, audioReceiver, builder,viewModel.isPaused.value!!)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mediaPlayer.value!!.release()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(this.audioReceiver)
        notificationManager!!.cancelNotifications()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SelectionFragment().apply { }
    }
}