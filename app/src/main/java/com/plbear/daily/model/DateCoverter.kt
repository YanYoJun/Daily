package com.plbear.daily.model

import androidx.room.TypeConverter
import java.util.*

/**
 * created by yanyongjun on 2020-03-14
 */
class DateCoverter {
    @TypeConverter
    fun revertDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun coverDate(date: Date): Long {
        return date.time
    }

}