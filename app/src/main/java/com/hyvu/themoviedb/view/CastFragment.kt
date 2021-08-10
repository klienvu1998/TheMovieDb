package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MovieCreditAdapter
import com.hyvu.themoviedb.databinding.FragmentCastBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class CastFragment : Fragment() {

    private lateinit var mBinding: FragmentCastBinding
    private var movieId = 0
    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[MovieInfoViewModel::class.java]
    }

    companion object {
        const val ARG_MOVIE_ID = "ARG_MOVIE_ID"

        fun newInstance(movieId: Int): CastFragment {
            val f = CastFragment()
            val arg = Bundle()
            arg.putInt(ARG_MOVIE_ID, movieId)
            f.arguments = arg
            return f
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_cast, container, false)
        mBinding = FragmentCastBinding.bind(v)
        movieId = arguments?.getInt(ARG_MOVIE_ID) ?: 0
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.fetchMovieCredits(movieId)
        initView()
        liveData()
    }

    private fun initView() {

    }

    private fun liveData() {
        mViewModel.movieCredits.observe(viewLifecycleOwner, { credits ->
            mBinding.rcvCast.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MovieCreditAdapter(context, credits)
            }
        })
    }
}