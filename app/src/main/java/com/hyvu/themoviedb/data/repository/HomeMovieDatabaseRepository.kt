package com.hyvu.themoviedb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.database.HomeMovieDetailDao
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomeMovieDatabaseRepository @Inject constructor(private val homeMovieDetailDao: HomeMovieDetailDao) {

    private val compositeDisposable = CompositeDisposable()
    private val _allMovieDetails: MutableLiveData<List<MovieDetail>> = MutableLiveData()
    val allMovieDetails: LiveData<List<MovieDetail>> = _allMovieDetails

    fun insert(movieDetail: MovieDetail) {
        compositeDisposable.add(
            Completable.create {
                homeMovieDetailDao.insert(movieDetail)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }
        )
    }

    fun getListMovieDetails() {
        compositeDisposable.add(
            homeMovieDetailDao.getListMovieDetail()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _allMovieDetails.postValue(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun clear() {
        compositeDisposable.clear()
    }

}