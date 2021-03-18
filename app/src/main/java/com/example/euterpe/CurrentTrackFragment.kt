package com.example.euterpe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.euterpe.databinding.FragmentCurrentTrackBinding
import com.example.euterpe.model.TrackListViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentTrackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentTrackFragment : Fragment() {

    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentCurrentTrackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_track, container, false);
        val view: View = binding.root

        binding?.apply{
            trackListViewModel = viewModel
        }

        binding.lifecycleOwner = viewLifecycleOwner

        //val adapter = TrackListAdapter()
        //binding.trackListRcv.adapter = adapter
        //adapter.data = viewModel.trackList.value!!.trackList

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CurrentTrackFragment.
         */
        @JvmStatic
        fun newInstance() = CurrentTrackFragment().apply {}
    }
}