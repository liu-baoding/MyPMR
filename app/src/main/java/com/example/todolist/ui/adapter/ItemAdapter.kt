package com.example.test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.ui.ToastUtil
import com.example.todolist.model.ItemToDo


class ItemAdapter(private val dataset: MutableList<ItemToDo>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>()  {
    val checkStatus: HashMap<Int, Boolean> = HashMap() // store the check status for all checkboxes
    val CAT: String = "TODO_ITEM"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(itemView = inflater.inflate(R.layout.item_item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind((dataset[position]))
        holder.cbView.setOnCheckedChangeListener(null)
        holder.cbView.setChecked(checkStatus.get(position) == true);
        holder.cbView.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            checkStatus.put(position, isChecked)
            if (isChecked) {
                ToastUtil.newToast(holder.cbView.context, holder.cbView.text.toString()+" has been done")
                dataset[position].isFait = true // update ItemToDo.isFait
            } else {
                ToastUtil.newToast(holder.cbView.context, holder.cbView.text.toString()+" to do")
                dataset[position].isFait = false // update ItemToDo.isFait
            }
        })
    }

    override fun getItemCount() = dataset.size

    public fun addData(item: ItemToDo) {
        // add data in the list to display
        dataset.add(item)
        checkStatus.put(dataset.size-1, item.isFait)
        notifyItemChanged(dataset.size)
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cbView = itemView.findViewById<CheckBox>(R.id.itemItem)

        fun bind(item: ItemToDo) {
            cbView.text = item.description
        }
    }

}
