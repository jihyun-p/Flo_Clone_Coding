package com.jihyun.floclonecoding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jihyun.floclonecoding.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() { // HomeFragment에서 앨범 사진 누르면 AlbumFragment로 이동
    lateinit var binding : FragmentAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater,container,false)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()
        }

        binding.songLalacLayout.setOnClickListener {
            Toast.makeText(activity,"LILAC",Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}