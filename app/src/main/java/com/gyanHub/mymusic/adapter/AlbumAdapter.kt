package com.gyanHub.mymusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gyanHub.mymusic.databinding.LayoutAlbumeCardBinding
import com.gyanHub.mymusic.model.Album


class AlbumAdapter(private val list: List<Album>,private val albumClick:AlbumClick) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    inner class AlbumViewHolder(private val binding: LayoutAlbumeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album,albumClick: AlbumClick,position: Int) {
            binding.txtAlbum.text = album.name
            binding.root.setOnClickListener {
                albumClick.onAlbumClick(album,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutAlbumeCardBinding.inflate(inflater, parent, false)
        return AlbumViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) = holder.bind(list[position],albumClick,position)
}

interface AlbumClick{
   fun onAlbumClick(album:Album,position: Int)
}

