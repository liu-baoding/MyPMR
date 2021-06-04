package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class demo(
    @SerializedName("success")
    val success: String,
    @SerializedName("lists")
    val lists: List<list>,
)

data class list(
    @SerializedName("id")
    val id: String,
    @SerializedName("label")
    val label: String
)
