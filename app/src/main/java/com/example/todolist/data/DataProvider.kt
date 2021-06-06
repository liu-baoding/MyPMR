package com.example.todolist.data

import com.example.todolist.data.api.ToDoService
import com.example.todolist.data.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataProvider {


    private val BASE_URL = "http://tomnab.fr/todo-api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val service = retrofit.create(ToDoService::class.java)


    // load lists
    suspend fun getLists(token: String): GetLists {
        return service.getLists(token)
    }

    // sign in and get hash value
    suspend fun signIn(login: String, password: String): GetAuthenticate {
        return service.signIn(login, password)
    }

    // load items
    suspend fun getItems(listId: String, token: String): GetItems {
        return service.getItems(listId.toInt(), token)
    }

    // add a new list
    suspend fun addList(label: String, token: String): GetAddList {
        // replace blank by %20
        label.replace(" ", "%20")
        return service.addList(label, token)
    }

    // add a new item
    suspend fun addItem(listId: String, label: String, token: String): GetAddCheckItem {
        // replace blank by %20
        label.replace(" ", "%20")
        return service.addItem(listId.toInt(), label, token)
    }

    // check or uncheck a item
    suspend fun changeItem(
        listId: String,
        itemId: String,
        check: String,
        token: String
    ): GetAddCheckItem {
        return service.changeItem(listId.toInt(), itemId.toInt(), check, token)
    }

}