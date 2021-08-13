package com.hyvu.themoviedb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.repository.datasource.MoviePagingSource
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.datasource.CommentPagingSource
import com.hyvu.themoviedb.data.repository.datasource.PopularMoviePagingSource
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.utils.Constraints
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScope
class MovieRepository @Inject constructor(val apiService: TheMovieDbAPI) {
    private val compositeDisposable = CompositeDisposable()

    private val _responseMovieVideos = MutableLiveData<MovieVideos>()
    val responseMovieVideos: LiveData<MovieVideos> = _responseMovieVideos

    private val _responseCurrentMovieDetail = MutableLiveData<MovieFullDetails>()
    val responseCurrentMovieDetail: LiveData<MovieFullDetails> = _responseCurrentMovieDetail

    private val _responseCurrentMovieDetailForTikMovie = MutableLiveData<TikMovie>()
    val responseCurrentMovieDetailForTikMovie: LiveData<TikMovie> = _responseCurrentMovieDetailForTikMovie

    private val _responseListMovieGenre: MutableLiveData<Genres> = MutableLiveData()
    val responseListMovieGenre: LiveData<Genres> = _responseListMovieGenre

    private val _responseMovieByGenre: MutableLiveData<Pair<Genre, List<MovieDetail>>> = MutableLiveData()
    val responseMovieByGenre: LiveData<Pair<Genre, List<MovieDetail>>> = _responseMovieByGenre

    private val _responseTrendingMovies: MutableLiveData<TrendingMovies> = MutableLiveData()
    val responseTrendingMovies: LiveData<TrendingMovies> = _responseTrendingMovies

    private val _responseLatestMovieDetail: MutableLiveData<MovieDetail> = MutableLiveData()
    val responseLatestMovieDetail: LiveData<MovieDetail> = _responseLatestMovieDetail

    private val _responseMovieCredits: MutableLiveData<Credits> = MutableLiveData()
    val responseMovieCredits: LiveData<Credits> = _responseMovieCredits

    fun fetchMovieCredits(movieId: Int) {
        compositeDisposable.add(
            apiService.getCredits(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _responseMovieCredits.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchTrendingMovie() {
        compositeDisposable.add(
                apiService.getTrendingMovies()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _responseTrendingMovies.postValue(it)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    fun fetchMovieDetails(movieId: Int) {
        compositeDisposable.add(
                apiService.getMovieDetails(movieId)
                        .flatMap { detail ->
                            _responseCurrentMovieDetail.postValue(detail)
                            apiService.getMovieVideos(movieId)
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

    fun fetchHomeListMovieByGenres() {
        var i = 0
        compositeDisposable.add(
                apiService.getListGenres()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ data ->
                            _responseListMovieGenre.postValue(data)
                            Observable.just(data.genres)
                                    .concatMap { genres -> Observable.fromIterable(genres) }
                                    .concatMapSingle { genre ->
                                        genre.id?.let { apiService.getMoviesByGenre(it, 1) }
                                    }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ movies ->
                                        _responseMovieByGenre.postValue(data.genres[i++] to movies.movieDetails )
                                    }, { e ->
                                        e.printStackTrace()
                                    }, {

                                    })
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    fun fetchMovieByGenrePerPage(genreId: Int): Flowable<PagingData<MovieDetail>> {
        return Pager(
            config = PagingConfig(
                maxSize = Constraints.MAX_ITEM_PER_SCROLL,
                pageSize = Constraints.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, genreId) }
        ).flowable
    }

    fun fetchPopularMovies(): Flowable<PagingData<MovieDetail>> {
        return Pager(
                config = PagingConfig(
                        maxSize =  Constraints.MAX_ITEM_PER_SCROLL,
                        pageSize = Constraints.NETWORK_PAGE_SIZE,
                        enablePlaceholders = false
                ),
                pagingSourceFactory = { PopularMoviePagingSource(apiService) }
        ).flowable
    }

    fun fetchMovieComments(movieId: Int): Flowable<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                maxSize = Constraints.MAX_ITEM_PER_SCROLL,
                pageSize = Constraints.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommentPagingSource(apiService, movieId) }
        ).flowable
    }

    fun fetchLatestMovie() {
        compositeDisposable.add(
                apiService.getLatestMovie()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _responseLatestMovieDetail.postValue(it)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }


    fun deinit() {
        compositeDisposable.clear()
    }
}