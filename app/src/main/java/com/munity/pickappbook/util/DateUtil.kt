package com.munity.pickappbook.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtil {
    private fun iso8601ToInstant(iso8601Date: String): Instant {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
        return Instant.from(formatter.parse(iso8601Date))
    }

    fun isIso8601Expired(iso8601Date: String): Boolean {
        // Parse the ISO 8601 string to an Instant
        val parsedTime = iso8601ToInstant(iso8601Date)
        val currentTime = Instant.now()

        return currentTime > parsedTime
    }

    fun iso8601ToTimeAgo(iso8601Date: String): String {
        // Parse the ISO 8601 string to an Instant
        val parsedTime = iso8601ToInstant(iso8601Date)
        val currentTime = Instant.now()

        // Calculate the difference between the current time and the parsed time
        val duration = Duration.between(parsedTime, currentTime)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} min ago"
            duration.toHours() < 24 -> "${duration.toHours()} hour ago"
            duration.toDays() < 30 -> "${duration.toDays()} days ago"
            duration.toDays() < 365 -> "${duration.toDays() / 30} months ago"
            else -> "${duration.toDays() / 365} years ago"
        }
    }
}