package com.shoplist.app.data.local

import androidx.room.TypeConverter
import com.shoplist.app.domain.model.Priority
import com.shoplist.app.domain.model.RecurrenceInterval

class Converters {
    @TypeConverter
    fun fromInterval(value: RecurrenceInterval?): String? = value?.name

    @TypeConverter
    fun toInterval(value: String?): RecurrenceInterval? = value?.let { RecurrenceInterval.valueOf(it) }

    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
}
