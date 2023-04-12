package com.gyanHub.mymusic.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.FrameMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.adapter.MusicAdapter
import com.gyanHub.mymusic.databinding.FragmentSongBinding
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.playHandal.MusicPlay
import com.gyanHub.mymusic.playHandal.SongClick
import com.gyanHub.mymusic.viewModel.ShareDataViewModel

class SongFragment : Fragment(), MusicPlay {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicViewModel: MyMusicViewModel
    private lateinit var shardData: ShareDataViewModel
    private var songListner: SongClick? = null
    private var fromMusic: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context is SongClick) songListner = context as SongClick else
            throw RuntimeException("$context must implement DataListener")
        musicViewModel = ViewModelProvider(requireActivity())[MyMusicViewModel::class.java]
        shardData = ViewModelProvider(requireActivity())[ShareDataViewModel::class.java]
        musicViewModel.listOfMusic.observe(viewLifecycleOwner) {
            setMusicInView(it)
        }
        shardData.musicFrom.observe(viewLifecycleOwner) {
            fromMusic = it
        }
    }

    private fun setMusicInView(list: List<MusicModel>) {
        binding.rcMusic.layoutManager = LinearLayoutManager(context)
        val adapter = MusicAdapter(list, this)
        binding.rcMusic.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(position: Int, list: List<MusicModel>) {
        addData(position,list)

    }

    private fun addData(position: Int, list: List<MusicModel>){
        val (_, _, storedFileName) = musicViewModel.getPlayingMusic()
        if (storedFileName.isNullOrEmpty()){
            musicViewModel.savePlayingMusicData(position,getString(R.string.songF))
        }else{
            if (storedFileName == getString(R.string.songF)){
                musicViewModel.updatePlayingPosition(position)
            }else{
                musicViewModel.savePlayingMusicData(position,getString(R.string.songF))
            }
        }


//        if (fromMusic.isNotEmpty()){
//            if (fromMusic == getString(R.string.songF)){
//                shardData.setPlaingPosition(position)
//            }else{
//                shardData.setMusicFrom(getString(R.string.songF))
//                shardData.setPlaingPosition(position)
//                shardData.setPlayingMusicList(list)
//            }
//        }else{
//            shardData.setMusicFrom(getString(R.string.songF))
//            shardData.setPlaingPosition(position)
//            shardData.setPlayingMusicList(list)
//        }
        songListner!!.onSongClick()
    }
}