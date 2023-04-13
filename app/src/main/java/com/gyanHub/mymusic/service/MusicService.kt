package com.gyanHub.mymusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyanHub.mymusic.model.MusicModel

class MusicService : Service() , MediaPlayer.OnCompletionListener{

    companion object {
        val handler = Handler(Looper.getMainLooper())
        var isPlaying = false
        val mediaPlayer = MediaPlayer()
        var playingPosition = 0
        var seekPosition: Int = 0
        var PlayingList: List<MusicModel>? = null

        private val _isPlaying = MutableLiveData<Boolean>()
        val getIsPlaying: LiveData<Boolean> = _isPlaying

        fun setIsPlaying(t: Boolean) {
            _isPlaying.value = t
        }

        private val _updatePosition = MutableLiveData<Int>()
        val updatePosition: LiveData<Int> = _updatePosition
        fun updatePosition(t: Int) {
            _updatePosition.value = t
        }

        // for seekbar current position
        private val _currentProgressPosition = MutableLiveData<Int>()
        val currentProgressPosition: LiveData<Int> = _currentProgressPosition
        fun setProgressPosition(t: Int) {
            _currentProgressPosition.value = t
        }

        // current music
        private val _music = MutableLiveData<MusicModel>()
        val music: LiveData<MusicModel> = _music
        fun setMusic(t: MusicModel) {
            _music.value = t
        }

        // progress
        private val updateUI = object : Runnable {
            override fun run() {
                try {
                    setProgressPosition(mediaPlayer.currentPosition)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (isPlaying) {
                    handler.postDelayed(this, 1000)
                }
            }
        }

//        private fun gatPath(music: MusicModel) {
//            if (music.duration != 0L) try {
//                setMusic(music)
//                mediaPlayer.reset()
//                mediaPlayer.setDataSource(music.data)
//                mediaPlayer.prepare()
//                playMusic()
//            } catch (e: Exception) {
//                return
//            } else nextMusic()
//
//
//        }
        private fun gatPath(music: MusicModel) {
            if (music.duration != 0L) try {
                setMusic(music)
                mediaPlayer.reset()
                mediaPlayer.setDataSource(music.data)
                mediaPlayer.prepare()
                playMusic()
            } catch (e: Exception) {
                return
            } else nextMusic()
        }


        fun seekPosition(po: Int) {
            mediaPlayer.seekTo(po)
        }

        fun playMusic() {
            mediaPlayer.start()
            isPlaying = true
            setIsPlaying(true)
            handler.post(object : Runnable {
                override fun run() {
                    updateUI.run()
                    handler.postDelayed(this, 1000)
                }
            })
        }

        fun pauseMusic() {
            mediaPlayer.pause()
            isPlaying = false
            setIsPlaying(false)
            handler.removeCallbacks(updateUI)
        }

        fun nextMusic() {
            playingPosition = if (playingPosition == (PlayingList!!.size - 1)) 0
            else ++playingPosition
            updatePosition(playingPosition)
            gatPath(PlayingList!![playingPosition])

        }

        fun previousMusic() {
            playingPosition = if (playingPosition == 0) (PlayingList!!.size - 1)
            else --playingPosition
            updatePosition(playingPosition)
            gatPath(PlayingList!![playingPosition])

        }

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.setOnCompletionListener(this)

        val musicListJson = intent?.getStringExtra("musicList")
        val position = intent?.getIntExtra("position", 0)
        val listType = object : TypeToken<List<MusicModel>>() {}.type
        val musicList = Gson().fromJson<List<MusicModel>>(musicListJson, listType)
        if (musicList != null && position != null) {
            PlayingList = musicList
            playingPosition = position
            gatPath(musicList[position])
        }

        return super.onStartCommand(intent, flags, startId)

    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextMusic()
    }
}
