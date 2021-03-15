package com.example.euterpe

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import layout.SelectionAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectionFragment : Fragment() {
    data class Audio(val uri: Uri,
                     val title: String,
                     val artist: Int,
                     val duration: Int
    )

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var viewPager: ViewPager2
    private val audioList = mutableListOf<Audio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_selection, container, false)
        val popupButton = rootView.findViewById<View>(R.id.orderby_menu_btn) as ImageButton

        popupButton.setOnClickListener { v ->
            this.showMenu(v)
        }
        return rootView
    }

    private fun showMenu(v: View) {
        PopupMenu(activity!!, v).apply {
            // MainActivity implements OnMenuItemClickListener
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.menu_alphabetical -> {
                        Toast.makeText(activity!!, item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.menu_artist -> {
                        Toast.makeText(activity!!, item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.menu_album -> {
                        Toast.makeText(activity!!, item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.menu_recently -> {
                        Toast.makeText(activity!!, item.title, Toast.LENGTH_SHORT).show()
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
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

            Log.i("Query count", cursor.count.toString())

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(idCol)
                        val name = cursor.getString(nameCol)
                        val duration = cursor.getInt(durationCol)
                        val size = cursor.getInt(sizeCol)
                        val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)

                        audioList += Audio(contentUri, name, duration, size)
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
        fun newInstance(param1: String, param2: String) =
            SelectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}