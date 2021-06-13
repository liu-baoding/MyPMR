package com.example.todolist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.model.OneList


class NewListAdapter(private val dataset: MutableList<OneList>) :
    RecyclerView.Adapter<NewListAdapter.ItemViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(itemView = inflater.inflate(R.layout.item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataset[position])
        holder.numView.text = (position + 1).toString()

        holder.itemView.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(position)
            }
        }
    }

    override fun getItemCount() = dataset.size

    fun addData(uneListe: OneList) {
        // add data in the list to display
        dataset.add(uneListe)
        notifyItemChanged(dataset.size)
    }

    fun addAllData(desListes: List<OneList>) {
        // add data in the list to display
        dataset.addAll(desListes)
        notifyItemChanged(dataset.size)
    }

//    fun show(lists: List<OneList>) {
//        addAllData(lists)
//        notifyDataSetChanged()
//    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.itemList)
        val numView: TextView = itemView.findViewById(R.id.number)

        fun bind(list: OneList) {
            textView.text = list.label
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
