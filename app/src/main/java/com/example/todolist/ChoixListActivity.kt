package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.adapter.ListAdapter
import com.example.todolist.model.ListeToDo
import com.example.todolist.model.ProfilListeToDo
import kotlinx.android.synthetic.main.activity_choix_list.*


class ChoixListActivity : AppCompatActivity(){
    val CAT: String = "TODO_LIST"
    val context = this
    lateinit var profil: ProfilListeToDo
    lateinit var pseudo: String
    val requestCode = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)
//        var remember: Boolean = intent.getBooleanExtra("remember",true)
        profil = intent.getSerializableExtra("profil") as ProfilListeToDo
        pseudo = profil.login
        Log.i(CAT,"create")
    }

    private fun showContent(){

        Log.i(CAT,"test")

        val recyclerView = findViewById<RecyclerView>(R.id.reViewList)

        val lists: MutableList<ListeToDo> = mutableListOf()

        // load existed ListeToDo
        if(profil.mesListeToDo!=null) {
            for (liste: ListeToDo in profil.mesListeToDo) {
                lists.add(liste)
            }
        }

        val adapter = ListAdapter(lists)

        // start ShowListActivity
        val intent = Intent(this,ShowListActivity::class.java)
        intent.putExtra("pseudo", pseudo)

        // action when click on the list
        adapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val listName = lists[position].titreListeToDo
                ToastUtil.newToast(context,"this is $listName")
                // pass the clicked list
                intent.putExtra("profil", profil)
                intent.putExtra("whichList",position)
                startActivityForResult(intent,requestCode)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        this.title="$pseudo's Todo-lists" // title of this page

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

                // add new list
                var newList : ListeToDo = ListeToDo(newListName)
                profil.ajouteListe(newList)

                etNewList.setText("") //clear the input area
            }

        }
    }

    override fun onStart() {
        super.onStart()
        showContent()
        Log.i(CAT,"start")

    }

    override fun onResume() {
        super.onResume()
        Log.i(CAT,"resume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(CAT,"Restart")
    }

    // receive modified profil from ShowListActivity after press "back"
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCode -> if (resultCode === RESULT_OK) {
                val returnData: ProfilListeToDo = data!!.getSerializableExtra("profil") as ProfilListeToDo
                this.profil = returnData
            }
        }
        Log.i(CAT, this.profil.toString())
    }

    // pass data to MainActivity when press "back"
    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("profil", profil)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

}
