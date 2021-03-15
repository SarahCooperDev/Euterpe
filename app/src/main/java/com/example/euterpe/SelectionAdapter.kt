package layout

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.euterpe.FavouritesFragment
import com.example.euterpe.PlaybackFragment
import com.example.euterpe.PlaylistsFragment
import com.example.euterpe.TrackListFragment

class SelectionAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {

    private fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PlaybackFragment()
            1 -> FavouritesFragment()
            2 -> TrackListFragment()
            else -> return PlaylistsFragment()
        }
    }

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return getItem(position)
    }
}