package com.plbear.daily.utils

import android.widget.Toast
import com.plbear.daily.model.base.App
import java.text.SimpleDateFormat
import java.util.*

/**
 * created by yanyongjun on 2020-03-14
 */
object Utils {
    fun showToast(text: String) {
        Toast.makeText(App.instance(), text, Toast.LENGTH_SHORT).show()
    }

    private val simpleFormat = SimpleDateFormat("yyyy-MM-dd")

    fun dateToString(date: Date?): String {
        if (date == null) return ""
        return simpleFormat.format(date)
    }
}