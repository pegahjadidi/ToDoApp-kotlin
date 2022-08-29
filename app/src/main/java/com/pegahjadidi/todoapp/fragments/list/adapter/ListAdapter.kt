package com.pegahjadidi.todoapp.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pegahjadidi.todoapp.R
import com.pegahjadidi.todoapp.data.model.ToDoData
import com.pegahjadidi.todoapp.data.viewModel.ToDoViewModel
import com.pegahjadidi.todoapp.databinding.RowLayoutBinding

class ListAdapter(private val viewModel: ToDoViewModel) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    var todoDataLists : List<ToDoData> = emptyList<ToDoData>()

    class ListViewHolder(private val binding : RowLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind (toDoData: ToDoData,viewModel : ToDoViewModel) {
            binding.toDoData = toDoData
            binding.viewModel =  viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListViewHolder {
                val view: RowLayoutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_layout,
                    parent,
                    false)
                return ListViewHolder(view)
            }
        }




    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = todoDataLists[position]
        holder.bind(currentItem,viewModel )



    }

    override fun getItemCount(): Int {
        return todoDataLists.size
    }

    fun setData(toDoData: List<ToDoData>) {
        val oldList = todoDataLists
        val newList = toDoData
        val toDoDiffUtil = ToDoDiffUtil(oldList,newList)
        val toDoDiffUtilResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.todoDataLists = toDoData
        toDoDiffUtilResult.dispatchUpdatesTo(this)
    }



}