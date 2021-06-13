package com.example.todolist.ui

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
import com.example.todolist.data.model.ItemChangeDb
import com.example.todolist.data.model.ItemDb
import com.example.todolist.data.model.OneItem
import com.example.todolist.ui.adapter.NewItemAdapter
import kotlinx.android.synthetic.main.activity_choix_list.*
import kotlinx.android.synthetic.main.activity_show_list.*
import kotlinx.coroutines.*


@Suppress("DEPRECATION")
class ShowListActivity : AppCompatActivity() {
    private val CAT: String = "TODO_ITEM"
    val context = this
    private var items: MutableList<OneItem> = mutableListOf()

    private var mode = false

    private lateinit var dbDataProvider: DbDataProvider

    private lateinit var listId: String
    private lateinit var adapter: NewItemAdapter
    private lateinit var sp: SharedPreferences
    private lateinit var token: String
    private val TRUE = "1"
    private val FALSE = "0"
    private var oldMap: HashMap<Int, Boolean> = HashMap()

    private val viewModel by viewModels<ItemViewModel>()

    // indicate the indices of changes and work as the id of them
    private var recordIndices = 0

    // coroutine
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)
        showProgress(false)
        this.sp = PreferenceManager.getDefaultSharedPreferences(this)
        this.token = this.sp.getString("edtToken", "defString").toString()

        this.listId = intent.getStringExtra("listId").toString()

        this.mode = intent.getBooleanExtra("mode", false)
        // for test use
//        this.mode = false

        this.dbDataProvider = DbDataProvider(application)

        setUpPageAdapter()

        // get items from API or db according to the mode
        if (mode) {
            loadItems()
        } else {
            loadItemsDb()
        }

        btnOKItem.setOnClickListener {
            val newItemName = etNewItem.text.toString()
            if (newItemName == "") {
                ToastUtil.newToast(context, "Please enter the name of item")
            } else {
                ToastUtil.newToast(context, "Add \"$newItemName\"")

                // add new item
                if (mode) {
                    addItem(this.listId, newItemName, this.token)
                } else {
                    addItemOffline(this.listId, newItemName, this.token)
                }
                etNewItem.setText("") // clear the input area
            }
        }
    }

    private fun setUpPageAdapter() {
        val recyclerView = findViewById<RecyclerView>(R.id.reViewItem)
        val listName = intent.getStringExtra("listName")

        this.title = "Items of \"$listName\" Todo-list"

        this.adapter = NewItemAdapter(this.items)

        // action when checked change on the item
        this.adapter.setOnCheckedChangeListener(object : NewItemAdapter.OnCheckedChangeListener {
            override fun onCheckedChange(position: Int, isChecked: Boolean) {
                val itemName = items[position].label
                // change status of item
                if (isChecked) {
                    ToastUtil.newToast(context, "$itemName has been done")
                } else {
                    ToastUtil.newToast(context, "$itemName to do")
                }
            }
        })

        recyclerView.adapter = this.adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    // copy the adapter.checkStatus to the oldMap
    private fun copyOldMap() {
        Log.i(CAT, "checkStatus: ${adapter.checkStatus}")
        adapter.checkStatus.forEach {
            oldMap[it.key] = it.value
        }
        Log.i(CAT, "oldMap: $oldMap")
    }

    // get data from API
    private fun loadItems() {
        runBlocking {
            viewModel.loadItems(token, listId)
        }
        viewModel.datas.observe(this, { viewState ->
            when (viewState) {
                is ItemViewModel.ViewState.Content -> {
                    adapter.addAllData(viewState.posts)
                    copyOldMap()
                    showProgress(false)
                }
                ItemViewModel.ViewState.Loading -> showProgress(true)
                is ItemViewModel.ViewState.Error -> {
                    showProgress(false)
                    ToastUtil.newToast(this@ShowListActivity, viewState.message)
                }
            }
        })

    }

    // get data from db
    private fun loadItemsDb() {
        runBlocking {
            viewModel.loadItemsDb(listId)
        }
        viewModel.datas.observe(this, { viewState ->
            when (viewState) {
                is ItemViewModel.ViewState.Content -> {
                    adapter.addAllData(viewState.posts)
                    copyOldMap()
                    showProgress(false)
                }
                ItemViewModel.ViewState.Loading -> showProgress(true)
                is ItemViewModel.ViewState.Error -> {
                    showProgress(false)
                    ToastUtil.newToast(this@ShowListActivity, viewState.message)
                }
            }
        })
    }

    // add a new item to API
    private fun addItem(listId: String, label: String, token: String) {
        activityScope.launch {
            showProgress(true)
            try {
                // add in API
                val returnItem = DataProvider.addItem(listId, label, token).aItem
                this@ShowListActivity.adapter.addData(returnItem)
                // convert data model and add to db
                dbDataProvider.addUpdateNewItem(convertApiToDb(returnItem))
                Log.i(CAT, returnItem.toString())
            } catch (e: Exception) {
                ToastUtil.newToast(this@ShowListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD DONE: $oldMap")
            }
            showProgress(false)
        }
    }

    private fun addItemOffline(listId: String, label: String, token: String) {
        activityScope.launch {
            showProgress(true)
            try {
                // when offline, we take "new_" followed with time for the id of new added item
                OneItem("new_" + getCurrentTimeStamp() + "_$recordIndices", label, null, "0").also {
                    // display it
                    this@ShowListActivity.adapter.addData(it)
                    // add it in db
                    dbDataProvider.addUpdateNewItem(convertApiToDb(it))
                    // add it in change db
                    dbDataProvider.addItemChangesDb(
                        listOf(
                            ItemChangeDb(
                                it.id,
                                it.label,
                                it.url,
                                it.checkedStr,
                                listId,
                                "add",
                                token
                            )
                        )
                    )
                }
                recordIndices += 1
            } catch (e: Exception) {
                ToastUtil.newToast(this@ShowListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD offline DONE: $oldMap")
            }
            showProgress(false)
        }
    }

    private fun getCurrentTimeStamp(): String {
        val time = System.currentTimeMillis()
        return (time / 1000).toString()
    }

    // change item status
    private fun changeItem(listId: String, itemId: String, check: String, token: String) {
        activityScope.launch {
            try {
                val returnItem = DataProvider.changeItem(listId, itemId, check, token).aItem
                Log.i(CAT, returnItem.toString())
                // convert data model and update in db
                dbDataProvider.addUpdateNewItem(convertApiToDb(returnItem))
            } catch (e: Exception) {
//                ToastUtil.newToast(this@ShowListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD DONE")
            }
        }
    }

    // convert item model in API to item model in db
    private fun convertApiToDb(oldItem: OneItem): ItemDb {
        return ItemDb(oldItem.id, oldItem.label, oldItem.url, oldItem.checkedStr, listId)
    }

    // display progress bar when loading data
    private fun showProgress(show: Boolean) {
        val progress = progressItem
        val list = reViewItem
        progress.isVisible = show
        list.isVisible = !show
    }

    // when online, write all changes in a time
    private fun writeChanges() {
        activityScope.launch {
            // compare new status map with old status map
            adapter.checkStatus.forEach {
                try {
                    if (oldMap.containsKey(it.key)) {
                        // for old items
                        if (oldMap[it.key] != it.value) {
                            // when the status changed
                            val itemId = adapter.getDataSet()[it.key].id
                            changeItem(
                                this@ShowListActivity.listId,
                                itemId,
                                if (it.value) TRUE else FALSE,
                                token
                            )
                        }
                    } else {
                        // for new added items
                        if (it.value) {
                            // when status is true, false is the default value and no need to apply
                            val itemId = adapter.getDataSet()[it.key].id
                            changeItem(
                                this@ShowListActivity.listId,
                                itemId,
                                TRUE,
                                token
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.i(CAT, "$it: $e")
                }
            }
            Log.i(CAT, "write done")
        }
    }

    // when offline, write all changes in a time in db
    private fun writeChangesDb() {
        activityScope.launch {
            // list of changed items
            val newItemsDb = mutableListOf<ItemDb>()
            val newItemChanges = mutableListOf<ItemChangeDb>()
            for ((key, value) in adapter.checkStatus) {
                try {
                    if (oldMap.containsKey(key)) {
                        if (oldMap[key] != value) {
                            // old added and changed items
                            newItemsDb.add(
                                ItemDb(
                                    // same id to replace data in db
                                    adapter.getDataSet()[key].id,
                                    adapter.getDataSet()[key].label,
                                    adapter.getDataSet()[key].url,
                                    if (value) TRUE else FALSE,
                                    this@ShowListActivity.listId
                                )
                            )
                            newItemChanges.add(
                                ItemChangeDb(
                                    // same id to replace data in db
                                    adapter.getDataSet()[key].id,
                                    adapter.getDataSet()[key].label,
                                    adapter.getDataSet()[key].url,
                                    if (value) TRUE else FALSE,
                                    this@ShowListActivity.listId,
                                    "change",
                                    token
                                )
                            )
                        }
                    } else {
                        if (value) {
                            // new added and not the default status
                            newItemsDb.add(
                                ItemDb(
                                    // same id to replace data in db
                                    adapter.getDataSet()[key].id,
                                    adapter.getDataSet()[key].label,
                                    adapter.getDataSet()[key].url,
                                    TRUE,
                                    this@ShowListActivity.listId
                                )
                            )
                            newItemChanges.add(
                                ItemChangeDb(
                                    // different id to garde 2 records of modification
                                    adapter.getDataSet()[key].id + "_change",
                                    adapter.getDataSet()[key].label,
                                    adapter.getDataSet()[key].url,
                                    if (value) TRUE else FALSE,
                                    this@ShowListActivity.listId,
                                    "change",
                                    token
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.i(CAT, e.toString())
                }
            }
            // apply changes
            dbDataProvider.addUpdateNewItems(newItemsDb)
            dbDataProvider.addItemChangesDb(newItemChanges)
        }
    }

    // write all changes in a time when press back button
    override fun onBackPressed() {
        super.onBackPressed()
        // write in API when online, in db when offline
        if (mode) {
            writeChanges()
        } else {
            writeChangesDb()
        }
    }
}
