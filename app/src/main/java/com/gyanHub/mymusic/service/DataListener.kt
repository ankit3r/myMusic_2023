package com.gyanHub.mymusic.service

import androidx.lifecycle.LiveData
import com.gyanHub.mymusic.model.MusicModel

interface DataListener {
    fun getList(): LiveData<List<MusicModel>>
}