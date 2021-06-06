package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class GetAddList(
    @SerializedName("success")
    val success: String,
    @SerializedName("list")
    val aList: OneList,
)