package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="users")
data class UserDb(
    @PrimaryKey
    val pseudo: String,
    val password: String,
    val token: String
)