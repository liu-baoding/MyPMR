package com.example.todolist.data

import com.example.todolist.data.api.ToDoService
import com.example.todolist.data.model.Authenticate
import com.example.todolist.data.model.Post
import com.example.todolist.data.model.list
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataProvider {


    private val BASE_URL = "http://tomnab.fr/todo-api/lists/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val service = retrofit.create(ToDoService::class.java)


    suspend fun getPostFromApi(): List<list> {
        return service.getPosts().lists
    }

    suspend fun signIn(login: String, password: String): Authenticate{
        return service.getToken(login, password)
    }

}