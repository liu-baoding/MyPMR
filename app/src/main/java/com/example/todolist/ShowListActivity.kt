package com.example.todolist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choix_list.*
import kotlinx.android.synthetic.main.activity_show_list.*

class ShowListActivity : AppCompatActivity(){
    val CAT: String = "TODO_ITEM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

//        var pseudo: String? = intent.getStringExtra("pseudo")
        var list_name: String? = intent.getStringExtra("list")

        this.title = "Items of \"$list_name\" Todo-list"

        cbItem1.setOnClickListener {
            if (cbItem1.isChecked){
                alerter("${cbItem1.text.toString()} has been done")
            }else{
                alerter("${cbItem1.text.toString()} to do")
            }
        }

        cbItem2.setOnClickListener {
            if (cbItem2.isChecked){
                alerter("${cbItem2.text.toString()} has been done")
            }else{
                alerter("${cbItem2.text.toString()} to do")
            }
        }

        cbItem3.setOnClickListener {
            if (cbItem3.isChecked){
                alerter("${cbItem3.text.toString()} has been done")
            }else{
                alerter("${cbItem3.text.toString()} to do")
            }
        }

        et_new_item.setOnClickListener {
            alerter("Add a Todo item")
            // TODO:
        }

        btnOK_item.setOnClickListener {
            alerter("click on btnOK")
            // TODO:
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
