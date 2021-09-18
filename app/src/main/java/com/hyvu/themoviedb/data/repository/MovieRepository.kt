package com.hyvu.themoviedb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.repository.datasource.MoviePagingSource
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.datasource.CommentPagingSource
import com.hyvu.themoviedb.data.repository.datasource.PopularMoviePagingSource
import com.hyvu.themoviedb.database.HomeMovieDetailDao
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.utils.Constraints
import com.hyvu.themoviedb.view.MoviesHomeFragment
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.collections.ArrayList

@ActivityScope
class MovieRepository @Inject constructor(val apiService: TheMovieDbAPI, private val homeMovieDetailDao: HomeMovieDetailDao) {
    private val compositeDisposable = CompositeDisposable()

    private val _responseMovieVideos = MutableLiveData<MovieVideos>()
    val responseMovieVideos: LiveData<MovieVideos> = _responseMovieVideos

    private val _responseCurrentMovieDetail = MutableLiveData<MovieFullDetails>()
    val responseCurrentMovieDetail: LiveData<MovieFullDetails> = _responseCurrentMovieDetail

    private val _responseCurrentMovieDetailForTikMovie = MutableLiveData<TikMovie>()
    val responseCurrentMovieDetailForTikMovie: LiveData<TikMovie> = _responseCurrentMovieDetailForTikMovie

    private val _responseListMovieGenre: MutableLiveData<Genres> = MutableLiveData()
    val responseListMovieGenre: LiveData<Genres> = _responseListMovieGenre

    private val _responseMovieByGenre: MutableLiveData<Map<Genre, List<MovieDetail>>> = MutableLiveData()
    val responseMovieByGenre: LiveData<Map<Genre, List<MovieDetail>>> = _responseMovieByGenre

    private val _responseTrendingMovies: MutableLiveData<TrendingMovies> = MutableLiveData()
    val responseTrendingMovies: LiveData<TrendingMovies> = _responseTrendingMovies

    private val _responseLatestMovieDetail: MutableLiveData<MovieDetail> = MutableLiveData()
    val responseLatestMovieDetail: LiveData<MovieDetail> = _responseLatestMovieDetail

    private val _responseMovieCredits: MutableLiveData<Credits> = MutableLiveData()
    val responseMovieCredits: LiveData<Credits> = _responseMovieCredits

    private val _favoriteList: MutableLiveData<MoviesListResponse> = MutableLiveData()
    val favoriteList: LiveData<MoviesListResponse> = _favoriteList

    private val _accountDetails: MutableLiveData<AccountDetails> = MutableLiveData()
    val accountDetails: LiveData<AccountDetails> = _accountDetails

    fun insertMovieDetailToDatabase(movieDetail: MovieDetail) {
        compositeDisposable.add(
            Completable.fromAction { homeMovieDetailDao.insertMovieDetailToDatabase(movieDetail) }
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    fun insertGenreToDatabase(genre: Genre) {
        compositeDisposable.add(
            Completable.fromAction { homeMovieDetailDao.insertGenreToDatabase(genre) }
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    fun queryMovie() {
        val map = LinkedHashMap<Genre, List<MovieDetail>>()
        map[Genre(-1, MoviesHomeFragment.TRENDING_MOVIE)] = ArrayList()
        compositeDisposable.add(
            homeMovieDetailDao.getListGenres()
                .subscribeOn(Schedulers.io())
                .subscribe({ genres ->
                    homeMovieDetailDao.getListMovieDetail()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ listMovieDetail ->
                            genres.forEach { genre ->
                                listMovieDetail.forEach {
                                    (map[genre] as ArrayList).add(it)
                                }
                                _responseMovieByGenre.postValue(map)
                            }
                        }, { e ->
                            e.printStackTrace()
                        })
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

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
        val map = LinkedHashMap<Genre, List<MovieDetail>>()
        compositeDisposable.add(
            TheMovieDbClient.getClient().getListGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    _responseListMovieGenre.postValue(data)
                    Observable.just(data.genres)
                        .concatMap { genres -> Observable.fromIterable(genres) }
                        .concatMapSingle { genre ->
                            genre.id?.let { TheMovieDbClient.getClient().getMoviesByGenre(it, 1) }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ movies ->
                            map[data.genres[i++]] = movies.movieDetails
                            _responseMovieByGenre.postValue(map)
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

    fun fetchFavoriteMovie(accountId: Int, sessionId: String) {
        compositeDisposable.add(
            apiService.getFavoriteMovie(accountId, sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(AndroidSchedulers.mainThread())
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

    fun deinit() {
        compositeDisposable.clear()
    }

}