package com.plbear.daily.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * created by yanyongjun on 2020-03-14
 */
@Entity
class Task {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var remark: String = ""
    var beginTime: Date? = null
    var signTimes: Int = 0
}