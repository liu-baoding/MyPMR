package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemDb(
    @PrimaryKey
    val itemId: String,
    val label: String,
    val url: String?,
    val checked: String,
    val listContainerId: String
)