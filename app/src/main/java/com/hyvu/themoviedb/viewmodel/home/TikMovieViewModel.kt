package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.hyvu.themoviedb.data.remote.entity.Genre
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TikMovieViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _tikMovieDetails: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val tikMovieDetails: LiveData<PagingData<MovieDetail>> = _tikMovieDetails
    val movieGenres: List<Genre> = repository.genres

    fun fetchTikMovie() {
        compositeDisposable.add(
                repository.fetchPopularMovies().cachedIn(viewModelScope)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ data ->
                            _tikMovieDetails.postValue(data)
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