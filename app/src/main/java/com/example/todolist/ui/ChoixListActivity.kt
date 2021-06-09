package com.example.todolist.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.ui.adapter.NewListAdapter
import com.example.todolist.R
import com.example.todolist.data.DataProvider
import com.example.todolist.data.model.OneList
import kotlinx.android.synthetic.main.activity_choix_list.*
import kotlinx.coroutines.*


@Suppress("DEPRECATION")
class ChoixListActivity : AppCompatActivity() {
    private val CAT: String = "TODO_LIST"
    val context = this
    private lateinit var pseudo: String
    private lateinit var sp: SharedPreferences
    private lateinit var token: String
    private var lists: MutableList<OneList> = mutableListOf()

    private lateinit var adapter: NewListAdapter

    // coroutine
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)
        pseudo = intent.getStringExtra("pseudo").toString()
        Log.i(CAT, "create")

        this.sp = PreferenceManager.getDefaultSharedPreferences(this)
        this.token = this.sp.getString("edtToken", "defString").toString()

        showContent()
    }

    // get lists from API
    private fun loadLists() {
        activityScope.launch {
            showProgress(true)
            try {
                val lists = DataProvider.getLists(token).lists
                for (list in lists) {
                    this@ChoixListActivity.adapter.addData(list)
                }
                Log.i(CAT, lists.toString())
            } catch (e: Exception) {
                ToastUtil.newToast(this@ChoixListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "LOAD DONE")
            }
//            delay(5000L)
            showProgress(false)
        }
    }

    // add a new list to API
    private fun addList(label: String, token: String) {
        activityScope.launch {
            showProgress(true)
            try {
                val returnList = DataProvider.addList(label, token).aList
                this@ChoixListActivity.adapter.addData(returnList)
                Log.i(CAT, returnList.toString())
            } catch (e: Exception) {
                ToastUtil.newToast(this@ChoixListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD DONE")
            }
            showProgress(false)
        }
    }


    override fun onStart() {
        super.onStart()
        Log.i(CAT, "start")
    }

    override fun onResume() {
        super.onResume()
        Log.i(CAT, "resume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(CAT, "Restart")
    }

    // display lists
    private fun showContent() {
        val recyclerView = findViewById<RecyclerView>(R.id.reViewList)

//        // for test use
//        this.lists.add(OneList("0", "test"))

        adapter = NewListAdapter(lists)

        loadLists()

        // start ShowListActivity
        val intent = Intent(this, ShowListActivity::class.java)

        // action when click on the list
        adapter.setOnItemClickListener(object : NewListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val listName = lists[position].label
                val listId = lists[position].id
                ToastUtil.newToast(context, "this is $listName")
                // pass the clicked list
                intent.putExtra("listName", listName)
                intent.putExtra("listId", listId)
                startActivity(intent)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        this.title = "$pseudo's Todo-lists" // title of this page

        etNewList.setOnClickListener {
            ToastUtil.newToast(context, "Add a Todo list")
        }

        btnOKList.setOnClickListener {
            val newListName = etNewList.text.toString()
            if (newListName == "") {
                ToastUtil.newToast(context, "Please enter the name of list")
            } else {
                ToastUtil.newToast(context, "Add \"$newListName\"")

                // add new list
                addList(newListName, this.token)

                etNewList.setText("") //clear the input area
            }
        }
    }

    // display progress bar when loading data
    private fun showProgress(show: Boolean) {
        val progress = progress
        val list = reViewList
        progress.isVisible = show
        list.isVisible = !show
    }
}
