package com.hyvu.themoviedb.view.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.entity.Genre
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentUserHomeBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.view.homescreen.adapter.UserCategoryAdapter
import com.hyvu.themoviedb.viewmodel.home.SharedViewModel
import com.hyvu.themoviedb.viewmodel.home.UserViewModel
import java.lang.ref.WeakReference
import javax.inject.Inject

class UserHomeFragment: BaseFragment<FragmentUserHomeBinding>() {

    @Inject lateinit var mViewModel: UserViewModel
    @Inject lateinit var mSharedViewModel: SharedViewModel

    private var adapterCategoryMovie: UserCategoryAdapter? = null
    val mapMovies: LinkedHashMap<Genre, List<MovieDetail>> = LinkedHashMap()

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {
        mapMovies[Genre(-1, "Favorite")] = ArrayList()
        mapMovies[Genre(-1, "Watchlist")] = ArrayList()
    }

    override fun initView() {
        adapterCategoryMovie = UserCategoryAdapter(WeakReference(context), userCategoryAdapterListener, LinkedHashMap())
        mBinding.rcvMovie.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterCategoryMovie
        }
    }

    override fun observerLiveData() {
        mSharedViewModel.favoriteList.observe(viewLifecycleOwner, {
            mapMovies[Genre(-1, "Favorite")] = it
            adapterCategoryMovie?.setData(mapMovies)
        })
        mSharedViewModel.watchList.observe(viewLifecycleOwner, {
            mapMovies[Genre(-1, "Watchlist")] = it
            adapterCategoryMovie?.setData(mapMovies)
        })
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserHomeBinding {
        return FragmentUserHomeBinding.inflate(layoutInflater, container, false)
    }

    private val userCategoryAdapterListener = object : UserCategoryAdapter.Listener {
        override fun onClickedSeeAll(genre: Genre) {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.add(R.id.container, MoviesByGenreFragment.newInstance(genre))
                ?.addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                ?.commit()
        }

    }
}