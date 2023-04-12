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

class MusicService : Service() , LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    companion object{
        var mediaPlayer = MediaPlayer()
        var isPlaying = false
        var isSurviceRuning = false
        private val _newPlayingMusic = MutableLiveData< List<MusicModel>>()
        val newPlayingMusic: LiveData< List<MusicModel>> = _newPlayingMusic
        var playingPosition = 0
        fun setNewPlaying( list: List<MusicModel>){
            _newPlayingMusic.value = list
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(12, showNotification())
        printList()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification(): Notification {
        val mediaSession = MediaSessionCompat(this, "tag")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setSmallIcon(R.drawable.ic_music_24)
            .build()
        mediaSession.setMetadata(setMataData())
        mediaSession.setPlaybackState(setPlayBack())

        return notification
    }

    private fun setMataData(): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "Song Title")
            .putString(MediaMetadata.METADATA_KEY_ARTIST, "Artist Name")
            .putString(MediaMetadata.METADATA_KEY_ALBUM, "Album Title")
            .putBitmap(
                MediaMetadata.METADATA_KEY_ALBUM_ART,
                BitmapFactory.decodeResource(resources, R.drawable.ic_music_logo)
            )
            .putLong(MediaMetadata.METADATA_KEY_DURATION, 4)
            .build()
    }

    private fun setPlayBack(): PlaybackStateCompat {
        return PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 2, 0f)
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
    }

    fun printList(){
        newPlayingMusic.observe(this){
            Log.d("Ankit",it.toString())

        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry


}

