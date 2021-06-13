package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="lists")
data class ListDb(
    @PrimaryKey
    val listId: String,
    val label: String,
    val userName: String
)