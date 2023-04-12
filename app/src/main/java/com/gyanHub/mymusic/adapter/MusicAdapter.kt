package com.gyanHub.mymusic.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.playHandal.MusicPlay

class MusicAdapter(private val items: List<MusicModel>,private val musicPlay: MusicPlay) : RecyclerView.Adapter<MusicAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(it: MusicModel,list:List<MusicModel>,position: Int) = with(itemView) {
            val musicName: TextView = this.findViewById(R.id.txtMusic)
            val musicTime: TextView = this.findViewById(R.id.txtMusicTime)
            val ImageView: ImageView = this.findViewById(R.id.imgMusic)
            musicName.text = it.title
            musicTime.text = formatDuration(it.duration)
            this.setOnClickListener {
                musicPlay.onClick(position,list)
            }
        }
       private fun formatDuration(duration: Long): String {
            val hours = duration / (1000 * 60 * 60)
            val minutes = duration % (1000 * 60 * 60) / (1000 * 60)
            val seconds = duration % (1000 * 60) / 1000
            return if (hours == 0L) String.format("%02d:%02d", minutes, seconds)
            else String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_song_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position],items,position)
}

