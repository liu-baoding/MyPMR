package com.example.todolist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.adapter.ItemAdapter
import com.example.test.adapter.ListAdapter
import com.example.todolist.model.MyItem
import kotlinx.android.synthetic.main.activity_show_list.*

class ShowListActivity : AppCompatActivity(){
    val CAT: String = "TODO_ITEM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)
        var list_name: String? = intent.getStringExtra("list")

        val recyclerView = findViewById<RecyclerView>(R.id.reViewItem)
        val items: MutableList<MyItem> = mutableListOf()

        repeat(5){
            items.add(MyItem("new${it+1}"))
        }


        val adapter = ItemAdapter(items)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        this.title = "Items of \"$list_name\" Todo-list"

        etNewItem.setOnClickListener {
            alerter("Add a Todo item")
        }

        btnOKItem.setOnClickListener {
            Log.i(CAT, "map: "+adapter.checkStatus.toString())
            var newItemName = etNewItem.text.toString()
            if (newItemName==null || newItemName==""){
                alerter("Please enter the name of item")
            }else {
                alerter("Add \"$newItemName\"")
                adapter.addData(newItemName)
                etNewItem.setText("") //clear the input area
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
    }

    private fun alerter(s: String) {
        Log.i(CAT, s)
        var t = Toast.makeText(this, s, Toast.LENGTH_SHORT)
        t.show()
    }

}
