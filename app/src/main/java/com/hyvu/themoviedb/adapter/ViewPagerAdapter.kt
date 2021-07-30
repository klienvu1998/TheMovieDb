package com.hyvu.themoviedb.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyvu.themoviedb.view.HomeFragment
import com.hyvu.themoviedb.view.SearchFragment
import com.hyvu.themoviedb.view.UserFragment

class ViewPagerAdapter(fragment: FragmentActivity, private val listener: Listener): FragmentStateAdapter(fragment) {

    interface Listener {
        fun onCreateFragment(position: Int): Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return listener.onCreateFragment(position)
    }


}