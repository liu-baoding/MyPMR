package com.example.todolist.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithLists(
    @Embedded val userDb: UserDb,
    @Relation(
        parentColumn = "pseudo",
        entityColumn = "userName"
    )
    val lists: List<ListDb>
)