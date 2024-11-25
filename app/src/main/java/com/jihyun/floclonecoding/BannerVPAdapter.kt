package com.jihyun.floclonecoding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment:Fragment) :FragmentStateAdapter(fragment) {

    // HomeFragment에서 막 쓰지 않게 하기 위해서 private 걸어둠
    private val fragmentList: ArrayList<Fragment> = ArrayList()

    //Fragment에 담긴 data 갯수만큼 전달
    override fun getItemCount(): Int = fragmentList.size


    override fun createFragment(position: Int): Fragment = fragmentList[position] //get 아이템 값 수 만큼 출력 1,2,3,4...

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment) // 인자 값으로 받은 fragment를 추가해 줄 거야
        notifyItemInserted(fragmentList.size-1)
    }

}