package com.example.todolist.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ListWithItems(
    @Embedded
    val listDb: ListDb,
    @Relation(
        parentColumn = "listId",
        entityColumn = "listContainerId"
    )
    val items: List<ItemDb>
)