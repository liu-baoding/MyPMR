package com.example.todolist.data.api

import com.example.todolist.data.model.Authenticate
import com.example.todolist.data.model.demo
import retrofit2.http.*

interface ToDoService {
    // test use
    @Headers("hash: 65f3b52cefe79ee19a20a59eac5ac8ec")
    @GET("http://tomnab.fr/todo-api/lists")
//    suspend fun getPosts(): PostsResponse
    suspend fun getPosts(): demo

    @POST("http://tomnab.fr/todo-api/authenticate")
    suspend fun getToken(@Query("user")login: String, @Query("password")password: String): Authenticate
}
