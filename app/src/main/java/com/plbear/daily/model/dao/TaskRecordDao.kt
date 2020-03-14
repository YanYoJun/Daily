package com.plbear.daily.model.dao

import androidx.room.Dao
import androidx.room.Insert
import com.plbear.daily.model.bean.TaskRecord

/**
 * created by yanyongjun on 2020-03-14
 */
@Dao
interface TaskRecordDao {
    @Insert
    fun insert(taskRecord: TaskRecord)
}