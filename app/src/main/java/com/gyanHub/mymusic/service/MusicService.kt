package com.gyanHub.mymusic.service

import android.app.*
import android.app.Notification.MediaStyle
import android.app.Notification.Style
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.utils.MyApplication.Companion.CHANNEL_ID

class MusicService : Service(), LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    companion object {
        private var mediaPlayer = MediaPlayer()

        // for check music is playing or not
        private val _isPlaying = MutableLiveData<Boolean>()
        val isPlaying: LiveData<Boolean> = _isPlaying

        fun setIsPlaying(t: Boolean) {
            _isPlaying.value = t
        }

        // for position
        private val _position = MutableLiveData<Int>()
        val playingPosition: LiveData<Int> = _position

        fun setPlayingPosition(t: Int) {
            _position.value = t
        }

        // for list of music
        private val _newPlayingMusic = MutableLiveData<List<MusicModel>>()
        val newPlayingMusic: LiveData<List<MusicModel>> = _newPlayingMusic

        fun setNewPlaying(list: List<MusicModel>) {
            Log.d("ANKIT", "print function set New Playing")
            _newPlayingMusic.value = list
        }

        // for current music Model or Music Details
        private val _music = MutableLiveData<MusicModel>()
        val music: LiveData<MusicModel> = _music
        fun setMusic(t: MusicModel) {
            _music.value = t
        }

        // for seekbar current position
        private val _currentProgressPosition = MutableLiveData<Int>()
        val currentProgressPosition: LiveData<Int> = _currentProgressPosition
        fun setProgressPosition(t: Int) {
            _currentProgressPosition.value = t
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        printList()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification(music: MusicModel): Notification {
        val mediaSession = MediaSessionCompat(this, "tag")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setSmallIcon(R.drawable.ic_music_24)
            .build()
        mediaSession.setMetadata(setMataData(music))
        mediaSession.setPlaybackState(setPlayBack())

        return notification
    }

    private fun setMataData(data: MusicModel): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, data.title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, data.artist)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, data.album)
            .putBitmap(
                MediaMetadata.METADATA_KEY_ALBUM_ART,
                BitmapFactory.decodeResource(resources, R.drawable.ic_music_logo)
            )
            .putLong(MediaMetadata.METADATA_KEY_DURATION, data.duration.toLong())
            .build()
    }

    private fun setPlayBack(): PlaybackStateCompat {
        return PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 2, 0f)
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
    }

    private fun printList() {
        newPlayingMusic.removeObservers(this)
        playingPosition.removeObservers(this)
        var position = 0
        var list: List<MusicModel>?
        playingPosition.observe(this) { po ->
            position = po
        }
        newPlayingMusic.observe(this) {
           list = it
            try {
                setMusic(list!![position])
            }catch (e:Exception){
                Log.e("ANKIT","Position Error $position ${list?.size}")
            }
        }
        music.observe(this) {
            startForeground(12, showNotification(it))
        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry


}

