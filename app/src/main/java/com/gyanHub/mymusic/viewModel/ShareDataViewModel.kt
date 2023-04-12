package com.gyanHub.mymusic.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gyanHub.mymusic.model.MusicModel

class ShareDataViewModel : ViewModel() {
    private val _musicFrom = MutableLiveData<String>()
    val musicFrom: LiveData<String> = _musicFrom

    fun setMusicFrom(t: String) {
        _musicFrom.value = t
    }

     var _listOfMusic : List<MusicModel>? = null


    private val _position = MutableLiveData<Int>()
    val playingPosition: LiveData<Int> = _position

    fun setPlayingMusicList(list: List<MusicModel>) {
        _listOfMusic = emptyList()
        _listOfMusic = list
    }

    fun setPlaingPosition(t: Int) {
        _position.value = t
    }
    fun formatDuration(duration: Long): String {
        val hours = duration / (1000 * 60 * 60)
        val minutes = duration % (1000 * 60 * 60) / (1000 * 60)
        val seconds = duration % (1000 * 60) / 1000
        return if (hours == 0L) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}