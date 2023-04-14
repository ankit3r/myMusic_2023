package com.gyanHub.mymusic.activity


import android.app.ActivityManager
import android.app.Service
import android.content.*
import android.os.*
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.adapter.TabAdapter
import com.gyanHub.mymusic.databinding.ActivityMainBinding
import com.gyanHub.mymusic.fragment.AlbumFragment
import com.gyanHub.mymusic.fragment.FolderFragment
import com.gyanHub.mymusic.fragment.SongFragment
import com.gyanHub.mymusic.model.MusicModel
import com.gyanHub.mymusic.playHandal.SongClick
import com.gyanHub.mymusic.service.MusicService
import com.gyanHub.mymusic.service.MusicService.Companion.currentProgressPosition
import com.gyanHub.mymusic.service.MusicService.Companion.getIsPlaying
import com.gyanHub.mymusic.service.MusicService.Companion.isPlaying
import com.gyanHub.mymusic.service.MusicService.Companion.music
import com.gyanHub.mymusic.service.MusicService.Companion.nextMusic
import com.gyanHub.mymusic.service.MusicService.Companion.pauseMusic
import com.gyanHub.mymusic.service.MusicService.Companion.playMusic
import com.gyanHub.mymusic.service.MusicService.Companion.previousMusic
import com.gyanHub.mymusic.service.MusicService.Companion.seekPosition
import com.gyanHub.mymusic.service.MusicService.Companion.updatePosition
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.viewModel.ShareDataViewModel

class MainActivity : AppCompatActivity(), SongClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var musicViewModel: MyMusicViewModel
    private lateinit var shardData: ShareDataViewModel
    private lateinit var backPressCallback: OnBackPressedCallback
    private val handler = Handler(Looper.getMainLooper())
    private var serviceIntent: Intent? = null
    private var isServiceRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // service call
        if (!isServiceRunning(MusicService::class.java)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 serviceIntent = Intent(this@MainActivity, MusicService::class.java)
//                applicationContext.startForegroundService(serviceIntent)
                ContextCompat.startForegroundService(this, serviceIntent!!)
                isServiceRunning = true
//                applicationContext.startService(serviceIntent)
            } else {
                val serviceIntent = Intent(this@MainActivity, MusicService::class.java)
                applicationContext.startService(serviceIntent)
            }
        }

        // viewMode
        musicViewModel = ViewModelProvider(this)[MyMusicViewModel::class.java]
        shardData = ViewModelProvider(this)[ShareDataViewModel::class.java]
        // to get last playing music
        getMusic(false)
        // for tab bar
        setTabLayout()
        // set music on player
        music.observe(this) {
            binding.txtTotalTime.text = shardData.formatDuration(it.duration)
            binding.textView.text = it.title
            binding.seekBar.max = it.duration.toInt()
        }
        binding.btnPlayPuse.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
        }
        binding.btnNext.setOnClickListener { nextMusic() }
        binding.btnPrevious.setOnClickListener { previousMusic() }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var userTouch = false
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekPosition(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userTouch = true
            }
        })
        currentProgressPosition.observe(this) {
            binding.txtRemTime.text = shardData.formatDuration(it.toLong())
            binding.seekBar.progress = it
        }

        getIsPlaying.observe(this) {
            if (it) binding.btnPlayPuse.setImageResource(R.drawable.ic_pause)
            else binding.btnPlayPuse.setImageResource(R.drawable.ic_play)
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
    private fun getMusic(onClick:Boolean) {
        var musicList: List<MusicModel>? = null
        val motionLayout = binding.motionLayout
        val (storedFilePath, storedPosition, _) = musicViewModel.getPlayingMusic()
        when (musicViewModel.getPlayingMusic().third) {
            getString(R.string.songF) -> {

                musicViewModel.listOfMusic.observe(this) {
                    musicList = it
                }
                motionLayout.transitionToState(R.id.startSong)
            }
            getString(R.string.folderF) -> {
                musicViewModel.getMusicFromFolder(storedFilePath!!)
                musicViewModel.listOfMusicFromFolder.observe(this) {
                    musicList = it
                }
                motionLayout.transitionToState(R.id.startSong)
            }
        }
       if(!isPlaying || onClick){
           handler.postDelayed({
               val musicListJson = Gson().toJson(musicList)
               val intent = Intent(this, MusicService::class.java)
               intent.putExtra("musicList", musicListJson)
               intent.putExtra("position", storedPosition)
               startService(intent)
           }, 100)
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
        getMusic(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceRunning && !isPlaying) {
            stopService(serviceIntent)
            isServiceRunning = false
            Toast.makeText(this, "service distroy", Toast.LENGTH_SHORT).show()
        }
    }
}