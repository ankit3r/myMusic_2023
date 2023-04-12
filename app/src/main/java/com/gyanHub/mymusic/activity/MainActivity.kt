package com.gyanHub.mymusic.activity

import android.app.ActivityManager
import android.app.Service
import android.content.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.tabs.TabLayoutMediator
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.adapter.TabAdapter
import com.gyanHub.mymusic.databinding.ActivityMainBinding
import com.gyanHub.mymusic.fragment.AlbumFragment
import com.gyanHub.mymusic.fragment.FolderFragment
import com.gyanHub.mymusic.fragment.SongFragment
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.playHandal.SongClick
import com.gyanHub.mymusic.service.MusicService
import com.gyanHub.mymusic.service.MusicService.Companion.listOfPlayingMusic
import com.gyanHub.mymusic.service.MusicService.Companion.playingPosition
import com.gyanHub.mymusic.service.MusicService.Companion.setNewPlaying
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.viewModel.ShareDataViewModel
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var musicViewModel: MyMusicViewModel
    private lateinit var shardData: ShareDataViewModel
    private lateinit var backPressCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // service call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceIntent = Intent(this@MainActivity, MusicService::class.java)
            applicationContext.startForegroundService(serviceIntent)
        } else {
            val serviceIntent = Intent(this@MainActivity, MusicService::class.java)
            applicationContext.startService(serviceIntent)
        }


    }

    private fun getMusic() {
        val motionLayout = binding.motionLayout
        val (storedFilePath, storedPosition, _) = musicViewModel.getPlayingMusic()
        playingPosition = storedPosition

        when (musicViewModel.getPlayingMusic().third) {
            getString(R.string.songF) -> {
                musicViewModel.listOfMusic.observe(this) {
                    setNewPlaying(it)
                }
                motionLayout.transitionToState(R.id.startSong)
            }
            getString(R.string.folderF) -> {
                musicViewModel.getMusicFromFolder(storedFilePath!!)
                musicViewModel.listOfMusicFromFolder.observe(this) {
                    setNewPlaying(it)
                }
                motionLayout.transitionToState(R.id.startSong)
            }
        }


    }

}