package com.hyvu.themoviedb.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.view.home.adapter.UserCategoryAdapter
import com.hyvu.themoviedb.data.remote.entity.Genre
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentUserHomeBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.SharedViewModel
import com.hyvu.themoviedb.viewmodel.home.UserViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class UserHomeFragment: BaseFragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[UserViewModel::class.java]
    }

    private val mSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory)[SharedViewModel::class.java]
    }

    private lateinit var mBinding: FragmentUserHomeBinding
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
        adapterCategoryMovie = UserCategoryAdapter(context, userCategoryAdapterListener, LinkedHashMap())
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_user_home, container, false)
        mBinding = FragmentUserHomeBinding.bind(v)
        return mBinding.root
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