package com.example.todolist.data.api

import com.example.todolist.data.model.*
import retrofit2.http.*

interface ToDoService {
    // to sign in
    @POST("http://tomnab.fr/todo-api/authenticate")
    suspend fun signIn(
        @Query("user") login: String,
        @Query("password") password: String
    ): GetAuthenticate

    // load lists
    @GET("http://tomnab.fr/todo-api/lists")
    suspend fun getLists(@Query("hash") token: String): GetLists

    // load items
    @GET("http://tomnab.fr/todo-api/lists/{list_id}/items")
    suspend fun getItems(@Path("list_id") listId: Int, @Query("hash") token: String): GetItems

    // add new list
    @POST("http://tomnab.fr/todo-api/lists")
    suspend fun addList(@Query("label") label: String, @Query("hash") token: String): GetAddList

    // add new item
    @POST("http://tomnab.fr/todo-api/lists/{list_id}/items")
    suspend fun addItem(
        @Path("list_id") listId: Int,
        @Query("label") label: String,
        @Query("hash") token: String
    ): GetAddCheckItem

    // change status of a item
    @PUT("http://tomnab.fr/todo-api/lists/{list_id}/items/{item_id}")
    suspend fun changeItem(
        @Path("list_id") listId: Int,
        @Path("item_id") itemId: Int,
        @Query("check") check: String,
        @Query("hash") token: String
    ): GetAddCheckItem
}

