package com.gyanHub.mymusic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.gyanHub.mymusic.activity.MainActivity
import com.gyanHub.mymusic.utils.MyApplication
import kotlin.system.exitProcess

class MusicNotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            MyApplication.PREVIOUS -> {
                val broadcastIntent = Intent("previous")
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
            }
            MyApplication.PLAY -> {
                val broadcastIntent = Intent("play")
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)

            }
            MyApplication.NEXT -> {
                val broadcastIntent = Intent("next")
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
            }
            MyApplication.EXIT -> {
                val broadcastIntent = Intent("exit")
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
            }
        }
    }

}