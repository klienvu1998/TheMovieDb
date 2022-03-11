package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import com.hyvu.themoviedb.view.homescreen.HomeFragment
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.collections.LinkedHashMap

class HomeViewModel @Inject constructor(private val repository: MovieRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var apiService: TheMovieDbAPI

    @Inject
    lateinit var database: HomeDatabase

    private val _movieByGenre: MutableLiveData<Map<Genre, List<MovieDetail>>> = MutableLiveData()
    val movieByGenre: LiveData<Map<Genre, List<MovieDetail>>> = _movieByGenre

    fun fetchMovies() {
        val map = LinkedHashMap<Genre, List<MovieDetail>>()
        compositeDisposable.add(
            apiService.getTrendingMovies()
                .flatMapObservable { trendingMovies ->
                    map[Genre(-1, HomeFragment.TRENDING_MOVIE)] = trendingMovies.trendingMovies.map { it.convertToMovieDetail() }
                    apiService.getListGenres()
                }.concatMap {
                    database.homeMovieDetailDao().insertListGenres(it.genres)
                    repository.genres = it.genres as ArrayList<Genre>
                    Observable.fromIterable(it.genres)
                }.concatMapSingle { genre ->
                    genre.id?.let { apiService.getMoviesByGenre(it, 1).map { listMovies -> genre to listMovies } }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    map[it.first] = it.second.movieDetails
                }, { e ->
                    e.printStackTrace()
                }, {
                    repository.mapHomeMovies = map
                    _movieByGenre.postValue(map)
                })
        )
    }

    fun fetchMoviesDatabase() {
        val map = LinkedHashMap<Genre, List<MovieDetail>>()
        compositeDisposable.add(
            database.homeMovieDetailDao().getListGenres()
                .flatMapObservable {
                    repository.genres = it as ArrayList<Genre>
                    Observable.fromIterable(it)
                }
                .concatMapSingle { genre ->
                    genre.id?.let { id -> database.homeMovieDetailDao().getMoviesByGenre(id).map { genre to it } }
                }.concatMapSingle {
                    database.homeMovieDetailDao().insertListMovieDetail(it.second)
                    Single.just(it)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    map[it.first] = it.second
                }, { e ->
                    e.printStackTrace()
                }, {
                    repository.mapHomeMovies = map
                    _movieByGenre.postValue(map)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}