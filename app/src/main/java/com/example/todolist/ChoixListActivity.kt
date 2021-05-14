package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.adapter.ListAdapter
import com.example.todolist.model.MyList
import kotlinx.android.synthetic.main.activity_choix_list.*


class ChoixListActivity : AppCompatActivity(){
    val CAT: String = "TODO_LIST"
    val context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)
        var pseudo: String? = intent.getStringExtra("pseudo")

        val recyclerView = findViewById<RecyclerView>(R.id.reViewList)
        val lists: MutableList<MyList> = mutableListOf()

        repeat(5){
            lists.add(MyList("new list ${it + 1}"))
        }

        val adapter = ListAdapter(lists)


        val intent = Intent(this,ShowListActivity::class.java)
        intent.putExtra("pseudo", pseudo)

        adapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val listName = lists[position].listTextStr
                ToastUtil.newToast(context,"this is $listName")
                intent.putExtra("list", listName)
                startActivity(intent)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        this.title="$pseudo's Todo-lists"

//        liste1.setOnClickListener {
//            alerter("this is ${liste1.text.toString()}")
//            Intent(this, ShowListActivity::class.java).apply {
//                putExtra("list", liste1.text.toString())
//                putExtra("pseudo", pseudo)
//                startActivity(this)
//            }
//        }
//
//        liste2.setOnClickListener {
//            alerter("this is ${liste2.text.toString()}")
//            Intent(this, ShowListActivity::class.java).apply {
//                putExtra("list", liste2.text.toString())
//                putExtra("pseudo", pseudo)
//                startActivity(this)
//            }
//        }
//
//        liste3.setOnClickListener {
//            alerter("this is ${liste3.text.toString()}")
//            Intent(this, ShowListActivity::class.java).apply {
//                putExtra("list", liste3.text.toString())
//                putExtra("pseudo", pseudo)
//                startActivity(this)
//            }
//        }

        etNewList.setOnClickListener {
            ToastUtil.newToast(context,"Add a Todo list")
        }

        btnOKList.setOnClickListener {
            var newListName = etNewList.text.toString()
            if (newListName==null || newListName==""){
                ToastUtil.newToast(context,"Please enter the name of list")
            }else {
                ToastUtil.newToast(context,"Add \"$newListName\"")
                adapter.addData(newListName)
                etNewList.setText("") //clear the input area
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

//    private fun alerter(s: String) {
//        Log.i(CAT, s)
//        var t = Toast.makeText(this, s, Toast.LENGTH_SHORT)
//        t.show()
//    }

//    override fun onClick(v: View?) {
//        when(v?.id){
//            R.id.liste1->{
//                alerter("this is ${liste1.text.toString()}")
//
//            }
//        }
//    }

}
