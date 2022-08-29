package com.pegahjadidi.todoapp.fragments.list.adapter

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pegahjadidi.todoapp.R
import com.pegahjadidi.todoapp.data.model.Priority
import com.pegahjadidi.todoapp.data.model.ToDoData
import com.pegahjadidi.todoapp.data.viewModel.ToDoViewModel
import com.pegahjadidi.todoapp.fragments.list.ListFragmentDirections
import java.text.DateFormat

class BindingAdapter {

    companion object {
        @androidx.databinding.BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, navigate:Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    view.findNavController().navigate(R.id.action_listFragment_to_addFragment)
                }
            }
        }

        @androidx.databinding.BindingAdapter("android:emptyDataBase")
        @JvmStatic
        fun emptyDataBase(view: View, emptyDataBase: MutableLiveData<Boolean>) {
            when (emptyDataBase.value) {
                true -> view.visibility = View.VISIBLE
                false -> view.visibility =View.INVISIBLE
                else -> {}
            }
        }

        @androidx.databinding.BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: CardView, priority: Priority){
            when(priority) {
                Priority.HIGH -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.red))

                Priority.MEDIUM -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.yellow))

                Priority.LOW -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.green))
            }


        }
        @androidx.databinding.BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(view : ConstraintLayout, currentItem : ToDoData) {
            view.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                view.findNavController().navigate(action)
            }
        }

        @androidx.databinding.BindingAdapter("android:setTimeStamp")
        @JvmStatic
        fun setTimeStamp(view : TextView, timeStamp : Long) {
            view.text = DateFormat.getInstance().format(timeStamp)
        }

        @androidx.databinding.BindingAdapter(value = ["todo", "vm"])
        @JvmStatic
        fun isChecking(checkBox: CheckBox, todo: ToDoData, viewModel: ToDoViewModel) {
            checkBox.isChecked = todo.completed

            checkBox.setOnCheckedChangeListener { _, b ->
                if (b) {
                    val checkedToDo = ToDoData( todo.id,
                        todo.title,
                        todo.priority,
                        todo.description,
                        System.currentTimeMillis(),true)
                    viewModel.updateData(checkedToDo)
                } else {
                    val unCheckedToDo = ToDoData (
                        todo.id,
                        todo.title,
                        todo.priority,
                        todo.description,
                        System.currentTimeMillis(),
                        false
                    )
                    viewModel.updateData(unCheckedToDo)
                }
            }
        }
        @BindingAdapter("android:strikeThrough")
        @JvmStatic
        fun striked(textView: TextView, isCheck: Boolean) {
            if (isCheck) {
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textView.paintFlags =
                    textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }



    }

}