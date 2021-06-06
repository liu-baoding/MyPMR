package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class GetAddCheckItem(
    @SerializedName("success")
    val success: String,
    @SerializedName("item")
    val aItem: OneItem,
)