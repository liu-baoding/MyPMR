package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class PostsResponse(
    @SerializedName("posts")
    val posts: List<Post>
)