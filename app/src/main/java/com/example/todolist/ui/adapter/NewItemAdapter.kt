package com.example.todolist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.model.OneItem


class NewItemAdapter(private val dataset: MutableList<OneItem>) :
    RecyclerView.Adapter<NewItemAdapter.ItemViewHolder>() {
    val checkStatus: HashMap<Int, Boolean> = HashMap() // store the check status for all checkboxes
    val CAT: String = "TODO_ITEM"
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(itemView = inflater.inflate(R.layout.item_item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataset[position])
        holder.cbView.setOnCheckedChangeListener(null)
        holder.cbView.isChecked = checkStatus[position] == true

        holder.cbView.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            checkStatus[position] = isChecked
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener!!.onCheckedChange(position, isChecked)
            }
        })
    }

    override fun getItemCount() = dataset.size

    fun addData(item: OneItem) {
        // add data in the list to display
        dataset.add(item)
        checkStatus[dataset.size - 1] = item.checkedStr == "1"
        notifyItemChanged(dataset.size)
    }

    fun addAllData(items: List<OneItem>) {
        for (item in items) {
            addData(item)
        }
    }

    fun getDataSet(): MutableList<OneItem> {
        return dataset
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        mOnCheckedChangeListener = listener
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbView: CheckBox = itemView.findViewById(R.id.itemItem)

        fun bind(item: OneItem) {
            cbView.text = item.label
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(position: Int, isChecked: Boolean)
    }

}
