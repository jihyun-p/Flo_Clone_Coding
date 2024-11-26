package com.jihyun.floclonecoding

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jihyun.floclonecoding.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    // 전역 변수 선언
    lateinit var binding: ActivitySongBinding // View Binding 객체
    lateinit var timer: Timer // 노래 진행 시간 업데이트를 위한 Timer 객체
    private var mediaPlayer: MediaPlayer? = null // 음악 재생을 위한 MediaPlayer 객체
    private var gson: Gson = Gson() // JSON 처리를 위한 Gson 객체

    val songs = arrayListOf<Song>() // 노래 리스트를 저장하는 ArrayList
    lateinit var songDB: SongDatabase // 노래 정보를 관리하는 로컬 데이터베이스 객체
    var nowPos = 0 // 현재 재생 중인 노래의 인덱스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater) // View Binding 초기화
        setContentView(binding.root)

        initPlayList() // 노래 목록 초기화
        initSong() // 현재 재생할 노래 설정
        initClickListener() // 각 버튼의 클릭 이벤트 리스너 설정
    }

    override fun onPause() {
        super.onPause()

        // 현재 노래의 재생 상태와 진행 시간을 저장
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime) / 100) / 1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false) // 노래를 일시 정지 상태로 변경

        // SharedPreferences를 이용하여 현재 재생 중인 노래 ID 저장
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.apply() // 변경 사항 적용
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() // Timer 스레드 종료
        mediaPlayer?.release() // MediaPlayer 리소스 해제
        mediaPlayer = null
    }

    private fun initPlayList() {
        // 데이터베이스에서 노래 정보를 불러와 리스트에 추가
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener() {
        // 각 버튼의 클릭 이벤트 처리
        binding.songDownIb.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true) // 재생 버튼 클릭 시 노래 재생
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false) // 일시정지 버튼 클릭 시 노래 일시 정지
        }

        binding.songNextIv.setOnClickListener {
            moveSong(+1) // 다음 곡으로 이동
        }

        binding.songPreviousIv.setOnClickListener {
            moveSong(-1) // 이전 곡으로 이동
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike) // 좋아요 상태 토글
        }
    }

    private fun initSong() {
        // SharedPreferences에서 이전에 재생 중이던 노래 ID를 가져옴
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        nowPos = getPlayingSongPosition(songId) // 노래 ID로 리스트에서 위치를 찾음

        Log.d("now Song ID", songs[nowPos].id.toString())

        startTimer() // 노래 진행 상황을 업데이트할 Timer 시작
        setPlayer(songs[nowPos]) // 현재 노래 정보 설정
    }

    private fun setLike(isLike: Boolean) {
        // 좋아요 상태 변경 및 데이터베이스 업데이트
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        // 좋아요 버튼의 아이콘 변경
        if (!isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun moveSong(direct: Int) {
        // 노래를 이동(이전/다음 곡)하며 경계 체크
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }

        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct // 현재 곡 위치 업데이트

        // Timer와 MediaPlayer를 재설정
        timer.interrupt()
        startTimer()
        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos]) // 새로운 곡 설정
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        // 주어진 songId로 현재 재생 중인 노래의 위치를 찾음
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0 // 기본적으로 첫 번째 곡을 반환
    }

    private fun setPlayer(song: Song) {
        // 화면에 노래 정보를 설정
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music) // MediaPlayer 초기화

        // 좋아요 상태에 따라 아이콘 변경
        if (song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying) // 플레이어 상태 설정
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        // 노래 재생/일시 정지 상태 설정
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start() // 재생 시작
        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            mediaPlayer?.pause() // 재생 일시 정지
        }
    }

    private fun startTimer() {
        // Timer 객체 생성 및 시작
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) { // 재생 시간이 종료되면 중지
                        break
                    }

                    if (isPlaying) {
                        sleep(50) // 50ms마다 업데이트
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) { // 1초 단위로 시간 업데이트
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}
