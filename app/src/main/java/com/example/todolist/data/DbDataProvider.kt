package com.example.todolist.data

import android.app.Application
import androidx.room.Room
import com.example.todolist.data.database.TodoRoomDatabase
import com.example.todolist.data.model.ItemChangeDb
import com.example.todolist.data.model.ItemDb
import com.example.todolist.data.model.ListDb
import com.example.todolist.data.model.UserDb

class DbDataProvider(
    application: Application
) {
    private val roomDatabase =
        Room.databaseBuilder(application, TodoRoomDatabase::class.java, "todo-room-database")
            .build()

    private val itemDao = roomDatabase.itemDao()
    private val listDao = roomDatabase.listDao()
    private val listWithItemsDao = roomDatabase.listWithItemsDao()
    private val userDao = roomDatabase.userDao()
    private val userWithListsDao = roomDatabase.userWithListsDao()
    private val itemChangesDao = roomDatabase.itemChangesDao()

    suspend fun getAllUsers() = userDao.geAllUsers()

    suspend fun getUserByPseudo(pseudo: String) = userDao.getUserByPseudo(pseudo)

    suspend fun addNewUser(newUserDb: UserDb) = userDao.addNewUser(newUserDb)

    suspend fun addNewList(newListDb: ListDb) = listDao.addNewList(newListDb)

    suspend fun addNewLists(newListsDb: List<ListDb>) = listDao.addNewLists(newListsDb)

    suspend fun getListsByUserName(pseudo: String) = userWithListsDao.getListsByUserName(pseudo)

    suspend fun addUpdateNewItem(newItemDb: ItemDb) = itemDao.addUpdateNewItem(newItemDb)

    suspend fun addUpdateNewItems(newItemsDb: List<ItemDb>) = itemDao.addUpdateNewItems(newItemsDb)

    suspend fun getItemsByListId(listId: String) = listWithItemsDao.getItemsByListId(listId)

    suspend fun addItemChangesDb(itemChangesDb: List<ItemChangeDb>) =
        itemChangesDao.addItemChangesDb(itemChangesDb)

    suspend fun getAllItemChanges() = itemChangesDao.getAllItemChanges()

    suspend fun deleteItemRecords(operationType: String, token: String) =
        itemChangesDao.deleteItemRecords(
            operationType, token
        )

    suspend fun deleteItemRecord(itemId: String) = itemChangesDao.deleteItemRecord(itemId)

}