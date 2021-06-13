package com.example.todolist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todolist.data.model.UserDb

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun geAllUsers(): List<UserDb>

    // query by user's name
    @Query("SELECT * FROM users WHERE pseudo = :pseudo")
    suspend fun getUserByPseudo(pseudo: String): List<UserDb>

    // add new user in db when authenticated
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewUser(newUserDb: UserDb)
}