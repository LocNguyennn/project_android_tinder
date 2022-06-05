package com.example.chattingapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattingapp.model.User

class ListFriendViewModel : ViewModel() {
    private var _listOfData : MutableLiveData<ArrayList<User>> = MutableLiveData()
    val listOfData : LiveData<ArrayList<User>>
        get() = _listOfData
    fun loadData(listUser : ArrayList<User>){
        _listOfData.postValue(listUser)
    }
}