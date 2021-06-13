package com.example.todolist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.data.model.ItemChangeDb
import com.example.todolist.data.model.ItemDb
import com.example.todolist.data.model.ListDb
import com.example.todolist.data.model.UserDb

@Database(
    entities = [
        ItemDb::class,
        ListDb::class,
        UserDb::class,
        ItemChangeDb::class
    ],
    version = 1
)
abstract class TodoRoomDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun listDao(): ListDao
    abstract fun listWithItemsDao(): ListWithItemsDao
    abstract fun userDao(): UserDao
    abstract fun userWithListsDao(): UserWithListsDao
    abstract fun itemChangesDao(): ItemChangesDao
}