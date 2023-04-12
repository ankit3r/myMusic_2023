package com.gyanHub.mymusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gyanHub.mymusic.databinding.LayoutFolderCardBinding
import java.io.File

class FolderAdapter(private val list: Set<String>,private val folderClick:FolderClick) :
    RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    inner class FolderViewHolder(private val binding: LayoutFolderCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(filePath: String, folderClick: FolderClick) {
            val file = File(filePath)
            binding.txtFolder.text = file.parentFile?.name
            binding.root.setOnClickListener {
                folderClick.onClick(filePath)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutFolderCardBinding.inflate(inflater, parent, false)
        return FolderViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) =
        holder.bind(list.elementAt(position),folderClick)
}

interface FolderClick {
    fun onClick(filePath: String)
}