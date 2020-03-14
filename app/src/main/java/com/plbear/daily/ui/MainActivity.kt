package com.plbear.daily.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plbear.daily.R
import com.plbear.daily.databinding.ActivityMainBinding
import com.plbear.daily.databinding.DialogSignBinding
import com.plbear.daily.databinding.ViewholderTodayTaskBinding
import com.plbear.daily.model.DailyDB
import com.plbear.daily.model.bean.Task
import com.plbear.daily.utils.Utils
import com.plbear.daily.utils.logcat
import com.plbear.daily.utils.runIO
import com.plbear.daily.utils.runUI
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mListTask = ArrayList<Task>()
    private var mAdapter = TaskListRecycleViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnStartTask.setOnClickListener { TaskListActivity.start(this@MainActivity) }
        binding.recycleView.adapter = mAdapter
        binding.recycleView.layoutManager = LinearLayoutManager(this)
    }

    private fun refreshData() {
        runIO {
            val list = DailyDB.instance.getTaskDao().selectAll()
            mListTask.clear()
            mListTask.addAll(list)
            mListTask.sortBy {
                it.signTimes * 24 * 60 * 60 * 1000 + it.beginTime!!.time
            }
            logcat("task list size:" + mListTask.size)
            runUI { mAdapter.notifyDataSetChanged() }
        }
    }

    override fun onStart() {
        super.onStart()
        refreshData()
    }

    inner class TaskListRecycleViewAdapter : RecyclerView.Adapter<TodayTaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayTaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_today_task, parent, false)
            return TodayTaskViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mListTask.size
        }

        override fun onBindViewHolder(holder: TodayTaskViewHolder, position: Int) {
            holder.setData(mListTask[position])
        }
    }


    inner class TodayTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var holderBinding = DataBindingUtil.bind<ViewholderTodayTaskBinding>(view)!!

        init {
            holderBinding.btnSign.setOnClickListener { sign(holderBinding.task) }
        }

        fun setData(task: Task) {
            holderBinding.task = task

            val cal = Calendar.getInstance().apply {
                this.time = task.beginTime
                this.set(Calendar.HOUR_OF_DAY, 0)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
                this.set(Calendar.MILLISECOND, 0)
            }
            cal.add(Calendar.DAY_OF_YEAR, task.signTimes - 1)
            holderBinding.tvLastSign.text = "上次打卡日期: " + Utils.dateToString(cal.time)

            val now = Calendar.getInstance().apply {
                this.set(Calendar.HOUR_OF_DAY, 0)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
                this.set(Calendar.MILLISECOND, 0)
            }

            val offset = ((now.time.time - cal.time.time) / (24 * 60 * 60 * 1000)).toInt()

            if (offset == 0) {
                holderBinding.tvSignOffset.text = "今天刚刚打过"
                holderBinding.tvSignOffset.setTextColor(Color.parseColor("#FF1C5A06"))
                holderBinding.tvLastSign.setTextColor(Color.parseColor("#FF1C5A06"))
            } else if (offset > 0) {
                holderBinding.tvSignOffset.text = "还欠" + offset + "天"
                holderBinding.tvSignOffset.setTextColor(Color.parseColor("#FF850931"))
                holderBinding.tvLastSign.setTextColor(Color.parseColor("#FF850931"))
            } else {
                holderBinding.tvSignOffset.text = "已经超前" + (-offset) + "天"
                holderBinding.tvSignOffset.setTextColor(Color.parseColor("#FF2196F3"))
                holderBinding.tvLastSign.setTextColor(Color.parseColor("#FF2196F3"))
            }
        }

        private fun sign(task: Task?) {
            if (task == null) return
            val layoutBinding = DataBindingUtil.inflate<DialogSignBinding>(
                layoutInflater,
                R.layout.dialog_sign,
                null,
                false
            )

            val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                .setView(layoutBinding.root)
            layoutBinding.task = task
            var builder = StringBuilder()
            builder.append("名称: ")
            builder.append(task.name)
            builder.append("\n")
            val cal = Calendar.getInstance().apply {
                this.time = task.beginTime
                this.set(Calendar.HOUR_OF_DAY, 0)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
                this.set(Calendar.MILLISECOND, 0)
            }
            cal.add(Calendar.DAY_OF_YEAR, task.signTimes)
            builder.append("打卡日期: ")
            builder.append(Utils.dateToString(cal.time))
            builder.append("\n")

            val now = Calendar.getInstance().apply {
                this.set(Calendar.HOUR_OF_DAY, 0)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
                this.set(Calendar.MILLISECOND, 0)
            }

            val offset = ((now.time.time - cal.time.time) / (24 * 60 * 60 * 1000)).toInt()

            if (offset == 0) {
                builder.append("打卡日期\n")
            } else if (offset > 0) {
                builder.append("将欠" + offset + "天\n")
            } else {
                builder.append("将超前" + (-offset) + "天\n")
            }
            layoutBinding.tvNotes.text = builder.toString()

            val dialog = dialogBuilder.create()
            dialog.show()
            layoutBinding.btnCancel.setOnClickListener { dialog.dismiss() }
            layoutBinding.btnSubmit.setOnClickListener {
                runIO {
                    task.signTimes++
                    DailyDB.instance.getTaskDao().update(task)
                    refreshData()
                }
                dialog.dismiss()
            }
        }
    }

}
