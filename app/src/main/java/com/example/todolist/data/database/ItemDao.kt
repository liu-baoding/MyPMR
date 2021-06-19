package com.example.todolist.data.database

import androidx.room.*
import com.example.todolist.data.model.ItemDb

@Dao
interface ItemDao {
    // add new item or update item in db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUpdateNewItem(newItemDb: ItemDb)

    // add new items or update items in db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUpdateNewItems(newItemsDb: List<ItemDb>)

    // delete items
    @Query("DELETE FROM items WHERE itemId = :itemId")
    suspend fun deleteItemById(itemId: String)
}