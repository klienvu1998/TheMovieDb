package com.hyvu.themoviedb.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataConverter {

    @TypeConverter
    fun listIntToJson(value: List<Int>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): List<Int> = Gson().fromJson<List<Int>>(value, object : TypeToken<List<Int>>() {}.type)

}