package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hyvu.themoviedb.data.entity.Comment
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CommentViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _movieComments: MutableLiveData<PagingData<Comment>> = MutableLiveData()
    val movieComments: LiveData<PagingData<Comment>> = _movieComments

    fun fetchMovieComments(movieId: Int) {
        compositeDisposable.add(
                repository.fetchMovieComments(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _movieComments.postValue(it)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}