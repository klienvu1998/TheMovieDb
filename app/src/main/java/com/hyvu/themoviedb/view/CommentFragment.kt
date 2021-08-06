package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.CommentPagingDataAdapter
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentCommentBinding
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel

class CommentFragment : Fragment() {

    companion object {
        const val ARG_MOVIE_DETAIL = "ARG_MOVIE_DETAIL"

        fun newInstance(movieDetail: MovieDetail): CommentFragment {
            val f = CommentFragment()
            val arg = Bundle()
            arg.putParcelable(ARG_MOVIE_DETAIL, movieDetail)
            f.arguments = arg
            return f
        }
    }

    private lateinit var mBinding: FragmentCommentBinding
    private val mViewModel by viewModels<MovieInfoViewModel>()
    private lateinit var currentMovie: MovieDetail
    private var commentPagingDataAdapter: CommentPagingDataAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_comment, container, false)
        mBinding = FragmentCommentBinding.bind(v)
        currentMovie = (context as MainActivity).currentMovie!!
        mViewModel.fetchMovieComments(currentMovie.id)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        liveData()
    }

    private fun initView() {
        mBinding.rcvComment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            commentPagingDataAdapter = CommentPagingDataAdapter(context)
            adapter = commentPagingDataAdapter
        }
    }

    private fun liveData() {
        mViewModel.movieComments.observe(viewLifecycleOwner, { data ->
            mBinding.rcvComment.apply {
                commentPagingDataAdapter?.submitData(lifecycle, data)
            }
        })
    }

}