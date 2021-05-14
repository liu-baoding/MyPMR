package com.example.test.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.MyList


class ListAdapter(private val dataset: MutableList<MyList>): RecyclerView.Adapter<ListAdapter.ItemViewHolder>()  {

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(itemView = inflater.inflate(R.layout.item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind((dataset[position]))
        holder.numView.text = (position+1).toString()

        holder.itemView.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(position)
            }
        }
    }

    override fun getItemCount() = dataset.size

    public fun addData(text: String) {
        // add data in the list to display
        dataset.add(MyList(text))
        notifyItemChanged(dataset.size)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView = itemView.findViewById<TextView>(R.id.itemList)
        val numView = itemView.findViewById<TextView>(R.id.number)

        fun bind(list: MyList) {
            textView.text = list.listTextStr
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
