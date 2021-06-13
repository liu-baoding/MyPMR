package com.example.todolist.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.DataProvider
import com.example.todolist.data.DbDataProvider
import com.example.todolist.data.model.ListDb
import com.example.todolist.data.model.OneList
import com.example.todolist.ui.adapter.NewListAdapter
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

    private var mode = false

    private lateinit var dbDataProvider: DbDataProvider

    private lateinit var adapter: NewListAdapter

    private val viewModel by viewModels<ListViewModel>()

    // coroutine
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)
        showProgress(false)
        pseudo = intent.getStringExtra("pseudo").toString()
        Log.i(CAT, "create")

        this.sp = PreferenceManager.getDefaultSharedPreferences(this)
        this.token = this.sp.getString("edtToken", "defString").toString()

        this.mode = intent.getBooleanExtra("mode", false)
        // for test use
//        this.mode = false

        dbDataProvider = DbDataProvider(application)

        setUpPageAdapter()

        if (mode) {
            showContentOnline()
        } else {
            showContentOffline()
        }
    }

    // get lists from API
    private fun loadLists() {
        viewModel.loadLists(token, pseudo)
        viewModel.datas.observe(this, { viewState ->
            when (viewState) {
                is ListViewModel.ViewState.Content -> {
                    adapter.addAllData(viewState.posts)
                    showProgress(false)
                }
                ListViewModel.ViewState.Loading -> showProgress(true)
                is ListViewModel.ViewState.Error -> {
                    showProgress(false)
                    ToastUtil.newToast(this@ChoixListActivity, viewState.message)
                }
            }
        })
    }

    // get lists from db
    private fun loadListsDb() {
        viewModel.loadListsDb(pseudo)
        viewModel.datas.observe(this, { viewState ->
            when (viewState) {
                is ListViewModel.ViewState.Content -> {
                    adapter.addAllData(viewState.posts)
                    showProgress(false)
                }
                ListViewModel.ViewState.Loading -> showProgress(true)
                is ListViewModel.ViewState.Error -> {
                    showProgress(false)
                    ToastUtil.newToast(this@ChoixListActivity, viewState.message)
                }
            }
        })
    }

    // add a new list to API
    private fun addList(label: String, token: String) {
        activityScope.launch {
            showProgress(true)
            try {
                val returnList = DataProvider.addList(label, token).aList
                this@ChoixListActivity.adapter.addData(returnList)
                // convert data model and add to db
                val newList = convertApiToDb(returnList)
                dbDataProvider.addNewList(newList)
                Log.i(CAT, "Add to db: $newList")
            } catch (e: Exception) {
                ToastUtil.newToast(this@ChoixListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD DONE")
            }
            showProgress(false)
        }
    }

    // convert the list model in API to list model in db
    private fun convertApiToDb(oldList: OneList): ListDb {
        return ListDb(oldList.id, oldList.label, this.pseudo)
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

    // display lists in mode online
    private fun showContentOnline() {
        // load lists from API
        loadLists()

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

    // display lists in mode offline
    private fun showContentOffline() {
        // deactivate the two views
        etNewList.isEnabled = false
        btnOKList.isEnabled = false

        // load lists from db
        loadListsDb()
//        etNewList.setOnClickListener {
//            ToastUtil.newToast(context, "Add a Todo list")
//        }
//
//        btnOKList.setOnClickListener {
//            val newListName = etNewList.text.toString()
//            if (newListName == "") {
//                ToastUtil.newToast(context, "Please enter the name of list")
//            } else {
//                ToastUtil.newToast(context, "Add \"$newListName\"")
//
//                // add new list
//                addList(newListName, this.token)
//
//                etNewList.setText("") //clear the input area
//            }
//            ToastUtil.newToast(context, "Add new list function not available offline.")
//        }
    }

    // set up page and adapter
    private fun setUpPageAdapter() {
        val recyclerView = findViewById<RecyclerView>(R.id.reViewList)

        adapter = NewListAdapter(lists)

        // start ShowListActivity
        val intent = Intent(this, ShowListActivity::class.java)

        // action when click on the list
        adapter.setOnItemClickListener(object : NewListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val listName = lists[position].label
                val listId = lists[position].id
//                ToastUtil.newToast(context, "this is $listName")
                // pass the clicked list
                intent.putExtra("listName", listName)
                intent.putExtra("listId", listId)
                intent.putExtra("mode", mode)
                startActivity(intent)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        this.title = "$pseudo's Todo-lists" // title of this page
    }

    // display progress bar when loading data
    private fun showProgress(show: Boolean) {
        val progress = progress
        val list = reViewList
        progress.isVisible = show
        list.isVisible = !show
    }
}
