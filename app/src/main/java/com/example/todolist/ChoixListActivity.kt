package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_choix_list.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class ChoixListActivity : AppCompatActivity(){
    val CAT: String = "TODO_LIST"
    var last_list: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        last_list = liste3.id

        var pseudo: String? = intent.getStringExtra("pseudo")

        this.title="$pseudo's Todo-lists"

        liste1.setOnClickListener {
            alerter("this is ${liste1.text.toString()}")
            Intent(this, ShowListActivity::class.java).apply {
                putExtra("list", liste1.text.toString())
                putExtra("pseudo", pseudo)
                startActivity(this)
            }
        }

        liste2.setOnClickListener {
            alerter("this is ${liste2.text.toString()}")
            Intent(this, ShowListActivity::class.java).apply {
                putExtra("list", liste2.text.toString())
                putExtra("pseudo", pseudo)
                startActivity(this)
            }
        }

        liste3.setOnClickListener {
            alerter("this is ${liste3.text.toString()}")
            Intent(this, ShowListActivity::class.java).apply {
                putExtra("list", liste3.text.toString())
                putExtra("pseudo", pseudo)
                startActivity(this)
            }
        }

        et_new_list.setOnClickListener {
            alerter("Add a Todo list")
            // TODO:
        }

        btnOK_list.setOnClickListener {
            var new_list_name = et_new_list.text.toString()
            if (new_list_name==null || new_list_name==""){
                alerter("Please enter the name of list")
            }else {
                alerter("Add a new list")
                // add a new TextView
                val layout = findViewById(R.id.choix_list) as ConstraintLayout
//                val params = liste1.getLayoutParams() as ConstraintLayout.LayoutParams

                var new_list = newList(this, new_list_name)

                var newParams = new_list.get_params()

//                val newParams = ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                    ConstraintLayout.LayoutParams.WRAP_CONTENT
//                )

                layout.addView(new_list, -1, newParams)
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

//    override fun onClick(v: View?) {
//        when(v?.id){
//            R.id.liste1->{
//                alerter("this is ${liste1.text.toString()}")
//
//            }
//        }
//    }

}
