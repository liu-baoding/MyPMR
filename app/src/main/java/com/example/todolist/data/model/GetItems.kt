package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class GetItems(
    @SerializedName("success")
    val success: String,
    @SerializedName("items")
    val lists: List<OneItem>,
)

data class OneItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("url")
    val url: String?,
    @SerializedName("checked")
    var checkedStr: String,
)