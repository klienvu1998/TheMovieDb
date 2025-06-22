package com.hyvu.themoviedb.view.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentCommentBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.view.homescreen.adapter.CommentPagingDataAdapter
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import com.hyvu.themoviedb.viewmodel.home.CommentViewModel
import java.lang.ref.WeakReference
import javax.inject.Inject

class CommentFragment : BaseFragment<FragmentCommentBinding>() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[CommentViewModel::class.java]
    }

    private lateinit var currentMovie: MovieDetail
    private var commentPagingDataAdapter: CommentPagingDataAdapter? = null

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCommentBinding {
        return FragmentCommentBinding.inflate(layoutInflater, container, false)
    }

    override fun getBundle() {
        currentMovie = mViewModel.currentMovie!!
    }

    override fun fetchData() {
        mViewModel.fetchMovieComments(currentMovie.movieId)
    }

    override fun initView() {
        mBinding.rcvComment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            commentPagingDataAdapter = CommentPagingDataAdapter(WeakReference(context))
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