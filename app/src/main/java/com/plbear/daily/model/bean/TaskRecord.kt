package com.plbear.daily.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * created by yanyongjun on 2020-03-14
 */
@Entity
class TaskRecord {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var taskId: Int = 0
    var remark: String = ""
    var signTime: Date? = null
}