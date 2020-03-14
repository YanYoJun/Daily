package com.plbear.daily.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plbear.daily.model.base.App
import com.plbear.daily.model.bean.Task
import com.plbear.daily.model.bean.TaskRecord
import com.plbear.daily.model.dao.TaskDao
import com.plbear.daily.model.dao.TaskRecordDao
import kotlinx.coroutines.Deferred

/**
 * created by yanyongjun on 2020-03-14
 */
@Database(entities = [Task::class, TaskRecord::class], version = 1, exportSchema = false)
@TypeConverters(DateCoverter::class)
abstract class DailyDB : RoomDatabase() {
    companion object {
        private const val DB_NAME = "daily.db"

        val instance by lazy {
            Room.databaseBuilder(App.instance(), DailyDB::class.java, DB_NAME).build()
        }
    }

    abstract fun getTaskDao(): TaskDao

    abstract fun getRecordDao(): TaskRecordDao
}