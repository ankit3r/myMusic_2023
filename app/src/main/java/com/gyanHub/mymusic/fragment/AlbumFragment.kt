package com.gyanHub.mymusic.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.adapter.AlbumAdapter
import com.gyanHub.mymusic.adapter.AlbumClick
import com.gyanHub.mymusic.databinding.FragmentAlbumBinding
import com.gyanHub.mymusic.model.Album
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.viewModel.ShareDataViewModel
import java.io.Serializable

class AlbumFragment : Fragment(), AlbumClick {
    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicViewModel: MyMusicViewModel
    private lateinit var shardData: ShareDataViewModel
    private lateinit var backPressCallback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicViewModel = ViewModelProvider(requireActivity())[MyMusicViewModel::class.java]
        shardData = ViewModelProvider(requireActivity())[ShareDataViewModel::class.java]
        binding.txtAlbumPath.visibility = View.GONE
        rcView()
        musicViewModel.listOfAlbum.observe(viewLifecycleOwner) {
            binding.rcAlbum.adapter = AlbumAdapter(it, this)
        }
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                reSetAlbum()
            }
        }
        binding.txtAlbumPath.setOnClickListener { reSetAlbum() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressCallback)
    }

    private fun rcView() {
        val layoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        binding.rcAlbum.layoutManager = layoutManager

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        backPressCallback.remove()
    }

    private fun setFragment(fragment: Fragment, list: List<MusicModel>, p: Int) {
        binding.fragmentHolderM.visibility = View.VISIBLE
        val bundle = Bundle().apply {
            putSerializable("musicList", list as Serializable)
            putInt("albumP", p)
        }
        fragment.arguments = bundle
        val fragmentTransient = childFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.fragmentHolderM, fragment)
        fragmentTransient.commit()
    }

    private fun reSetAlbum() {
        if (binding.rcAlbum.visibility == View.GONE) {
            binding.rcAlbum.visibility = View.VISIBLE
            binding.fragmentHolderM.removeAllViews()
            binding.fragmentHolderM.visibility = View.GONE
            binding.txtAlbumPath.visibility = View.GONE
        }
    }

    override fun onAlbumClick(album: Album, position: Int) {
        setFragment(FolderMusicFragment(), album.songs,position)
        binding.rcAlbum.visibility = View.GONE
        binding.txtAlbumPath.visibility = View.VISIBLE
        binding.txtAlbumPath.text = getString(R.string.albumPath, ">> ${album.name}")
    }
}