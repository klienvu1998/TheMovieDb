package com.hyvu.themoviedb.view.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerMainAdapter(fragment: FragmentActivity, private val listener: Listener): FragmentStateAdapter(fragment) {

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