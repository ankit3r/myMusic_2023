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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
        val handler = Handler(Looper.getMainLooper())
        private var position = 0
        private var list: List<MusicModel>? = null
        var isPlaying = false
        private var playThisMusic : MusicModel? = null

        // for check music is playing or not
        private val _isPlaying = MutableLiveData<Boolean>()
        val getIsPlaying: LiveData<Boolean> = _isPlaying

        fun setIsPlaying(t: Boolean) {
            isPlaying = t
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

        // progress
        private val updateUI = object : Runnable {
            override fun run() {
                try { setProgressPosition(mediaPlayer.currentPosition) }
                catch (e: Exception) { e.printStackTrace() }
                if (isPlaying) { handler.postDelayed(this, 1000) }
            }
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

        playingPosition.observe(this) { po ->
            position = po
        }
        newPlayingMusic.observe(this) {
            list = it
            try {
                setMusic(list!![position])
            } catch (e: Exception) {
                Log.e("ANKIT", "Position Error $position ${list?.size}")
            }
        }
        music.observe(this) {
            playThisMusic = it
            gatPath()
            startForeground(12, showNotification(it))
        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry

    private fun gatPath() {
        Log.e("ANKIT","playingMusic = $playThisMusic")
        try {
            mediaPlayer.reset()
//            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            playMusic()
        } catch (e: Exception) {
            return
        }

    }

    private fun playMusic() {
        mediaPlayer.start()
        setIsPlaying(true)
        handler.post(object : Runnable {
            override fun run() {
                updateUI.run()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        setIsPlaying(false)
        handler.removeCallbacks(updateUI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            stopForeground(STOP_FOREGROUND_DETACH) else
            stopForeground(true)
    }

    private fun nextMusic() {
        if (position == (list!!.size - 1)) setPlayingPosition(0)
        else setPlayingPosition(++position)
        gatPath()
    }

    private fun previousMusic() {
        if (position == 0) setPlayingPosition((list!!.size - 1))
        else setPlayingPosition(--position)
        gatPath()

    }

}

