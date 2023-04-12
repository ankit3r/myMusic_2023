package com.gyanHub.mymusic.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.gyanHub.mymusic.model.MusicModel
import java.io.File

class MusicRepository(private val context: Context) {
    suspend fun getAllMusic(): List<MusicModel> {
        return readMusic("")
    }

    suspend fun getAllMusicFolders(): Set<String> {
        val folderSet = mutableSetOf<String>()
        val musicList = getAllMusic()
        for (music in musicList) {
            val file = File(music.data)
            val folder = file.parentFile?.path
            folder?.let { folderSet.add(it) }
        }
        return folderSet
    }

    suspend fun getAllMusicFromFolder(folderPath: String): List<MusicModel> {
        return readMusic(folderPath)
    }

    private fun readMusic(folderPath: String): List<MusicModel> {
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        val musicList = mutableListOf<MusicModel>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )
        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val album = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)
                val data = cursor.getString(dataColumn)
                if (folderPath.isNotEmpty()) {
                    if (data.startsWith(folderPath)) {
                        val music = MusicModel(id, title, album, artist, duration, data)
                        musicList.add(music)
                    }
                } else {
                    val music = MusicModel(id, title, album, artist, duration, data)
                    musicList.add(music)
                }
            }
        }
        return musicList
    }

    private val sharedPreferences =
        context.getSharedPreferences("musicPlaying", Context.MODE_PRIVATE)

    fun saveMusicData(position: Int, fileName: String) {
        val editor = sharedPreferences.edit()
        editor.putInt("position", position)
        editor.putString("fileName", fileName)
        editor.apply()
    }

    fun updateMusicPosition(position: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("position", position)
        editor.apply()
    }

    fun updateMusicFolder(filePath: String) {
        val editor = sharedPreferences.edit()
        editor.putString("filePath", filePath)
        editor.apply()
    }


    fun getMusicData(): Triple<String?, Int, String?> {
        val filePath = sharedPreferences.getString("filePath", null)
        val position = sharedPreferences.getInt("position", 0)
        val fileName = sharedPreferences.getString("fileName", null)
        return Triple(filePath, position, fileName)
    }

}