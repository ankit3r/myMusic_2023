package com.gyanHub.mymusic.activity


import android.app.ActivityManager
import android.content.*
import android.os.*
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import com.gyanHub.mymusic.service.MusicService.Companion.music
import com.gyanHub.mymusic.service.MusicService.Companion.setNewPlaying
import com.gyanHub.mymusic.service.MusicService.Companion.setPlayingPosition
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.viewModel.ShareDataViewModel

class MainActivity : AppCompatActivity(), SongClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var musicViewModel: MyMusicViewModel
    private lateinit var shardData: ShareDataViewModel
    private lateinit var backPressCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // service call
        if (!isServiceRunning(MusicService::class.java)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceIntent = Intent(this@MainActivity, MusicService::class.java)
                applicationContext.startForegroundService(serviceIntent)
            } else {
                val serviceIntent = Intent(this@MainActivity, MusicService::class.java)
                applicationContext.startService(serviceIntent)
            }
        }
        // viewMode
        musicViewModel = ViewModelProvider(this)[MyMusicViewModel::class.java]
        shardData = ViewModelProvider(this)[ShareDataViewModel::class.java]
        // to get last playing music
        getMusic()
        // for tab bar
        setTabLayout()
        // set music on player
        music.observe(this){
            binding.txtTotalTime.text = shardData.formatDuration(it.duration)
            binding.textView.text = it.title
            binding.seekBar.max = it.duration.toInt()
        }
    }

    // setUp TabLayout
    private fun setTabLayout() {
        val adapter = TabAdapter(this)
        adapter.addFragment(AlbumFragment(), getString(R.string.album))
        adapter.addFragment(SongFragment(), getString(R.string.songs))
        adapter.addFragment(FolderFragment(), getString(R.string.folder))

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }


    // to get all data when click on any music
    private fun getMusic() {
        Log.d("ANKIT", "get music")
        val motionLayout = binding.motionLayout
        val (storedFilePath, storedPosition, _) = musicViewModel.getPlayingMusic()
        setPlayingPosition(storedPosition)
        when (musicViewModel.getPlayingMusic().third) {
            getString(R.string.songF) -> {
                var musicList : List<MusicModel>? = null
                musicViewModel.listOfMusic.observe(this) {
                    musicList = it
                    setNewPlaying(musicList!!)
                    Log.d("ANKIT", "get music in main $musicList ")
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

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onSongClick() {
        Log.d("ANKIT", "on song click")
        getMusic()
    }
}