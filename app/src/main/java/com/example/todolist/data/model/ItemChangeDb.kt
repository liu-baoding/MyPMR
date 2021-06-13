package com.example.todolist.data.model

import androidx.room.Entity

@Entity(tableName = "item_changes", primaryKeys = arrayOf("itemId", "operation"))
data class ItemChangeDb(
    val itemId: String,
    val label: String,
    val url: String?,
    val checked: String,
    val listContainerId: String,
    val operation: String,
    val token: String
)