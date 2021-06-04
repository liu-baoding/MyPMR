package com.example.todolist.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.adapter.ItemAdapter
import com.example.todolist.R
import com.example.todolist.model.ItemToDo
import com.example.todolist.model.ListeToDo
import com.example.todolist.model.ProfilListeToDo
import kotlinx.android.synthetic.main.activity_show_list.*


class ShowListActivity : AppCompatActivity(){
    val CAT: String = "TODO_ITEM"
    val context = this
    lateinit var profil: ProfilListeToDo
    var whichList: Int = 0
    lateinit var listTodo: ListeToDo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)
        this.profil = intent.getSerializableExtra("profil") as ProfilListeToDo
        whichList = intent.getIntExtra("whichList",0)
        listTodo = profil.mesListeToDo[whichList]
        var list_name = listTodo.titreListeToDo

        val recyclerView = findViewById<RecyclerView>(R.id.reViewItem)
        val items: MutableList<ItemToDo> = mutableListOf()

        this.title = "Items of \"$list_name\" Todo-list"

        // to test the function
//        repeat(5){
//            items.add(ItemToDo("new item ${it+1}"))
//        }


        val adapter = ItemAdapter(items)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        // load existed ListeToDo
        if(listTodo.lesItems!=null) {
            for (item: ItemToDo in listTodo.lesItems) {
                adapter.addData(item)
            }
        }

        etNewItem.setOnClickListener {
            ToastUtil.newToast(context, "Add a Todo item")

        }

        btnOKItem.setOnClickListener {
            Log.i(CAT, "map: "+adapter.checkStatus.toString())
            var newItemName = etNewItem.text.toString()
            if (newItemName==null || newItemName==""){
                ToastUtil.newToast(context, "Please enter the name of item")
            }else {
                ToastUtil.newToast(context, "Add \"$newItemName\"")

                // add new item
                var newItem : ItemToDo = ItemToDo(newItemName)
                adapter.addData(newItem) // add new item

                listTodo.ajouteItem(newItem)

                etNewItem.setText("") // clear the input area
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

    // pass data to ChoixListActivity when press "back"
    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("profil", profil)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

}
