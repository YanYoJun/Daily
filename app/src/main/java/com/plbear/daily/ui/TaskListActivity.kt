package com.plbear.daily.ui

import android.content.Context
import android.content.Intent
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
import com.plbear.daily.databinding.ActivityTaskListBinding
import com.plbear.daily.databinding.DialogAddTaskBinding
import com.plbear.daily.databinding.DialogDeleteBinding
import com.plbear.daily.databinding.ViewholderTaskListBinding
import com.plbear.daily.model.DailyDB
import com.plbear.daily.model.bean.Task
import com.plbear.daily.utils.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * created by yanyongjun on 2020-03-14
 */
class TaskListActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TaskListActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityTaskListBinding
    private var mListTask = ArrayList<Task>()
    private var mAdapter = TaskListRecycleViewAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_task_list
        )
        binding.btnAddTask.setOnClickListener { showAddTaskDialog() }
        binding.recycleView.adapter = mAdapter
        binding.recycleView.layoutManager = LinearLayoutManager(this)
        refreshData()
    }

    private fun refreshData() {
        runIO {
            val list = DailyDB.instance.getTaskDao().selectAll()
            mListTask.clear()
            mListTask.addAll(list)
            logcat("task list size:" + mListTask.size)
            runUI { mAdapter.notifyDataSetChanged() }
        }
    }

    private fun showAddTaskDialog() {
        val layoutBinding = DataBindingUtil.inflate<DialogAddTaskBinding>(
            layoutInflater,
            R.layout.dialog_add_task,
            null,
            false
        )
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(layoutBinding.root)
        val dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        layoutBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        layoutBinding.btnSubmit.setOnClickListener {
            val name = layoutBinding.etName.text.toString()
            val remark = layoutBinding.etRemark.text.toString()
            if (name.isNullOrBlank()) {
                Utils.showToast("名称为空, 创建失败")
                return@setOnClickListener
            }
            runUI {
                var task = asyncIO { DailyDB.instance.getTaskDao().selectByName(name) }.await()
                if (task != null) {
                    Utils.showToast("名称已经存在, 创建失败")
                    return@runUI
                }
                task = Task().apply {
                    this.name = name
                    this.remark = remark
                    this.signTimes = 0
                    this.beginTime = Date()
                }
                asyncIO { DailyDB.instance.getTaskDao().insert(task) }.await()
                Utils.showToast("创建任务成功")
                dialog.dismiss()
                refreshData()
            }
        }
    }

    inner class TaskListRecycleViewAdapter : RecyclerView.Adapter<TaskListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_task_list, parent, false)
            return TaskListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mListTask.size
        }

        override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
            holder.setData(mListTask[position])
        }
    }

    inner class TaskListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var holderBinding = DataBindingUtil.bind<ViewholderTaskListBinding>(view)!!

        init {
            holderBinding.btnDelete.setOnClickListener {
                showDeleteDialog(holderBinding.task)
            }
            holderBinding.btnEdit.setOnClickListener {
                showEditTaskDialog(holderBinding.task)
            }
        }

        fun setData(task: Task) {
            holderBinding.task = task
        }

        private fun showEditTaskDialog(task: Task?) {
            if (task == null) return
            val layoutBinding = DataBindingUtil.inflate<DialogAddTaskBinding>(
                layoutInflater,
                R.layout.dialog_add_task,
                null,
                false
            )
            val dialogBuilder = AlertDialog.Builder(this@TaskListActivity)
                .setView(layoutBinding.root)
            layoutBinding.etName.setText(task.name)
            layoutBinding.etRemark.setText(task.remark)
            val dialog = dialogBuilder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            layoutBinding.btnCancel.setOnClickListener { dialog.dismiss() }
            layoutBinding.btnSubmit.setOnClickListener {
                val name = layoutBinding.etName.text.toString()
                val remark = layoutBinding.etRemark.text.toString()
                if (name.isNullOrBlank()) {
                    Utils.showToast("名称为空, 更新失败")
                    return@setOnClickListener
                }
                runUI {
                    task.name = name
                    task.remark = remark
                    asyncIO {
                        DailyDB.instance.getTaskDao().update(task)
                    }.await()
                    Utils.showToast("更新任务成功")
                    dialog.dismiss()
                    refreshData()
                }
            }
        }

        private fun showDeleteDialog(task: Task?) {
            if (task == null) return
            val layoutBinding = DataBindingUtil.inflate<DialogDeleteBinding>(
                layoutInflater,
                R.layout.dialog_delete,
                null,
                false
            )

            val dialogBuilder = AlertDialog.Builder(this@TaskListActivity)
                .setView(layoutBinding.root)
            layoutBinding.task = task
            val dialog = dialogBuilder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            layoutBinding.btnCancel.setOnClickListener { dialog.dismiss() }
            layoutBinding.btnSubmit.setOnClickListener {
                runIO {
                    DailyDB.instance.getTaskDao().delete(holderBinding.task)
                    refreshData()
                }
                dialog.dismiss()
            }
        }
    }
}