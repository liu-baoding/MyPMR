package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class GetAuthenticate(
    @SerializedName("success")
    val success: String,
    @SerializedName("hash")
    val token: String,
)