package com.example.todolist.data

import android.app.Application
import com.example.todolist.data.model.ItemDb
import com.example.todolist.data.model.ListDb
import com.example.todolist.data.model.OneItem
import com.example.todolist.data.model.OneList

class DataRepository(
    private val dbDataProvider: DbDataProvider,
) {
    suspend fun getLists(token: String, userName: String): List<OneList> {
        return DataProvider.getLists(token).lists.also {
            val newLists = mutableListOf<ListDb>()
            it.forEach {
                newLists.add(ListDb(it.id, it.label, userName))
            }
            dbDataProvider.addNewLists(newLists)
        }
    }

    suspend fun getListsDb(userName: String): List<OneList> {
        val newLists = mutableListOf<OneList>()
        dbDataProvider.getListsByUserName(userName)[0].lists.forEach {
            newLists.add(OneList(it.listId, it.label))
        }
        return newLists
    }

    suspend fun getItems(token: String, listId: String): List<OneItem> {
        return DataProvider.getItems(listId, token).lists.also {
            val newItems = mutableListOf<ItemDb>()
            it.forEach {
                newItems.add(ItemDb(it.id, it.label, it.url, it.checkedStr, listId))
            }
            dbDataProvider.addUpdateNewItems(newItems)
        }
    }

    suspend fun getItemsDb(listId: String): List<OneItem> {
        val newItems = mutableListOf<OneItem>()
        dbDataProvider.getItemsByListId(listId)[0].items.forEach {
            newItems.add(OneItem(it.itemId, it.label, it.url, it.checked))
        }
        return newItems
    }

    companion object {
        fun newInstance(application: Application): DataRepository {
            return DataRepository(
                dbDataProvider = DbDataProvider(application),
            )
        }
    }
}