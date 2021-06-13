package com.example.todolist.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.todolist.data.model.UserWithLists

@Dao
interface UserWithListsDao {
    // query lists by the signed in user's name
    @Transaction
    @Query("SELECT * FROM users WHERE pseudo = :pseudo")
    suspend fun getListsByUserName(pseudo: String): List<UserWithLists>
}