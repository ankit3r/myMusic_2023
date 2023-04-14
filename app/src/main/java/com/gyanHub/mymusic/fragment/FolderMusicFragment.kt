package com.gyanHub.mymusic.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
        val filePath = arguments?.getString("FilePath")
        if (filePath != null) {
            musicViewModel.listOfMusicFromFolder.observe(viewLifecycleOwner) {
                setMusicInView(it)
            }
        }else{
            val musicList = arguments?.getSerializable("musicList") as? List<MusicModel>
            Log.w("ANKIT",musicList.toString())
            setMusicInView(musicList!!)
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

    override fun onClick(position: Int, list: List<MusicModel>) = addData(position)


    private fun addData(position: Int) {
        val filePath = arguments?.getString("FilePath")
        val albumP = arguments?.getInt("albumP")!!.toInt()
        if (filePath != null) {
            Toast.makeText(context, "not null filepath", Toast.LENGTH_SHORT).show()
            val (_, _, storedFileName) = musicViewModel.getPlayingMusic()
            if (storedFileName.isNullOrEmpty()) {
                musicViewModel.savePlayingMusicData(position, getString(R.string.folderF))
                musicViewModel.updatePlayingFolder(filePath)
            } else {
                if (storedFileName == getString(R.string.folderF)) {
                    musicViewModel.updatePlayingPosition(position)
                    musicViewModel.updatePlayingFolder(filePath)
                } else {
                    musicViewModel.savePlayingMusicData(position, getString(R.string.folderF))
                    musicViewModel.updatePlayingFolder(filePath)
                }
            }
        } else {
            Toast.makeText(context, "null filepath", Toast.LENGTH_SHORT).show()
            val (_, _, storedFileName) = musicViewModel.getPlayingMusic()
            if (storedFileName.isNullOrEmpty()) {
                musicViewModel.savePlayingMusicData(position, getString(R.string.albumF))
                musicViewModel.updateAlbumPosition(albumP)
            } else {
                if (storedFileName == getString(R.string.albumF)) {
                    musicViewModel.updatePlayingPosition(position)
                    musicViewModel.updateAlbumPosition(albumP)
                } else {
                    musicViewModel.savePlayingMusicData(position, getString(R.string.albumF))
                    musicViewModel.updateAlbumPosition(albumP)
                }
            }
        }

        songListner!!.onSongClick()
    }
}