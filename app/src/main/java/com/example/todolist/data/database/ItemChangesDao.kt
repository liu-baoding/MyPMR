package com.example.todolist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todolist.data.model.ItemChangeDb

@Dao
interface ItemChangesDao {
    // add a record of change
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemChangesDb(itemChanges: List<ItemChangeDb>)

    @Query("SELECT * FROM item_changes")
    suspend fun getAllItemChanges(): List<ItemChangeDb>

    @Query("DELETE FROM item_changes WHERE operation = :operationType AND token = :token")
    suspend fun deleteItemRecords(operationType: String, token: String)

    @Query("DELETE FROM item_changes WHERE itemId = :itemId")
    suspend fun deleteItemRecord(itemId: String)
}