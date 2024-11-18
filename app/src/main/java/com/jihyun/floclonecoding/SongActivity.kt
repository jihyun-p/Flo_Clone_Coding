package com.jihyun.floclonecoding

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jihyun.floclonecoding.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayStatus(false) // 아래 함수의 else 부분 출력됨 = 정지 상태 = songMiniplayerIv = GONE
        }
        binding.songPauseIv.setOnClickListener {
            setPlayStatus(true) // 아래 함수의 if 부분 출력 = 재생 상태 = songPauseIv = GONE
        }
        if (intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text=intent.getStringExtra("title")
            binding.songSingerNameTv.text=intent.getStringExtra("singer")

        }
    }

    fun setPlayStatus(isPlaying : Boolean){
        if(isPlaying){ // 재생 중일 때
            binding.songMiniplayerIv.visibility = View.VISIBLE // 재생 아이콘 보임
            binding.songPauseIv.visibility = View.GONE // 정지 아이콘 사라진 상태
        }
        else { // 정지 상태일 때
            binding.songMiniplayerIv.visibility = View.GONE // 재생 아이콘은 사라진 상태
            binding.songPauseIv.visibility = View.VISIBLE // 정지 아이콘은 보인다
        }
    }
}