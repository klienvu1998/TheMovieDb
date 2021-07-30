package com.hyvu.themoviedb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap

object MovieRepository {
    private val compositeDisposable = CompositeDisposable()

    private val _responseMovieVideos = MutableLiveData<MovieVideo>()
    val responseMovieVideos: LiveData<MovieVideo> = _responseMovieVideos

    private val _responseCurrentMovieDetail = MutableLiveData<MovieDetails>()
    val responseCurrentMovieDetail: LiveData<MovieDetails> = _responseCurrentMovieDetail

    private val _responseListMovieGenre: MutableLiveData<Genres> = MutableLiveData()
    val responseListMovieGenre: LiveData<Genres> = _responseListMovieGenre

    private val _responseMovieByGenre: MutableLiveData<Map<Genre, MoviesByGenre>> = MutableLiveData()
    val responseMovieByGenre: LiveData<Map<Genre, MoviesByGenre>> = _responseMovieByGenre

    fun fetchMovieDetails(movieId: Int) {
        compositeDisposable.add(
                TheMovieDbClient.getClient().getMovieDetails(movieId)
                        .flatMap { detail ->
                            _responseCurrentMovieDetail.postValue(detail)
                            TheMovieDbClient.getClient().getMovieVideos(movieId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ videos ->
                            _responseMovieVideos.postValue(videos)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    fun fetchListMovieGenres() {
        var i = 0
        val map = ConcurrentHashMap<Genre, MoviesByGenre>()
        compositeDisposable.add(
                TheMovieDbClient.getClient().getListGenres()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ data ->
                            _responseListMovieGenre.postValue(data)
                            Observable.just(data.genres)
                                    .concatMap { genres -> Observable.fromIterable(genres) }
                                    .concatMap { genre ->
                                        Observable.just(genre)
                                        TheMovieDbClient.getClient().getListMovieOverview(genre.id)
                                    }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ movies ->
                                        map[data.genres[i++]] = movies
                                    }, { e ->
                                        e.printStackTrace()
                                    }, {
                                        _responseMovieByGenre.postValue(map)
                                    })
                        }, { e ->
                            e.printStackTrace()
                        })
        )

    }

    fun fetchMovieVideos(movieId: Int) {
        compositeDisposable.add(
                TheMovieDbClient.getClient().getMovieVideos(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ movieVideos ->
                            _responseMovieVideos.postValue(movieVideos)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }
}