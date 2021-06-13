package com.example.todolist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.DataRepository
import com.example.todolist.data.model.OneList
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository by lazy { DataRepository.newInstance(application) }

    val datas = MutableLiveData<ViewState>()

    fun loadLists(token: String, userName: String) {
        viewModelScope.launch {
            datas.value = ViewState.Loading
            try {
                datas.value = ViewState.Content(posts = dataRepository.getLists(token, userName))
            } catch (e: Exception) {
                datas.value = ViewState.Error(e.message.orEmpty())
            }
        }
    }

    fun loadListsDb(userName: String) {
        viewModelScope.launch {
            datas.value = ViewState.Loading
            try {
                datas.value = ViewState.Content(posts = dataRepository.getListsDb(userName))
            } catch (e: Exception) {
                datas.value = ViewState.Error(e.message.orEmpty())
            }
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        data class Content(val posts: List<OneList>) : ViewState()
        data class Error(val message: String) : ViewState()
    }
}