package com.hyvu.themoviedb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.hyvu.themoviedb.data.repository.datasource.MoviePagingSource
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.datasource.CommentPagingSource
import com.hyvu.themoviedb.data.repository.datasource.PopularMoviePagingSource
import com.hyvu.themoviedb.utils.Constraints
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object MovieRepository {
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

    fun fetchMovieCredits(movieId: Int) {
        compositeDisposable.add(
            TheMovieDbClient.getClient().getCredits(movieId)
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
                TheMovieDbClient.getClient().getTrendingMovies()
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
            pagingSourceFactory = { MoviePagingSource(TheMovieDbClient.getClient(), genreId) }
        ).flowable
    }

    fun fetchPopularMovies(): Flowable<PagingData<MovieDetail>> {
        return Pager(
                config = PagingConfig(
                        maxSize =  Constraints.MAX_ITEM_PER_SCROLL,
                        pageSize = Constraints.NETWORK_PAGE_SIZE,
                        enablePlaceholders = false
                ),
                pagingSourceFactory = { PopularMoviePagingSource(TheMovieDbClient.getClient()) }
        ).flowable
    }

    fun fetchMovieComments(movieId: Int): Flowable<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                maxSize = Constraints.MAX_ITEM_PER_SCROLL,
                pageSize = Constraints.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommentPagingSource(TheMovieDbClient.getClient(), movieId) }
        ).flowable
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

    fun fetchLatestMovie() {
        compositeDisposable.add(
                TheMovieDbClient.getClient().getLatestMovie()
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