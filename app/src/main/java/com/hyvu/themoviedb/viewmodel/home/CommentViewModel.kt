package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.hyvu.themoviedb.data.remote.entity.Comment
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CommentViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _movieComments: MutableLiveData<PagingData<Comment>> = MutableLiveData()
    val movieComments: LiveData<PagingData<Comment>> = _movieComments

    val currentMovie = repository.currentMovieDetail

    fun fetchMovieComments(movieId: Int) {
        compositeDisposable.add(
                repository.fetchMovieComments(movieId).cachedIn(viewModelScope)
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