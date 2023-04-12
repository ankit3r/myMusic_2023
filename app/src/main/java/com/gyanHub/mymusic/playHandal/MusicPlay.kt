package com.gyanHub.mymusic.playHandal

import com.gyanHub.mymusic.model.MusicModel


interface MusicPlay {
    fun onClick(position: Int,list:List<MusicModel>)
}