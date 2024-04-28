package com.plcoding.spotifycloneyt.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.plcoding.spotifycloneyt.adapter.SongAdapter
import com.plcoding.spotifycloneyt.databinding.FragmentHomeBinding
import com.plcoding.spotifycloneyt.ui.viewmodel.MainViewModel
import com.plcoding.spotifycloneyt.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var songsAdapter: SongAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    /**
     *  we bind mainViewModel to the lifecycle of the activity and not to our fragment
     *     here we need to do it in ViewModelProvider() way
     *     so we can explicitly pass our activity here as a life cycle owner
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initializeRecyclerView()
        subscribeToObservers()
        songsAdapter.setOnItemClickListener { mainViewModel.playOrToggleSong(it) }
    }

    private fun initializeRecyclerView() = rvAllSongs.apply {
        adapter = songsAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let {
                        songsAdapter.differSongList = it
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> {
                    allSongsProgressBar.isVisible = true
                }
            }
        }
    }
}