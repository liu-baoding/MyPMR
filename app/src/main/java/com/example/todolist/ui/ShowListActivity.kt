package com.example.todolist.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.ui.adapter.NewItemAdapter
import com.example.todolist.R
import com.example.todolist.data.DataProvider
import com.example.todolist.data.model.OneItem
import kotlinx.android.synthetic.main.activity_choix_list.*
import kotlinx.android.synthetic.main.activity_show_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class ShowListActivity : AppCompatActivity() {
    private val CAT: String = "TODO_ITEM"
    val context = this
    private var items: MutableList<OneItem> = mutableListOf()
    private lateinit var listId: String
    private lateinit var adapter: NewItemAdapter
    private lateinit var sp: SharedPreferences
    private lateinit var token: String
    private val TRUE = "1"
    private val FALSE = "0"
    private val oldMap: HashMap<Int, Boolean> = HashMap()
    private val newMap: HashMap<Int, Boolean> = HashMap()

    // coroutine
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)
        this.sp = PreferenceManager.getDefaultSharedPreferences(this)
        this.token = this.sp.getString("edtToken", "defString").toString()

        val listName = intent.getStringExtra("listName")
        listId = intent.getStringExtra("listId").toString()

        val recyclerView = findViewById<RecyclerView>(R.id.reViewItem)

        this.title = "Items of \"$listName\" Todo-list"

//        // test use
//        this.items.add(OneItem("0", "test", null, FALSE))

        this.adapter = NewItemAdapter(this.items)

        // action when checked change on the item
        this.adapter.setOnCheckedChangeListener(object : NewItemAdapter.OnCheckedChangeListener {
            override fun onCheckedChange(position: Int, isChecked: Boolean) {
                val itemName = items[position].label
                val itemId = items[position].id
                // change in API
                if (isChecked) {
//                    changeItem(this@ShowListActivity.listId, itemId, TRUE, token)
                    ToastUtil.newToast(context, "$itemName has been done")
                } else {
//                    changeItem(this@ShowListActivity.listId, itemId, FALSE, token)
                    ToastUtil.newToast(context, "$itemName to do")
                }
            }
        })

        recyclerView.adapter = this.adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // get items from API
        loadItems()

        etNewItem.setOnClickListener {
            ToastUtil.newToast(context, "Add a Todo item")
        }

        btnOKItem.setOnClickListener {
            val newItemName = etNewItem.text.toString()
            if (newItemName == null || newItemName == "") {
                ToastUtil.newToast(context, "Please enter the name of item")
            } else {
                ToastUtil.newToast(context, "Add \"$newItemName\"")

                // add new item
                addItem(this.listId, newItemName, this.token)

                etNewItem.setText("") // clear the input area
            }
        }
    }

    // get data from API
    private fun loadItems() {
        activityScope.launch {
            showProgress(true)
            try {
                Log.i(CAT, this@ShowListActivity.listId)
                Log.i(CAT, this@ShowListActivity.token)
                val items = DataProvider.getItems(
                    this@ShowListActivity.listId,
                    this@ShowListActivity.token
                ).lists
                Log.i(CAT, items.toString())
                for (item in items) {
                    this@ShowListActivity.adapter.addData(item)
                }
                Log.i(CAT, items.toString())
            } catch (e: Exception) {
                ToastUtil.newToast(this@ShowListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "LOAD DONE")
                // store the old item status map
                for ((key, value) in adapter.checkStatus) {
                    this@ShowListActivity.oldMap[key] = value
                }
            }
            showProgress(false)
        }
    }

    // add a new item to API
    private fun addItem(listId: String, label: String, token: String) {
        activityScope.launch {
            showProgress(true)
            try {
                val returnItem = DataProvider.addItem(listId, label, token).aItem
                this@ShowListActivity.adapter.addData(returnItem)
                Log.i(CAT, returnItem.toString())
            } catch (e: Exception) {
                ToastUtil.newToast(this@ShowListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD DONE")
            }
            showProgress(false)
        }
    }

    // change item status
    private fun changeItem(listId: String, itemId: String, check: String, token: String) {
        activityScope.launch {
//            showProgress(true)
            try {
                val returnItem = DataProvider.changeItem(listId, itemId, check, token).aItem
                Log.i(CAT, returnItem.toString())
            } catch (e: Exception) {
                ToastUtil.newToast(this@ShowListActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                Log.i(CAT, "ADD DONE")
            }
//            showProgress(false)
        }
    }

    // display progress bar when loading data
    private fun showProgress(show: Boolean) {
        val progress = progressItem
        val list = reViewItem
        progress.isVisible = show
        list.isVisible = !show
    }

    // write all changes in a time when press back button
    override fun onBackPressed() {
        super.onBackPressed()
        activityScope.launch {
            // compare new status map with old status map
            for ((key, value) in adapter.checkStatus) {
                val itemId = adapter.getDataSet()[key].id
                if (oldMap[key] != value) {
                    try {
                        if (value) {
                            changeItem(this@ShowListActivity.listId, itemId, TRUE, token)
                        } else {
                            changeItem(this@ShowListActivity.listId, itemId, FALSE, token)
                        }
                    } catch (e: Exception) {
                        Log.i(CAT, e.toString())
                        if (value) {
                            changeItem(this@ShowListActivity.listId, itemId, TRUE, token)
                        } else {
                            changeItem(this@ShowListActivity.listId, itemId, FALSE, token)
                        }
                    }
                }
            }
        }
    }

}
