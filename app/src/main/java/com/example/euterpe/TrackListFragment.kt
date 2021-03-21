package com.example.euterpe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.euterpe.adapter.TrackListAdapter
import com.example.euterpe.adapter.TrackListListener
import com.example.euterpe.databinding.FragmentTrackListBinding
import com.example.euterpe.model.TrackListViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrackListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackListFragment : Fragment() {

    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentTrackListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_list, container, false);
        val view: View = binding.root

        binding?.apply{
            trackListViewModel = viewModel
        }

        val adapter =
            TrackListAdapter(TrackListListener { uri ->
                viewModel.playOnClick(requireContext(), uri)
            })

        binding.trackListRcv.adapter = adapter
        viewModel.trackList.observe(viewLifecycleOwner, Observer{
            it?.let{
                adapter.submitList(it.trackList)
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = TrackListFragment().apply {}
    }
}