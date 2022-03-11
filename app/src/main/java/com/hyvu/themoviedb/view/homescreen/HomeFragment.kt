package com.hyvu.themoviedb.view.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.view.homescreen.adapter.HomeCategoryMovieAdapter
import com.hyvu.themoviedb.data.remote.entity.Genre
import com.hyvu.themoviedb.databinding.FragmentMoviesHomeFragmentBinding
import com.hyvu.themoviedb.utils.Constraints
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.HomeViewModel
import com.hyvu.themoviedb.viewmodel.home.SharedViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    companion object {
        const val TRENDING_MOVIE = "TRENDING_MOVIE"
    }

    private lateinit var mBinding: FragmentMoviesHomeFragmentBinding

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory)[HomeViewModel::class.java]
    }
    private val mSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory)[SharedViewModel::class.java]
    }

    private var adapterHomeCategoryMovie: HomeCategoryMovieAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_movies_home_fragment, container, false)
        mBinding = FragmentMoviesHomeFragmentBinding.bind(v)
        return mBinding.root
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {
        if (isOnline) {
            val sessionId = userManager.sessionId
            if (sessionId.isEmpty()) {
                mViewModel.fetchMovies()
            } else {
                mSharedViewModel.fetchUserData(userManager.accountId ?: 0, userManager.sessionId)
            }
        } else {
            mViewModel.fetchMoviesDatabase()
        }
    }

    override fun initView() {
        mBinding.rcvCategory.apply {
            adapterHomeCategoryMovie = HomeCategoryMovieAdapter(WeakReference(context), listenerHomeCategoryAdapter, LinkedHashMap())
            adapterHomeCategoryMovie?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = linearLayoutManager
            adapter = adapterHomeCategoryMovie
        }
    }

    override fun observerLiveData() {
        mViewModel.movieByGenre.observe(viewLifecycleOwner, { data ->
            if (data.isNotEmpty()) mBinding.progressBar.visibility = View.GONE
            adapterHomeCategoryMovie?.addMovieData(data)
        })
        mSharedViewModel.initComplete.observe(this, {
            if (it == SharedViewModel.STATUS.SUCCESS) {
                mViewModel.fetchMovies()
            } else if (it == SharedViewModel.STATUS.FAIL) {
                mBinding.errorLayout.visibility = View.VISIBLE
                mBinding.progressBar.visibility = View.GONE
            } else {
                mBinding.progressBar.visibility = View.VISIBLE
            }
        })
    }

    private val listenerHomeCategoryAdapter = object : HomeCategoryMovieAdapter.Listener {
        override fun onClickedSeeAll(genre: Genre) {
            parentFragment?.childFragmentManager?.beginTransaction()
                    ?.add(R.id.home_container, MoviesByGenreFragment.newInstance(genre))
                    ?.addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                    ?.commit()
        }

        override fun onClickedTrending() {
            parentFragment?.childFragmentManager?.beginTransaction()
                    ?.add(R.id.home_container,
                        MoviesByGenreFragment.newInstance(Genre(-1, Constraints.TRENDING_MOVIE))
                    )
                    ?.addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                    ?.commit()
        }
    }

    override fun onDestroyView() {
        context?.let { Glide.get(it).clearMemory() }
        super.onDestroyView()
    }
}