package com.gyanHub.mymusic.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.adapter.MusicAdapter
import com.gyanHub.mymusic.databinding.FragmentFolderMusicBinding
import com.gyanHub.mymusic.databinding.FragmentSongBinding
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.playHandal.MusicPlay
import com.gyanHub.mymusic.playHandal.SongClick
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.viewModel.ShareDataViewModel

class FolderMusicFragment : Fragment(), MusicPlay {
    private var _binding: FragmentFolderMusicBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicViewModel: MyMusicViewModel
    private lateinit var shardData: ShareDataViewModel
    private var songListner: SongClick? = null
    private var fromMusic: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderMusicBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context is SongClick) songListner = context as SongClick else
            throw RuntimeException("$context must implement DataListener")

        musicViewModel = ViewModelProvider(requireActivity())[MyMusicViewModel::class.java]
        shardData = ViewModelProvider(requireActivity())[ShareDataViewModel::class.java]
        musicViewModel.listOfMusicFromFolder.observe(viewLifecycleOwner) {
            setMusicInView(it)
        }
        shardData.musicFrom.observe(viewLifecycleOwner) {
            fromMusic = it
        }

    }

    private fun setMusicInView(list: List<MusicModel>) {
        binding.rcFolderMusic.layoutManager = LinearLayoutManager(context)
        val adapter = MusicAdapter(list, this)
        binding.rcFolderMusic.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(position: Int, list: List<MusicModel>) {
        addData(position,list)

    }

    private fun addData(position: Int, list: List<MusicModel>){
        val filePath = arguments?.getString("FilePath")
        val (_, _, storedFileName) = musicViewModel.getPlayingMusic()
        if (storedFileName.isNullOrEmpty()){
            musicViewModel.savePlayingMusicData(position,getString(R.string.folderF))
            musicViewModel.updatePlayingFolder(filePath!!)
        }else{
            if (storedFileName == getString(R.string.folderF)){
                musicViewModel.updatePlayingPosition(position)
                musicViewModel.updatePlayingFolder(filePath!!)
            }else{
                musicViewModel.savePlayingMusicData(position,getString(R.string.folderF))
                musicViewModel.updatePlayingFolder(filePath!!)
            }
        }



//        if (fromMusic.isNotEmpty()){
//            if (fromMusic == getString(R.string.folderF)){
//                shardData.setPlaingPosition(position)
//            }else{
//                shardData.setMusicFrom(getString(R.string.folderF))
//                shardData.setPlaingPosition(position)
//                shardData.setPlayingMusicList(list)
//            }
//        }else{
//            shardData.setMusicFrom(getString(R.string.folderF))
//            shardData.setPlaingPosition(position)
//            shardData.setPlayingMusicList(list)
//        }
        songListner!!.onSongClick()
    }
}