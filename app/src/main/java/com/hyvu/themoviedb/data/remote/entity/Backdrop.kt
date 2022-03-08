package com.hyvu.themoviedb.data.remote.entity


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Backdrop(
    @SerializedName("aspect_ratio")
    val aspectRatio: Double,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("iso_639_1")
    val iso6391: Any,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("width")
    val width: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt(),
        "iso6391",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(aspectRatio)
        parcel.writeString(filePath)
        parcel.writeInt(height)
        parcel.writeDouble(voteAverage)
        parcel.writeInt(voteCount)
        parcel.writeInt(width)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Backdrop> {
        override fun createFromParcel(parcel: Parcel): Backdrop {
            return Backdrop(parcel)
        }

        override fun newArray(size: Int): Array<Backdrop?> {
            return arrayOfNulls(size)
        }
    }
}