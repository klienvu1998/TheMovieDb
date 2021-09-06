package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.CommentPagingDataAdapter
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentCommentBinding
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.CommentViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class CommentFragment : BaseFragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[CommentViewModel::class.java]
    }

    private lateinit var mBinding: FragmentCommentBinding
    private lateinit var currentMovie: MovieDetail
    private var commentPagingDataAdapter: CommentPagingDataAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_comment, container, false)
        mBinding = FragmentCommentBinding.bind(v)
        mViewModel.fetchMovieComments(currentMovie.id)
        return mBinding.root
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {
        currentMovie = (activity as MainActivity).currentMovie!!
    }

    override fun fetchData() {

    }

    override fun initView() {
        mBinding.rcvComment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            commentPagingDataAdapter = CommentPagingDataAdapter(context)
            adapter = commentPagingDataAdapter
        }
        mBinding.toolBar.apply {
            tvTitle.text = "Comments"
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun observerLiveData() {
        mViewModel.movieComments.observe(this, { data ->
            mBinding.rcvComment.apply {
                commentPagingDataAdapter?.submitData(lifecycle, data)
            }
        })
    }

}