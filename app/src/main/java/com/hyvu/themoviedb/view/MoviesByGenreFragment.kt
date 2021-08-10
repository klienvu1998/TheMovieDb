package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MoviesPagingDataAdapter
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.databinding.FragmentMoviesByGenreBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class MoviesByGenreFragment : Fragment() {

    companion object {
        const val ARG_GENRE = "ARG_GENRE_ID"
        const val ARG_GENRE_NAME = "ARG_GENRE_NAME"
        fun newInstance(genre: Genre): MoviesByGenreFragment {
            val f = MoviesByGenreFragment()
            val arg = Bundle()
            arg.putParcelable(ARG_GENRE, genre)
            f.arguments = arg
            return f
        }

        fun newInstance(genre: String): MoviesByGenreFragment {
            val f = MoviesByGenreFragment()
            val arg = Bundle()
            arg.putString(ARG_GENRE_NAME, genre)
            f.arguments = arg
            return f
        }
    }

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[HomeViewModel::class.java]
    }

    private lateinit var genre: Genre
    private lateinit var mBinding: FragmentMoviesByGenreBinding
    private var moviePagingDataAdapter: MoviesPagingDataAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_movies_by_genre, container, false)
        mBinding = FragmentMoviesByGenreBinding.bind(v)
        genre = arguments?.getParcelable(ARG_GENRE) ?: Genre(null, arguments?.getString(ARG_GENRE_NAME, "") ?: "")
        if (genre.id != null) mViewModel.getMoviesPerPage(genre.id!!)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        liveData()
    }

    private fun liveData() {
        mViewModel.responseMovies.observe(viewLifecycleOwner, { pagingData ->
            moviePagingDataAdapter?.submitData(lifecycle, pagingData)
        })
    }

    private fun initView() {
        moviePagingDataAdapter = MoviesPagingDataAdapter(context, listenerMoviesPagingDataAdapter)
        mBinding.rcvMovie.apply {
            val displayMetrics = DisplayMetrics()
            (context as MainActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val itemWidth = (displayMetrics.widthPixels / (110 * resources.displayMetrics.density)).toInt()
            layoutManager = GridLayoutManager(context, itemWidth, GridLayoutManager.VERTICAL, false)
            adapter = moviePagingDataAdapter
        }
        mBinding.toolBarContainer.tvTitle.text = genre.name
        mBinding.toolBarContainer.btnBack.setOnClickListener {
            parentFragment?.childFragmentManager?.popBackStack()
        }
    }

    private val listenerMoviesPagingDataAdapter = object : MoviesPagingDataAdapter.Listener {

    }

}