package com.hyvu.themoviedb.data.entity


import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.hyvu.themoviedb.database.DataConverter

@TypeConverters(DataConverter::class)
@Entity(tableName = "movie_detail")
data class MovieDetail(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("unique_id")
    val uniqueId: Long = 0,

    @ColumnInfo(name = "adult")
    @SerializedName("adult")
    val adult: Boolean,

    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @ColumnInfo(name = "genre_ids")
    @SerializedName("genre_ids")
    val genreIds: List<Int>,

//    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    @SerializedName("id")
    val movieId: Int,

    @ColumnInfo(name = "original_language")
    @SerializedName("original_language")
    val originalLanguage: String,

    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    val originalTitle: String,

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    val overview: String,

    @ColumnInfo(name = "popularity")
    @SerializedName("popularity")
    val popularity: Double,

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    val posterPath: String?,

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    val releaseDate: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "video")
    @SerializedName("video")
    val video: Boolean,

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    val voteAverage: Double,

    @ColumnInfo(name = "vote_count")
    @SerializedName("vote_count")
    val voteCount: Int,

    var isFavorite: Boolean = false,
    var isWatchList: Boolean = false
) : Parcelable {
    @Suppress("UNREACHABLE_CODE")
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            TODO("genreIds"),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readByte() != 0.toByte(),
            parcel.readDouble(),
            parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()) {
    }

    fun getBackdropImage(): String? {
        return if (this.backdropPath.isNullOrEmpty()) {
            if (this.posterPath.isNullOrEmpty()) {
                this.posterPath
            } else null
        } else this.backdropPath
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(uniqueId)
        parcel.writeByte(if (adult) 1 else 0)
        parcel.writeString(backdropPath)
        parcel.writeInt(movieId)
        parcel.writeString(originalLanguage)
        parcel.writeString(originalTitle)
        parcel.writeString(overview)
        parcel.writeDouble(popularity)
        parcel.writeString(posterPath)
        parcel.writeString(releaseDate)
        parcel.writeString(title)
        parcel.writeByte(if (video) 1 else 0)
        parcel.writeDouble(voteAverage)
        parcel.writeInt(voteCount)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isWatchList) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieDetail> {
        override fun createFromParcel(parcel: Parcel): MovieDetail {
            return MovieDetail(parcel)
        }

        override fun newArray(size: Int): Array<MovieDetail?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity(tableName = "remote_keys")
data class MovieDetailRemoteKey (
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    val movieId: Int,
    @ColumnInfo(name = "prev_key")
    val prevKey: Int?,
    @ColumnInfo(name = "next_key")
    val nextKey: Int?
)