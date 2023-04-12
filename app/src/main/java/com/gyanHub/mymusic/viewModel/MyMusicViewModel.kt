package com.gyanHub.mymusic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.repository.MusicRepository
import kotlinx.coroutines.launch


class MyMusicViewModel(application: Application) : AndroidViewModel(application) {
    private val musicRepository = MusicRepository(application)
    private val _listOfMusic = MutableLiveData<List<MusicModel>>()
    val listOfMusic: LiveData<List<MusicModel>> = _listOfMusic

    private val _listOfMusicFromFolder = MutableLiveData<List<MusicModel>>()
    val listOfMusicFromFolder: LiveData<List<MusicModel>> = _listOfMusicFromFolder

    private val _listOfFolder = MutableLiveData<Set<String>>()
    val listOfFolder: LiveData<Set<String>> = _listOfFolder

    private fun getAllMusic() {
        viewModelScope.launch {
            _listOfMusic.value = musicRepository.getAllMusic()
        }
    }

    private fun getAllMusicFolders() {
        viewModelScope.launch {
            _listOfFolder.value = musicRepository.getAllMusicFolders()
        }
    }

    fun getMusicFromFolder(folderPath: String) {
        viewModelScope.launch {
            _listOfMusicFromFolder.value = musicRepository.getAllMusicFromFolder(folderPath)
        }
    }

    fun savePlayingMusicData(position: Int, fileName: String) {
        musicRepository.saveMusicData(position, fileName)
    }

    fun getPlayingMusic(): Triple<String?, Int, String?> {
        return musicRepository.getMusicData()
    }

    fun updatePlayingPosition(position: Int) {
        musicRepository.updateMusicPosition(position)
    }

    fun updatePlayingFolder(filePath: String) {
        musicRepository.updateMusicFolder(filePath)
    }

    init {
        getAllMusic()
        getAllMusicFolders()
    }
}