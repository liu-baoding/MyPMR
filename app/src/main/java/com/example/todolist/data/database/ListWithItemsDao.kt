package com.example.todolist.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.todolist.data.model.ListWithItems

@Dao
interface ListWithItemsDao {
    // query items with clicked list id
    @Transaction
    @Query("SELECT * FROM lists WHERE listId = :listId")
    suspend fun getItemsByListId(listId: String): List<ListWithItems>
}