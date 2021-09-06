package com.hyvu.themoviedb.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MoviesPagingDataAdapter
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.databinding.FragmentMoviesByGenreBinding
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.CategoryMoviesViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class MoviesByGenreFragment : BaseFragment() {

    companion object {
        const val ARG_GENRE = "ARG_GENRE_ID"
        fun newInstance(genre: Genre): MoviesByGenreFragment {
            val f = MoviesByGenreFragment()
            val arg = Bundle()
            arg.putParcelable(ARG_GENRE, genre)
            f.arguments = arg
            return f
        }
    }

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[CategoryMoviesViewModel::class.java]
    }

    private lateinit var genre: Genre
    private lateinit var mBinding: FragmentMoviesByGenreBinding
    private var moviePagingDataAdapter: MoviesPagingDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_movies_by_genre, container, false)
        mBinding = FragmentMoviesByGenreBinding.bind(v)
        return mBinding.root
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {
        genre = arguments?.getParcelable(ARG_GENRE)!!
    }

    override fun fetchData() {
        mViewModel.getMoviesPerPage(genre.id ?: -1)
    }

    override fun initView() {
        moviePagingDataAdapter = MoviesPagingDataAdapter(context, listenerMoviesPagingDataAdapter)
        mBinding.rcvMovie.apply {
            val displayMetrics = DisplayMetrics()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                context.display?.getRealMetrics(displayMetrics)
            } else {
                @Suppress("DEPRECATION")
                (context as MainActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            }

            val itemWidth = (displayMetrics.widthPixels / (110 * resources.displayMetrics.density)).toInt()
            layoutManager = GridLayoutManager(context, itemWidth, GridLayoutManager.VERTICAL, false)
            adapter = moviePagingDataAdapter
        }
        mBinding.toolBarContainer.tvTitle.text = genre.name
        mBinding.toolBarContainer.btnBack.setOnClickListener {
            parentFragment?.childFragmentManager?.popBackStack()
        }
    }

    override fun observerLiveData() {
        mViewModel.responseMovies.observe(this, { pagingData ->
            moviePagingDataAdapter?.submitData(lifecycle, pagingData)
        })
    }

    private val listenerMoviesPagingDataAdapter = object : MoviesPagingDataAdapter.Listener {

    }

}