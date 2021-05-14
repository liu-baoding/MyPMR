package com.example.test.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.ToastUtil
import com.example.todolist.model.MyItem


class ItemAdapter(private val dataset: MutableList<MyItem>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>()  {
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
            } else {
                ToastUtil.newToast(holder.cbView.context, holder.cbView.text.toString()+" to do")
            }
        })
    }

    override fun getItemCount() = dataset.size

    public fun addData(text: String) {
        // add data in the list to display
        dataset.add(MyItem(text))
        checkStatus.put(dataset.size, false)
        notifyItemChanged(dataset.size)
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cbView = itemView.findViewById<CheckBox>(R.id.itemItem)

        fun bind(item: MyItem) {
            cbView.text = item.itemTextStr
        }
    }

//    private fun alerter(context: Context, s: String) {
//        Log.i(CAT, s)
//        var t = Toast.makeText(context, s, Toast.LENGTH_SHORT)
//        t.show()
//    }

}
