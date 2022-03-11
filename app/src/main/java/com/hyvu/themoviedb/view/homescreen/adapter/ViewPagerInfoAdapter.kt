package com.hyvu.themoviedb.view.homescreen.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerInfoAdapter(fragment: Fragment, private val listener: Listener): FragmentStateAdapter(fragment) {

    interface Listener {
        fun onCreateFragment(position: Int): Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return listener.onCreateFragment(position)
    }


}