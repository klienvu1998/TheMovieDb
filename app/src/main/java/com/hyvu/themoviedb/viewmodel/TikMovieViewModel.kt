package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hyvu.themoviedb.data.entity.Genres
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class TikMovieViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _tikMovieDetails: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val tikMovieDetails: LiveData<PagingData<MovieDetail>> = _tikMovieDetails
    val movieGenres: LiveData<Genres> = repository.responseListMovieGenre

    fun fetchLatestMovie() {
        repository.fetchLatestMovie()
    }

    fun fetchTikMovie() {
        compositeDisposable.add(
                repository.fetchPopularMovies()
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