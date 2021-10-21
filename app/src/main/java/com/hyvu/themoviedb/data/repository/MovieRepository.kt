package com.hyvu.themoviedb.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.datasource.CommentPagingSource
import com.hyvu.themoviedb.data.repository.datasource.MovieDetailsMediator
import com.hyvu.themoviedb.data.repository.datasource.PopularMoviePagingSource
import com.hyvu.themoviedb.database.HomeDatabase
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.utils.Constraints
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.collections.ArrayList

@ActivityScope
class MovieRepository @Inject constructor(private val apiService: TheMovieDbAPI, private val database: HomeDatabase) {
    private val compositeDisposable = CompositeDisposable()

    private val _responseMovieVideos = MutableLiveData<MovieVideos>()
    val responseMovieVideos: LiveData<MovieVideos> = _responseMovieVideos

    private val _responseCurrentMovieDetail = MutableLiveData<MovieFullDetails>()
    val responseCurrentMovieDetail: LiveData<MovieFullDetails> = _responseCurrentMovieDetail

    private val _responseListMovieGenre: MutableLiveData<List<Genre>> = MutableLiveData()
    val responseListMovieGenre: LiveData<List<Genre>> = _responseListMovieGenre

    private val _responseMovieByGenre: MutableLiveData<Map<Genre, List<MovieDetail>>> = MutableLiveData()
    val responseMovieByGenre: LiveData<Map<Genre, List<MovieDetail>>> = _responseMovieByGenre

    private val _responseTrendingMovies: MutableLiveData<TrendingMovies> = MutableLiveData()
    val responseTrendingMovies: LiveData<TrendingMovies> = _responseTrendingMovies

    private val _responseMovieCredits: MutableLiveData<Credits> = MutableLiveData()
    val responseMovieCredits: LiveData<Credits> = _responseMovieCredits

    private val _favoriteList: MutableLiveData<MoviesListResponse> = MutableLiveData()
    val favoriteList: LiveData<MoviesListResponse> = _favoriteList

    private val _accountDetails: MutableLiveData<AccountDetails> = MutableLiveData()
    val accountDetails: LiveData<AccountDetails> = _accountDetails

    private val _watchList: MutableLiveData<MoviesListResponse> = MutableLiveData()
    val watchList: LiveData<MoviesListResponse> = _watchList

    fun fetchMovieCredits(movieId: Int) {
        compositeDisposable.add(
            apiService.getCredits(movieId)
                .subscribeOn(Schedulers.io())
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
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ videos ->
                            _responseMovieVideos.postValue(videos)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    fun fetchHomeListMovieByGenres() {
        var i = 0
        val map = LinkedHashMap<Genre, List<MovieDetail>>()
        compositeDisposable.add(
            TheMovieDbClient.getClient().getListGenres()
                .subscribeOn(Schedulers.io())
                .subscribe({ data ->
                    _responseListMovieGenre.postValue(data.genres)
//                    database.homeMovieDetailDao().insertListGenres(data.genres)
                    Observable.just(data.genres)
                        .concatMap { genres -> Observable.fromIterable(genres) }
                        .concatMapSingle { genre ->
                            genre.id?.let { TheMovieDbClient.getClient().getMoviesByGenre(it, 1) }
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ movies ->
//                            database.homeMovieDetailDao().insertListMovieDetail(movies.movieDetails)
                            map[data.genres[i++]] = movies.movieDetails
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

    @ExperimentalPagingApi
    fun fetchMovieByGenre(genreId: Int): Flowable<PagingData<MovieDetail>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                maxSize = 100,
                prefetchDistance = 5,
                initialLoadSize = 20
            ),
            remoteMediator = MovieDetailsMediator(genreId, apiService, database),
            pagingSourceFactory = { database.homeMovieDetailDao().getPagingListMovieDetailByGenre(genreId) }
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

    fun fetchFavoriteMovie(accountId: Int, sessionId: String) {
        compositeDisposable.add(
            apiService.getFavoriteMovie(accountId, sessionId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _favoriteList.postValue(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun fetchAccountDetail(sessionId: String) {
        compositeDisposable.add(
            apiService.getAccountDetail(sessionId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _accountDetails.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun updateFavoriteList(movieDetail: MovieDetail) {
        val favoriteList = _favoriteList.value
        if (movieDetail.isFavorite) {
            (favoriteList?.movieDetails as ArrayList).add(movieDetail)
        }
        else {
            (favoriteList?.movieDetails as ArrayList).remove(movieDetail)
        }
        _favoriteList.postValue(favoriteList)
    }

    fun fetchWatchList(accountId: Int, sessionId: String) {
        compositeDisposable.add(
            apiService.getWatchList(accountId, sessionId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _watchList.postValue(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun updateWatchList(movieDetail: MovieDetail) {
        val watchList = _watchList.value
        if (movieDetail.isWatchList) {
            (watchList?.movieDetails as ArrayList).add(movieDetail)
        }
        else {
            (watchList?.movieDetails as ArrayList).remove(movieDetail)
        }
        _watchList.postValue(watchList)
    }

    fun deinit() {
        compositeDisposable.clear()
    }

    fun getGenresInDatabase() {
        compositeDisposable.add(
            database.homeMovieDetailDao().getListGenres()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _responseListMovieGenre.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun getListMovieDetailByGenreInDatabase(genres: List<Genre>) {
        var i = 0
        val map = LinkedHashMap<Genre, List<MovieDetail>>()
        compositeDisposable.add(
            Observable.just(genres)
                .concatMap { Observable.fromIterable(it) }
                .concatMapSingle { genre ->
                    genre.id?.let { database.homeMovieDetailDao().getMoviesByGenre(it) }
                }
                .subscribeOn(Schedulers.io())
                .subscribe( {
                    map[genres[i++]] = it
                }, { e ->
                    e.printStackTrace()
                }, {
                    _responseMovieByGenre.postValue(map)
                })
        )
    }

}