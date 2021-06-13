package com.example.todolist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.todolist.data.model.ItemDb

@Dao
interface ItemDao {
    // add new item or update item in db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUpdateNewItem(newItemDb: ItemDb)

    // add new items or update items in db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUpdateNewItems(newItemsDb: List<ItemDb>)
}