package com.hyvu.themoviedb.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    const val DAY_1: Long = 24 * 60 * 60 * 1000L

    fun parseDateTimeToUnixTimestampLegacy(dateTimeString: String): Long? {
        // IMPORTANT: SimpleDateFormat is NOT thread-safe. Create a new instance or use ThreadLocal.
        // For this example, we'll create a new instance inside the function.
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US)
        // Explicitly set the time zone to UTC, as indicated by 'UTC' in the string
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            val date = formatter.parse(dateTimeString)
            date?.time // .time returns milliseconds since epoch
        } catch (e: ParseException) {
            println("Error parsing date-time string: ${e.message}")
            null
        } catch (e: Exception) {
            println("An unexpected error occurred: ${e.message}")
            null
        }
    }
}