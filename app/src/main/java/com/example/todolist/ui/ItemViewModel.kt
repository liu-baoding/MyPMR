package com.example.todolist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.DataRepository
import com.example.todolist.data.model.OneItem
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository by lazy { DataRepository.newInstance(application) }

    val datas = MutableLiveData<ViewState>()

    fun loadItems(token: String, listId: String) {
        viewModelScope.launch {
            datas.value = ViewState.Loading
            try {
                datas.value = ViewState.Content(posts = dataRepository.getItems(token, listId))
            } catch (e: Exception) {
                datas.value = ViewState.Error(e.message.orEmpty())
            }
        }
    }

    fun loadItemsDb(listId: String) {
        viewModelScope.launch {
            datas.value = ViewState.Loading
            try {
                datas.value = ViewState.Content(posts = dataRepository.getItemsDb(listId))
            } catch (e: Exception) {
                datas.value = ViewState.Error(e.message.orEmpty())
            }
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        data class Content(val posts: List<OneItem>) : ViewState()
        data class Error(val message: String) : ViewState()
    }
}