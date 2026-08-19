package com.asiradnan.asirtasks.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Returns "Today", "Tomorrow", "Yesterday", or a formatted date.
 */
fun Long.toFormattedDate(): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@toFormattedDate }
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(calendar, today) -> "Today"
        isSameDay(calendar, tomorrow) -> "Tomorrow"
        isSameDay(calendar, yesterday) -> "Yesterday"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(this))
    }
}

/**
 * Formats time-since-midnight (ms) to a localized string (e.g., "10:30 AM").
 */
fun Long.toFormattedTime(): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MILLISECOND, this@toFormattedTime.toInt())
    }
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
}

/**
 * Extract hour of day from milliseconds since midnight.
 */
fun Long.getHourFromMillis(): Int {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.MILLISECOND, this@getHourFromMillis.toInt())
    }
    return calendar.get(Calendar.HOUR_OF_DAY)
}

/**
 * Extract minute from milliseconds since midnight.
 */
fun Long.getMinuteFromMillis(): Int {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.MILLISECOND, this@getMinuteFromMillis.toInt())
    }
    return calendar.get(Calendar.MINUTE)
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun Long.toTimeStr(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

fun Long.normalizeToMidnight(): Long {
    return Calendar.getInstance().apply {
        timeInMillis = this@normalizeToMidnight
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}