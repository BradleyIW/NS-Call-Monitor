package io.github.bradleyiw.ns.common

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DatabaseConverters {

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let {
            LocalDateTime.parse(it)
        }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? =
        date?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
