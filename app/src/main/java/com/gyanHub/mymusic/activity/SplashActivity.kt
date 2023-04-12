package com.gyanHub.mymusic.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.postDelayed
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.viewModel.PermissionViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler(Looper.getMainLooper())
        ViewModelProvider(this)[PermissionViewModel::class.java].requestPermissions(this) {
            if (it) handler.postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000) else finish()
        }

    }
}