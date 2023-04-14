package com.gyanHub.mymusic.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.utils.MyApplication.Companion.CHANNEL_ID
import com.gyanHub.mymusic.utils.MyApplication.Companion.EXIT
import com.gyanHub.mymusic.utils.MyApplication.Companion.NEXT
import com.gyanHub.mymusic.utils.MyApplication.Companion.PLAY
import com.gyanHub.mymusic.utils.MyApplication.Companion.PREVIOUS

class MusicService : Service(), ViewModelStoreOwner, LifecycleOwner,
    MediaPlayer.OnCompletionListener ,MediaPlayer.OnErrorListener{
    private val lifecycleRegistry = LifecycleRegistry(this)
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val viewModelStores = ViewModelStore()

    companion object {
        private val _isPlay = MutableLiveData<Boolean>()
        val getIsPlay: LiveData<Boolean> = _isPlay
        val handler = Handler(Looper.getMainLooper())
        var isPlaying = false
        val mediaPlayer = MediaPlayer()
        var playingPosition = 0
        var PlayingList: List<MusicModel>? = null

        private val _isPlaying = MutableLiveData<Boolean>()
        val getIsPlaying: LiveData<Boolean> = _isPlaying

        private fun setIsPlaying(t: Boolean) {
            _isPlaying.value = t
            _isPlay.value = t
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
        private fun setMusic(t: MusicModel) {
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
    private fun updateMusicPosition(context: Context, position: Int) {
        val sharedPreferences = context.getSharedPreferences("musicPlaying", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("position", position)
        editor.apply()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        getIsPlay.observe(this) {
            music.observe(this) { musicList ->
                if (it) {
                    showNotification(R.drawable.ic_pause_24, musicList)
                } else {
                    showNotification(R.drawable.ic_play_24, musicList)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        stopForeground(STOP_FOREGROUND_DETACH)
                    else stopForeground(true)
                }
            }
        }
        updatePosition.observe(this) {
            updateMusicPosition(baseContext, playingPosition)
        }

        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnErrorListener(this)
        mediaSessionCompat = MediaSessionCompat(baseContext, "myMusic")
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


    private fun showNotification(playerBtn: Int, music: MusicModel) {
        val prevIntent =
            Intent(baseContext, MusicNotificationService::class.java).setAction(PREVIOUS)
        val prevPendingIntent =
            PendingIntent.getBroadcast(baseContext, 123, prevIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(baseContext, MusicNotificationService::class.java).setAction(PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            122,
            playIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val nextIntent = Intent(baseContext, MusicNotificationService::class.java).setAction(NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            121,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val exitIntent = Intent(baseContext, MusicNotificationService::class.java).setAction(EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            121,
            exitIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setContentTitle(music.title)
            .setContentText(music.artist)
            .setSmallIcon(R.drawable.ic_music_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(exitPendingIntent)
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2, 3)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(exitPendingIntent)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previous_24, PREVIOUS, prevPendingIntent)
            .addAction(playerBtn, PLAY, playPendingIntent)
            .addAction(R.drawable.ic_next_24, NEXT, nextPendingIntent)
            .addAction(R.drawable.ic_stop_24, EXIT, exitPendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.color.seed))
            .build()
        startForeground(12, notification)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextMusic()
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore
        get() = viewModelStores

    override fun onDestroy() {
        super.onDestroy()
        viewModelStores.clear()
        mediaPlayer.release()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            stopForeground(STOP_FOREGROUND_DETACH)
        else stopForeground(true)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
       gatPath(PlayingList!![playingPosition])
      return true
    }
}
