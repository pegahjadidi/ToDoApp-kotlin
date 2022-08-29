package com.pegahjadidi.todoapp.data.viewModel

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pegahjadidi.todoapp.R
import com.pegahjadidi.todoapp.data.model.Priority
import com.pegahjadidi.todoapp.data.model.ToDoData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val emptyDataBase : MutableLiveData<Boolean> = MutableLiveData(false)
    fun checkIfDataBaseEmpty(toDoDataList : List<ToDoData>) {
        emptyDataBase.value = toDoDataList.isEmpty()
    }

    //change spinner color
    val listener : AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long) {
            when (position) {
                0 -> {(parent?.getChildAt(0) as TextView)
                    .setTextColor(
                        ContextCompat.getColor(application,
                        R.color.red))}
                1 -> {(parent?.getChildAt(0) as TextView)
                    .setTextColor(
                        ContextCompat.getColor(application,
                        R.color.yellow))}
                2 -> {(parent?.getChildAt(0) as TextView)
                    .setTextColor(
                        ContextCompat.getColor(application,
                        R.color.green))}

            }

        }
    }

    //this function checks if the Title or description is empty
    fun verifyDataFromUser(Title : String ,Description : String):Boolean {
        return if (TextUtils.isEmpty(Title) || TextUtils.isEmpty(Description)) {
            false
        }else !(TextUtils.isEmpty(Title) || TextUtils.isEmpty(Description))
    }

    //the type of mPriority is string , but we need Priority type to insert data to DB
    fun parsePriority(priority: String) : Priority {
        return when (priority) {
            "High Priority" -> {
                Priority.HIGH}
            "Medium Priority" -> {
                Priority.MEDIUM}
            "Low Priority" -> {
                Priority.LOW}
            else -> Priority.LOW
        }
    }

    //this function pars priority to int for update fragment using safeargs
    fun parsePriorityToInt(priority: Priority) : Int {
        return when(priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }
}