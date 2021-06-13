package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class GetLists(
    @SerializedName("success")
    val success: String,
    @SerializedName("lists")
    val lists: List<OneList>,
)


data class OneList(
    @SerializedName("id")
    val id: String,
    @SerializedName("label")
    val label: String
)