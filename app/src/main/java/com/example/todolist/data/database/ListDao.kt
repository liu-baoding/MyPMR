package com.example.todolist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.todolist.data.model.ListDb

@Dao
interface ListDao {
    // add new list in db
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewList(newListDb: ListDb)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewLists(newListsDb: List<ListDb>)
}