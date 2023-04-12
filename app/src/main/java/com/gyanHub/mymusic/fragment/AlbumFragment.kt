package com.gyanHub.mymusic.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() {
    private var _binding : FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun rcView() {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val columnCount = when {
            screenWidthDp >= 1200 -> 5
            screenWidthDp >= 800 -> 4
            screenWidthDp >= 600 -> 3
            screenWidthDp >= 390 -> 2
            else -> 1
        }
        val layoutManager = GridLayoutManager(context, columnCount)
//        binding.rcMusic.layoutManager = layoutManager
        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(binding.rcMusic)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}