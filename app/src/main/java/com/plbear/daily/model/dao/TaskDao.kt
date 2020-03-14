package com.plbear.daily.model.dao

import androidx.room.*
import com.plbear.daily.model.bean.Task

/**
 * created by yanyongjun on 2020-03-14
 */
@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task)

    @Query("select * from task where name =:name")
    fun selectByName(name: String): Task?

    @Query("select * from task order by id")
    fun selectAll(): List<Task>

    @Delete
    fun delete(task: Task?)

    @Update
    fun update(task:Task)
}